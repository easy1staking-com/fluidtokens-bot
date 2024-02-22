package com.fluidtokens.nft.borrow.service;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.address.AddressProvider;
import com.bloxbean.cardano.client.address.Credential;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.exception.CborDeserializationException;
import com.bloxbean.cardano.client.plutus.spec.BigIntPlutusData;
import com.bloxbean.cardano.client.plutus.spec.ConstrPlutusData;
import com.bloxbean.cardano.client.plutus.spec.PlutusData;
import com.bloxbean.cardano.client.util.HexUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluidtokens.nft.borrow.model.Rent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

@Service
@Slf4j
public class DatumService {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        return parse(inlineDatum).map(Rent::isExpired).orElse(false);
    }

    public Optional<Rent> parse(String inlineDatum) {
        try {
            var rentDatum = (ConstrPlutusData) PlutusData.deserialize(HexUtil.decodeHexString(inlineDatum));
            var jsonStringDatum = objectMapper.writeValueAsString(rentDatum);

            var jsonDatum = objectMapper.readTree(jsonStringDatum);

            var ownerAddress = extractAddress(jsonDatum.path("fields").get(0));
            var tenantAddress = extractAddress(jsonDatum.path("fields").get(8));

            var dataItems = rentDatum.getData().getPlutusDataList();
            var deadline = ((BigIntPlutusData) dataItems.get(9)).getValue().longValue();

            var loanDeadline = LocalDateTime.ofInstant(Instant.ofEpochMilli(deadline), TimeZone.getDefault().toZoneId());

            return Optional.of(new Rent(ownerAddress, tenantAddress, loanDeadline));

        } catch (Exception e) {
            log.warn(String.format("could not parse datum: %s", inlineDatum), e);
            return Optional.empty();
        }
    }

    private Address extractAddress(JsonNode node) {

        var paymentCredentialsHex = node.path("fields").get(0).path("fields").get(0).path("bytes").asText();
        var paymentCredentials = Credential.fromKey(paymentCredentialsHex);
        Optional<String> stakingCredentialsHexOpt;

        if (!node.path("fields").get(1).path("fields").isEmpty()) {
            var stakingCredentialsHex = node.path("fields").get(1)
                    .path("fields").get(0)
                    .path("fields").get(0)
                    .path("fields").get(0).path("bytes").asText();
            stakingCredentialsHexOpt = Optional.of(stakingCredentialsHex);
        } else {
            stakingCredentialsHexOpt = Optional.empty();
        }

        return stakingCredentialsHexOpt.map(stakingCredentialsHex -> {
            var stakingCredentials = Credential.fromKey(stakingCredentialsHex);
            return AddressProvider.getBaseAddress(paymentCredentials, stakingCredentials, Networks.mainnet());
        }).orElseGet(() -> AddressProvider.getEntAddress(paymentCredentials, Networks.mainnet()));

    }

}
