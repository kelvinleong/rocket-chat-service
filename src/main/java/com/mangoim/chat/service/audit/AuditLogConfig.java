package com.mangoim.chat.service.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class AuditLogConfig implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        return chain
                .filter(new PayloadServerWebExchangeDecorator(exchange))
                .doOnSuccessOrError((done, throwable) -> success(startTime, throwable));
    }

    private void success(long startTime, Throwable e) {
        log.info("", e);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Processing Time：%dms", (System.currentTimeMillis() - startTime)) + "\n");
        sb.append("===========================================================================================");

        log.info(sb.toString());
    }
}
