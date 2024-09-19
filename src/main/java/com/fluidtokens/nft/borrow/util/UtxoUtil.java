package com.fluidtokens.nft.borrow.util;

import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.yaci.store.common.domain.Amt;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.AddressUtxoEntity;

public class UtxoUtil {

    public static Amount toAmount(Amt amount) {
        return Amount.builder()
                .unit(amount.getUnit())
                .quantity(amount.getQuantity())
                .build();
    }

    public static Utxo toUtxo(AddressUtxoEntity addressUtxoEntity) {
        return Utxo.builder()
                .txHash(addressUtxoEntity.getTxHash())
                .outputIndex(addressUtxoEntity.getOutputIndex())
                .address(addressUtxoEntity.getOwnerAddr())
                .amount(addressUtxoEntity.getAmounts().stream().map(UtxoUtil::toAmount).toList())
                .dataHash(addressUtxoEntity.getDataHash())
                .inlineDatum(addressUtxoEntity.getInlineDatum())
                .referenceScriptHash(addressUtxoEntity.getReferenceScriptHash())
                .build();
    }

}
