package com.mangoim.chat.service.configuration.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

	@Override
	public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		switch (parser.getCurrentToken()) {
			case VALUE_STRING:
				String rawDate = parser.getText();
				return FORMATTER.parse(rawDate, LocalDate::from);
		}
		throw context.wrongTokenException(parser, LocalDate.class, JsonToken.START_ARRAY, "Expected string.");
	}
}
