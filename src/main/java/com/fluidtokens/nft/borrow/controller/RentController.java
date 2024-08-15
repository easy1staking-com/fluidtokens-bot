package com.fluidtokens.nft.borrow.controller;

import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.repository.UtxoRepository;
import com.fluidtokens.nft.borrow.model.Rent;
import com.fluidtokens.nft.borrow.service.DatumService;
import com.fluidtokens.nft.borrow.service.RentService;
import com.fluidtokens.nft.borrow.util.UtxoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Comparator;

@RestController
@RequestMapping("/extra_rewards")
@RequiredArgsConstructor
@Slf4j
public class RentController {

    record RentUtxo(Rent rent, Utxo utxo) {

    }

    private final RentService rentService;

    private final DatumService datumService;

    private final UtxoRepository utxoRepository;

    @GetMapping
    public ResponseEntity<?> get(@RequestParam(value = "lent_only", required = false, defaultValue = "false") Boolean isLentOnly,
                                 @RequestParam(value = "expired_only", required = false, defaultValue = "false") Boolean isExpiredOnly,
                                 @RequestParam(value = "ada_only", required = false, defaultValue = "false") Boolean isAdaOnly,
                                 @RequestParam(value = "nft_only", required = false, defaultValue = "false") Boolean isNftOnly) {

        var rentsStream = rentService.findAllRents().stream();

        if (isLentOnly) {
            rentsStream = rentsStream.filter(utxoRent -> utxoRent.rent().isLent());
        }

        if (isExpiredOnly) {
            rentsStream = rentsStream.filter(utxoRent -> utxoRent.rent().isExpired());
        }

        var rentUtxosStream = rentsStream.sorted(Comparator.comparing(utxoRent -> utxoRent.rent().deadline()))
                .flatMap(utxoRent -> utxoRepository.findById(new UtxoId(utxoRent.transactionOutput().hash(), utxoRent.transactionOutput().index())).stream())
                .map(UtxoUtil::toUtxo);

        if (isAdaOnly) {
            rentUtxosStream = rentUtxosStream.filter(rentUtxo -> rentUtxo.getAmount().size() == 1);
        }

        if (isNftOnly) {
            rentUtxosStream = rentUtxosStream.filter(rentUtxo -> rentUtxo.getAmount().stream().anyMatch(amount -> amount.getQuantity().equals(BigInteger.ONE)));
        }

        var rentStream = rentUtxosStream.flatMap(utxo -> {
            var datumOpt = datumService.parse(utxo.getInlineDatum());
            return datumOpt.map(datum -> new RentUtxo(datum, utxo)).stream();
        });

        return ResponseEntity.ok(rentStream.toList());
    }

}
