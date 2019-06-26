package com.mangoim.chat.service.audit;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AuditLogUtils {
    public static final List<MediaType> legalLogMediaTypes = Lists.newArrayList(MediaType.TEXT_XML,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_JSON_UTF8,
            MediaType.TEXT_PLAIN,
            MediaType.TEXT_XML);

    public static <T extends DataBuffer> T loggingRequest(Logger logger, T buffer, String requestInfo) {
        return logging(logger, requestInfo, buffer);
    }

    public static <T extends DataBuffer> T loggingResponse(Logger logger, T buffer) {
        return logging(logger, "Response ", buffer);
    }

    private static String passwordMask(byte[] bytes) {
        try {
            JsonObject jsonObject = new JsonParser().parse(new String(bytes)).getAsJsonObject();

            jsonObject.entrySet().forEach((obj) -> {
                if (obj.getKey().toLowerCase().contains("password")) {
                    obj.setValue(new JsonPrimitive("**********"));
                }
            });

            return jsonObject.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static <T extends DataBuffer> T logging(Logger log, String inOrOut, T buffer) {
        try {
            InputStream dataBuffer = buffer.asInputStream();
            byte[] bytes = dataBuffer.readAllBytes();
            var nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));

            StringBuilder sb = new StringBuilder();
            sb.append("\n")
              .append(inOrOut)
              .append("Body").append("      : ")
              .append(passwordMask(bytes));

            log.info(sb.toString());

            DataBufferUtils.release(buffer);
            return (T) nettyDataBufferFactory.wrap(bytes);
        } catch (Exception e) {
            log.error("Failed to write body. {}", e.getMessage(), e);
        }
        return null;
    }
}
