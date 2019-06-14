package com.l2d.tuto.springbootwebfluxsecurity;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public CustomErrorAttributes() {
        super(false);
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest serverRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(serverRequest, includeStackTrace);
        // format & update timestamp
        var timestamp = Optional.ofNullable(errorAttributes.get("timestamp"));
        errorAttributes.putIfAbsent("timestamp", dateFormat.format(timestamp.orElseGet(Date::new)));

        return errorAttributes;
    }
}