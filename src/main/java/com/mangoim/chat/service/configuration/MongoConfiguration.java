package com.mangoim.chat.service.configuration;

import com.google.common.collect.Lists;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;


import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Configuration
@Slf4j
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;


    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    /**
     * Returns the list of custom converters that will be used by the MongoDB template
     *
     **/
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = Lists.newArrayList();
        converterList.add(new ZonedDateTimeToDateConverter());
        converterList.add(new DateToZonedDateTimeConverter());
        return new MongoCustomConversions(converterList);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient) {
        var template =  new ReactiveMongoTemplate(mongoClient, "test");
        MappingMongoConverter mongoMapping = (MappingMongoConverter) template.getConverter();
        mongoMapping.setCustomConversions(customConversions());
        mongoMapping.afterPropertiesSet();
        return template;
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create();
    }


    @ReadingConverter
    static class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {

        @Override
        public ZonedDateTime convert(Date date) {
            return new com.mangoim.chat.service.utils.DateToZonedDateTimeConverter().convert(date);
        }
    }

    @WritingConverter
    static class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {

        @Override
        public Date convert(ZonedDateTime source) {
            return new com.mangoim.chat.service.utils.ZonedDateTimeToDateConverter().convert(source);
        }
    }
}
