package com.mangoim.chat.service.configuration.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

	@Override
	public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		switch (parser.getCurrentToken()) {
			case VALUE_STRING:
				String rawDate = parser.getText();
				return ZonedDateTime.parse(rawDate);
		}
		throw context.wrongTokenException(parser, LocalDateTime.class, JsonToken.START_ARRAY, "Expected string.");
	}
}
