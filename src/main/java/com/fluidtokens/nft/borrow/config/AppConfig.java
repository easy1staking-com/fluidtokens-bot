package com.fluidtokens.nft.borrow.config;

import org.cardanofoundation.conversions.CardanoConverters;
import org.cardanofoundation.conversions.ClasspathConversionsFactory;
import org.cardanofoundation.conversions.domain.NetworkType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {


    @Bean
    public CardanoConverters cardanoConverters() {
        return ClasspathConversionsFactory.createConverters(NetworkType.MAINNET);
    }

}
