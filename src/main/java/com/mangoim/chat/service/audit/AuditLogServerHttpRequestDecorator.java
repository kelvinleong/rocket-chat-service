package com.mangoim.chat.service.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.stream.Collectors;

import static reactor.core.scheduler.Schedulers.single;

@Slf4j
public class AuditLogServerHttpRequestDecorator extends ServerHttpRequestDecorator {
    private Flux<DataBuffer> body;

    public AuditLogServerHttpRequestDecorator(ServerHttpRequest delegate) {
        super(delegate);
        final String path = delegate.getURI().getPath();
        final String query = delegate.getURI().getQuery();
        final String method = Optional.ofNullable(delegate.getMethod()).orElse(HttpMethod.GET).name();
        final String headers = delegate.getHeaders().entrySet()
                .stream()
                .map(entry -> "            " + entry.getKey() + ": [" + String.join(";", entry.getValue()) + "]")
                .collect(Collectors.joining("\n"));
        final MediaType contentType = delegate.getHeaders().getContentType();

        StringBuilder sb = new StringBuilder();
        sb.append("HttpMethod : ").append(method).append("\n")
          .append("Uri        : ").append(path).append(StringUtils.isEmpty(query) ? "" : "?" + query).append("\n")
          .append("Headers    : ").append("\n").append(headers).append("\n");

        Flux<DataBuffer> flux = super.getBody();
        if (AuditLogUtils.legalLogMediaTypes.contains(contentType)) {
            body = flux.publishOn(single()).map(dataBuffer -> AuditLogUtils.loggingRequest(log, dataBuffer, sb.toString()));
        } else {
            body = flux;
        }
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return body;
    }
}
