package com.fluidtokens.nft.borrow.model;

import java.math.BigInteger;
import java.util.List;

public record RentData(List<String> renterAddress, BigInteger poolAmount) {
}