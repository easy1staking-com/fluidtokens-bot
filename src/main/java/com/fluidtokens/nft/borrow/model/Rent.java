package com.fluidtokens.nft.borrow.model;

import com.bloxbean.cardano.client.address.Address;

import java.time.LocalDateTime;

public record Rent(Address owner, Address tenant, LocalDateTime deadline) {

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(deadline);
    }

}
