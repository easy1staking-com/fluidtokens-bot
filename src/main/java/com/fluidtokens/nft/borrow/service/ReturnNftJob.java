package com.fluidtokens.nft.borrow.service;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.address.AddressProvider;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.plutus.spec.BigIntPlutusData;
import com.bloxbean.cardano.client.plutus.spec.ConstrPlutusData;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.ScriptTx;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.repository.UtxoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluidtokens.nft.borrow.model.UtxoRent;
import com.fluidtokens.nft.borrow.util.UtxoUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.fluidtokens.nft.borrow.model.Constants.SCRIPT_REF_INPUT_HASH;
import static com.fluidtokens.nft.borrow.model.Constants.SCRIPT_REF_INPUT_INDEX;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReturnNftJob implements Runnable {

    @Value("${dryRun}")
    private boolean dryRun;

    private final Account account;

    private final BFBackendService bfBackendService;

    private final DatumService datumService;

    private final RentService rentService;

    private final FluidtokensRentContractService fluidtokensRentContractService;

    private final UtxoRepository utxoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        log.info("Running in dry-run mode: {}", dryRun);
    }

    private List<UtxoRent> getExpiredRents() {
        var allRents = rentService.findAllRents();

        var nextLoan = allRents.stream()
                .filter(utxoRent -> utxoRent.rent().isLent())
                .min(Comparator.comparing(utxoRent -> utxoRent.rent().deadline()));

        nextLoan.ifPresent(loan -> log.info("Next loan: {}", loan));

        return allRents
                .stream()
                .filter(utxoRent -> utxoRent.rent().canBeReturned())
                .sorted(Comparator.comparing(UtxoRent::transactionOutput))
                .limit(10)
                .toList();
    }

    @SneakyThrows
    @Override
    @Scheduled(timeUnit = TimeUnit.MINUTES, initialDelay = 2, fixedDelay = 15)
    public void run() {

        log.info("Running");

        List<UtxoRent> expiredRents = getExpiredRents();

        while (!expiredRents.isEmpty()) {

            final ScriptTx scriptTx = new ScriptTx()
                    .readFrom(SCRIPT_REF_INPUT_HASH, SCRIPT_REF_INPUT_INDEX);

            for (int i = 0; i < expiredRents.size(); i++) {
                var rent = expiredRents.get(i);
                final var j = i;

                var utxoHash = rent.transactionOutput().hash();
                var utxoIndex = rent.transactionOutput().index();

                try {

                    var utxoEntityOpt = utxoRepository.findById(new UtxoId(utxoHash, utxoIndex));
                    if (utxoEntityOpt.isEmpty()) {
                        log.warn("could not find utxo: {}:{}", utxoHash, utxoIndex);
                        return;
                    }

                    var utxoEntity = utxoEntityOpt.get();
                    var utxo = UtxoUtil.toUtxo(utxoEntity);

                    var isNftRent = utxo.getAmount().stream().anyMatch(amount -> !amount.getUnit().equals("lovelace") && amount.getQuantity().equals(BigInteger.ONE));
                    log.info("isNftRent: {}", isNftRent);

                    String inlineDatum = utxoEntity.getInlineDatum();

                    var delegationCredentials = rent.rent().owner().getDelegationCredential().get();

                    var addressOwner = AddressProvider.getBaseAddress(fluidtokensRentContractService.getPaymentCredentials(),
                            delegationCredentials,
                            Networks.mainnet());

                    datumService.getNewDatum(inlineDatum)
                            .ifPresent(newDatum -> {
                                scriptTx.collectFrom(utxo, ConstrPlutusData.of(4, BigIntPlutusData.of(j)));
                                scriptTx.payToContract(addressOwner.getAddress(), utxo.getAmount(), newDatum);
                            });

                } catch (Exception e) {
                    log.warn("Error while processing utxo: {}:{}", utxoHash, utxoIndex);
                    throw new RuntimeException(e);
                }

            }

            scriptTx.withChangeAddress(account.baseAddress());

            QuickTxBuilder quickTxBuilder = new QuickTxBuilder(bfBackendService);

            var slot = bfBackendService.getBlockService().getLatestBlock().getValue().getSlot();

            var transaction = quickTxBuilder
                    .compose(scriptTx)
                    .mergeOutputs(false)
                    .withSigner(SignerProviders.signerFrom(account))
                    .validFrom(slot - 120L)
                    .validTo(slot + 600L)
                    .feePayer(account.baseAddress());

            if (dryRun) {
                var tx = transaction.build();
                log.info("tx: {}", objectMapper.writeValueAsString(tx));
            } else {
                transaction.completeAndWait();
            }

            Thread.sleep(30000L);

            expiredRents = getExpiredRents();

        }
        log.info("Completed");
    }

}
