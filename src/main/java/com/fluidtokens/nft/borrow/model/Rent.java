package com.fluidtokens.nft.borrow.model;

import com.bloxbean.cardano.client.address.Address;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record Rent(Address owner, Address tenant, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deadline) {


    public boolean canBeReturned() {
        return isExpired() && isLent();
    }

    public boolean isLent() {
        return !owner.getAddress().equals(tenant.getAddress());
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(deadline);
    }

}
