package com.fluidtokens.nft.borrow.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

class TransactionOutputTest {

    @Test
    public void testSorting() {

        var actualUtxoList = Stream.of("23b0a99ca2311604f8ba40ce4acf1524f3aa8723fe4ca5262428d402aa080f70#2",
                        "18fbf60b3c90e43dcb1875e321ae65b052ff8bc921144ef01b82357d69a2d800#0",
                        "1e6db1cac3546b790a9d48ec5b470d89e871d93c02cf23aab22654d6d9396eae#0",
                        "1e6db1cac3546b790a9d48ec5b470d89e871d93c02cf23aab22654d6d9396eae#10",
                        "23b0a99ca2311604f8ba40ce4acf1524f3aa8723fe4ca5262428d402aa080f70#0")
                .map(TransactionOutput::fromString)
                .sorted().toList();

        var expectedUtxoList = List.of(
                new TransactionOutput("18fbf60b3c90e43dcb1875e321ae65b052ff8bc921144ef01b82357d69a2d800", 0),
                new TransactionOutput("1e6db1cac3546b790a9d48ec5b470d89e871d93c02cf23aab22654d6d9396eae", 0),
                new TransactionOutput("1e6db1cac3546b790a9d48ec5b470d89e871d93c02cf23aab22654d6d9396eae", 10),
                new TransactionOutput("23b0a99ca2311604f8ba40ce4acf1524f3aa8723fe4ca5262428d402aa080f70", 0),
                new TransactionOutput("23b0a99ca2311604f8ba40ce4acf1524f3aa8723fe4ca5262428d402aa080f70", 2));

        Assertions.assertEquals(expectedUtxoList, actualUtxoList);


    }

}