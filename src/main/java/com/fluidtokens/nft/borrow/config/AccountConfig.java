package com.fluidtokens.nft.borrow.config;

import com.bloxbean.cardano.client.account.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    public Account account(@Value("${wallet.mnemonic}") String mnemonic) {
        return new Account(mnemonic);
    }


}
