package com.fluidtokens.nft.borrow.service;

import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class ReturnNftJob implements Runnable {

    private final BFBackendService bfBackendService;

    @Override
    @Scheduled(timeUnit = TimeUnit.MINUTES, initialDelay = 2, fixedDelay = 15)
    public void run() {

        log.info("Running");


//        bfBackendService.getUtxoService().getUtxos()


    }
}
