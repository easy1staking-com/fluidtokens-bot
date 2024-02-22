package com.fluidtokens.nft.borrow.model.external.api;

import java.math.BigInteger;
import java.util.List;

public record RentData(List<String> renterAddress, BigInteger poolAmount) {
}