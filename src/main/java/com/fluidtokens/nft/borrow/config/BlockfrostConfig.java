package com.fluidtokens.nft.borrow.config;

import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockfrostConfig {

    @Value("${blockfrost.url}")
    private String blockfrostUrl;

    @Value("${blockfrost.key}")
    private String blockfrostKey;

    @Bean
    public BFBackendService bfBackendService() {
        return new BFBackendService(blockfrostUrl, blockfrostKey);
    }

}
