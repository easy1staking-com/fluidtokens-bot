package com.fluidtokens.nft.borrow.storage;

import com.bloxbean.cardano.yaci.store.common.domain.AddressUtxo;
import com.bloxbean.cardano.yaci.store.common.domain.TxInput;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.UtxoCache;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.UtxoStorageImpl;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.repository.TxInputRepository;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.repository.UtxoRepository;
import com.fluidtokens.nft.borrow.service.FluidtokensRentContractService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class FluidtokensUtxoStorage extends UtxoStorageImpl {

    private final UtxoRepository utxoRepository;

    private final FluidtokensRentContractService fluidtokensRentContractService;

    public FluidtokensUtxoStorage(UtxoRepository utxoRepository,
                                  TxInputRepository spentOutputRepository,
                                  DSLContext dsl,
                                  UtxoCache utxoCache,
                                  FluidtokensRentContractService fluidtokensRentContractService) {
        super(utxoRepository, spentOutputRepository, dsl, utxoCache);
        this.utxoRepository = utxoRepository;
        this.fluidtokensRentContractService = fluidtokensRentContractService;

    }

    @Override
    public void saveUnspent(List<AddressUtxo> addressUtxoList) {

        final var paymentCredentialsBytes = fluidtokensRentContractService.getPaymentCredentialsHex();

        var fluidtokensRentsAddresses = addressUtxoList
                .stream()
                .filter(addressUtxo -> addressUtxo.getOwnerPaymentCredential() != null &&
                        addressUtxo.getOwnerPaymentCredential().equals(paymentCredentialsBytes))
                .toList();

        super.saveUnspent(fluidtokensRentsAddresses);
    }

    @Override
    public void saveSpent(List<TxInput> txInputs) {
        var fluidtokensRentsInputs = txInputs
                .stream()
                .filter(txInput -> utxoRepository.findById(new UtxoId(txInput.getTxHash(), txInput.getOutputIndex())).isPresent())
                .toList();
        super.saveSpent(fluidtokensRentsInputs);
    }

}
