package com.fluidtokens.nft.borrow.model;

public record TransactionOutput(String hash, int index) implements Comparable<TransactionOutput> {
    @Override
    public int compareTo(TransactionOutput that) {

        int i = hash.compareTo(that.hash);

        if (i == 0) {

            if (index == that.index) {
                return 0;
            } else {
                return index - that.index;
            }

        } else {
            return i;
        }

    }

    public static TransactionOutput fromString(String hashIndex) {
        var parts = hashIndex.split("#");
        return new TransactionOutput(parts[0], Integer.parseInt(parts[1]));
    }

}
