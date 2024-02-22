package com.fluidtokens.nft.borrow.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class ExpirationConversionTest {

    @Test
    public void expirationDateConversion() {
        var expirationDate = 1707925577000L;

        LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(expirationDate), TimeZone.getDefault().toZoneId());

        System.out.println(triggerTime.toString());

    }

}
