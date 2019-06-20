package com.mangoim.chat.service.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
@Order(2)
@Slf4j
public class AuditLogConfig implements WebFilter{

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        return chain.filter(new PayloadServerWebExchangeDecorator(exchange))
                .doOnSuccess((done) -> success(startTime));
    }

    private void success(long startTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(String.format("Processing Time：%dms", (System.currentTimeMillis() - startTime)) + "\n");
        sb.append("==============================================");

        log.info(sb.toString());
    }

}
