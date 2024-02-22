package com.fluidtokens.nft.borrow.service;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.address.AddressProvider;
import com.bloxbean.cardano.client.address.Credential;
import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.plutus.spec.BigIntPlutusData;
import com.bloxbean.cardano.client.plutus.spec.ConstrPlutusData;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.ScriptTx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluidtokens.nft.borrow.client.FluidtokensApi;
import com.fluidtokens.nft.borrow.model.TransactionOutput;
import com.fluidtokens.nft.borrow.model.UtxoRent;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
//@AllArgsConstructor
public class ReturnNftJob implements Runnable {

    private static final String SCRIPT_REF_INPUT_HASH = "2c812d5ba6d240eea79dca528f22a3854adcaac140f3151ecbcf5d945c5981e3";
    private static final int SCRIPT_REF_INPUT_INDEX = 0;

    @Value("${dryRun}")
    private boolean dryRun;
    @Autowired
    private Account account;
    @Autowired
    private BFBackendService bfBackendService;
    @Autowired
    private FluidtokensApi fluidtokensApi;
    @Autowired
    private DatumService datumService;
    @Autowired
    private RentService rentService;
    @Autowired
    private FluidtokensRentContractService fluidtokensRentContractService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        log.info("Running in dry-run mode: {}", dryRun);
    }

    @SneakyThrows
    @Override
    @Scheduled(timeUnit = TimeUnit.MINUTES, initialDelay = 2, fixedDelay = 15)
    public void run() {

        log.info("Running");

        var actualExpiredRents = rentService.findAllRents()
                .stream()
                .filter(utxoRent -> utxoRent.rent().isExpired())
                .map(UtxoRent::transactionOutput)
                .collect(Collectors.toSet());

        var expiredRents = fluidtokensApi.getExpiredRents();
        if (!expiredRents.isEmpty()) {

            var expectedExpiredLoans = expiredRents.stream().map(rent -> {
                        var utxoParts = rent.rentUtxoId().split("#");
                        var utxoHash = utxoParts[0];
                        var utxoIndex = Integer.parseInt(utxoParts[1]);
                        return new TransactionOutput(utxoHash, utxoIndex);
                    })
                    .collect(Collectors.toSet());

            actualExpiredRents.forEach(loanTO -> log.info("actual loan to: {}", loanTO));
            expectedExpiredLoans.forEach(loanTO -> log.info("expected loan to: {}", loanTO));
            log.info("Equals? {}", actualExpiredRents.equals(expectedExpiredLoans));

            expiredRents.sort(Comparator.comparing(o -> TransactionOutput.fromString(o.rentUtxoId())));

            final ScriptTx scriptTx = new ScriptTx()
                    .readFrom(SCRIPT_REF_INPUT_HASH, SCRIPT_REF_INPUT_INDEX);

            for (int i = 0; i < expiredRents.size(); i++) {
                var rent = expiredRents.get(i);
                final var j = i;

                var rentUtxo = rent.rentUtxoId();
                var utxoParts = rentUtxo.split("#");
                var utxoHash = utxoParts[0];
                var utxoIndex = Integer.parseInt(utxoParts[1]);

                try {
                    Result<Utxo> txOutputResult = bfBackendService.getUtxoService().getTxOutput(utxoHash, utxoIndex);
                    Utxo utxo = txOutputResult.getValue();

                    String inlineDatum = utxo.getInlineDatum();

                    var stakingAddress = rent.rentData().renterAddress().get(1);
                    var delegationCredentials = Credential.fromKey(stakingAddress);

                    var addressOwner = AddressProvider.getBaseAddress(fluidtokensRentContractService.getPaymentCredentials(),
                            delegationCredentials,
                            Networks.mainnet());

                    datumService.getNewDatum(inlineDatum)
                            .ifPresent(newDatum -> {
                                scriptTx.collectFrom(utxo, ConstrPlutusData.of(4, BigIntPlutusData.of(j)));
                                scriptTx.payToContract(addressOwner.getAddress(), utxo.getAmount(), newDatum);
                            });

                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }

            }

            scriptTx.withChangeAddress(account.baseAddress());

            QuickTxBuilder quickTxBuilder = new QuickTxBuilder(bfBackendService);

            var slot = bfBackendService.getBlockService().getLatestBlock().getValue().getSlot();

            var stuff = quickTxBuilder
                    .compose(scriptTx)
                    .mergeOutputs(false)
                    .withSigner(SignerProviders.signerFrom(account))
                    .validFrom(slot - 120L)
                    .validTo(slot + 600L)
                    .feePayer(account.baseAddress());

            if (dryRun) {
                var tx = stuff.build();
                log.info("tx: {}", objectMapper.writeValueAsString(tx));
            } else {
                stuff.completeAndWait();
            }


        }
        log.info("Completed");
    }

}
