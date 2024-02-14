package com.fluidtokens.nft.borrow.service;

import com.bloxbean.cardano.client.api.TransactionEvaluator;
import com.bloxbean.cardano.client.api.exception.ApiException;
import com.bloxbean.cardano.client.api.model.EvaluationResult;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.api.model.Utxo;
import com.bloxbean.cardano.client.plutus.spec.ExUnits;
import com.bloxbean.cardano.client.plutus.spec.RedeemerTag;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class NoOpTransactionEvaluator implements TransactionEvaluator {

    @Override
    public Result<List<EvaluationResult>> evaluateTx(byte[] cbor, Set<Utxo> inputUtxos) throws ApiException {
        EvaluationResult result0 = EvaluationResult.builder()
                .redeemerTag(RedeemerTag.Spend)
                .index(0)
                .exUnits(ExUnits.builder().mem(BigInteger.valueOf(325000L)).steps(BigInteger.valueOf(165000000L)).build())
                .build();
        EvaluationResult result1 = EvaluationResult.builder()
                .redeemerTag(RedeemerTag.Spend)
                .index(1)
                .exUnits(ExUnits.builder().mem(BigInteger.valueOf(250000L)).steps(BigInteger.valueOf(115000000L)).build())
                .build();
        EvaluationResult result2 = EvaluationResult.builder()
                .redeemerTag(RedeemerTag.Spend)
                .index(2)
                .exUnits(ExUnits.builder().mem(BigInteger.valueOf(250000L)).steps(BigInteger.valueOf(115000000L)).build())
                .build();


        return Result.success("saul goodman").withValue(List.of(result0, result1, result2));
    }

}
