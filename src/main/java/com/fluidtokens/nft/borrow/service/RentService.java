package com.fluidtokens.nft.borrow.service;

import com.bloxbean.cardano.yaci.store.utxo.storage.impl.repository.UtxoRepository;
import com.fluidtokens.nft.borrow.model.TransactionOutput;
import com.fluidtokens.nft.borrow.model.UtxoRent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RentService {

    private final FluidtokensRentContractService fluidtokensRentContractService;

    private final DatumService service;

    private final UtxoRepository utxoRepository;

    public List<UtxoRent> findAllRents() {
        return utxoRepository
                .findUnspentByOwnerPaymentCredential(fluidtokensRentContractService.getPaymentCredentialsHex(), Pageable.unpaged())
                .stream()
                .flatMap(List::stream)
                .flatMap(utxo -> service.parse(utxo.getInlineDatum()).map(rent -> new UtxoRent(new TransactionOutput(utxo.getTxHash(), utxo.getOutputIndex()), rent)).stream())
                .sorted(Comparator.comparing(utxoRent -> utxoRent.rent().deadline()))
                .toList();
    }

}
