package com.fluidtokens.nft.borrow.service;

import com.bloxbean.cardano.client.exception.CborDeserializationException;
import com.bloxbean.cardano.client.plutus.spec.BigIntPlutusData;
import com.bloxbean.cardano.client.plutus.spec.BytesPlutusData;
import com.bloxbean.cardano.client.plutus.spec.ConstrPlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusData;
import com.bloxbean.cardano.client.util.HexUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.TimeZone;

@Service
@Slf4j
public class DatumService {

    public Optional<PlutusData> getNewDatum(String inlineDatum) {

        try {
            var rentDatum = (ConstrPlutusData) PlutusData.deserialize(HexUtil.decodeHexString(inlineDatum));
            var dataItems = rentDatum.getData().getPlutusDataList();

            return Optional.of(ConstrPlutusData.of(0,
                    dataItems.get(0),
                    dataItems.get(1),
                    dataItems.get(2),
                    dataItems.get(3),
                    dataItems.get(4),
                    dataItems.get(5),
                    dataItems.get(6),
                    dataItems.get(7),
                    dataItems.get(0),
                    dataItems.get(9),
                    dataItems.get(10),
                    dataItems.get(11),
                    dataItems.get(12),
                    dataItems.get(13),
                    dataItems.get(14)));

        } catch (CborDeserializationException e) {
            log.warn(String.format("could not parse datum: %s", inlineDatum), e);
            return Optional.empty();
        }


    }

    public boolean isLoanExpired(String inlineDatum) {
        try {
            var rentDatum = (ConstrPlutusData) PlutusData.deserialize(HexUtil.decodeHexString(inlineDatum));
            var dataItems = rentDatum.getData().getPlutusDataList();

            var owner = ((BytesPlutusData) dataItems.get(0)).getValue();
            var tenant = ((BytesPlutusData) dataItems.get(8)).getValue();
            var deadline = ((BigIntPlutusData) dataItems.get(9)).getValue().longValue();

            var loanDeadline = LocalDateTime.ofInstant(Instant.ofEpochMilli(deadline), TimeZone.getDefault().toZoneId());

            return !Arrays.equals(owner, tenant) && LocalDateTime.now().isAfter(loanDeadline);

        } catch (CborDeserializationException e) {
            log.warn(String.format("could not parse datum: %s", inlineDatum), e);
            return false;
        }
    }

}
