package com.fluidtokens.nft.borrow.client;

import com.fluidtokens.nft.borrow.model.Rents;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class FluidtokensApi {

    private WebClient client;

    public List<Rents> getExpiredRents() {
        Rents[] rents = client.get().retrieve().bodyToMono(Rents[].class).block();
        if (rents != null) {
            return Arrays.asList(rents);
        } else {
            return List.of();
        }
    }

    @PostConstruct
    public void initWebClient() {
        client = WebClient.builder()
                .baseUrl("https://api.fluidtokens.com/get-bs-available-pools?page=0")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
