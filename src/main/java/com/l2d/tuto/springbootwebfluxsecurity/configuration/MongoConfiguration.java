package com.l2d.tuto.springbootwebfluxsecurity.configuration;

import com.github.mongobee.Mongobee;
import com.google.common.collect.Lists;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;


import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author duc-d
 */
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

//    @Bean
//    public Mongobee mongobee(MongoProperties mongoProperties) throws Exception {
//        log.debug("Configuring Mongobee");
//        Mongobee mongobee = new Mongobee(mongo());
//        mongobee.setDbName(mongoProperties.getMongoClientDatabase());
//        mongobee.setMongoTemplate(mongoTemplate());
//        // package to scan for migrations
//        mongobee.setChangeLogsScanPackage("com.l2d.tuto.springbootwebfluxsecurity.configuration");
//        mongobee.setEnabled(true);
//        return mongobee;
//    }

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create();
    }


    @ReadingConverter
    static class DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {

        @Override
        public ZonedDateTime convert(Date date) {
            return new com.l2d.tuto.springbootwebfluxsecurity.utils.DateToZonedDateTimeConverter().convert(date);
        }
    }

    @WritingConverter
    static class ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {

        @Override
        public Date convert(ZonedDateTime source) {
            return new com.l2d.tuto.springbootwebfluxsecurity.utils.ZonedDateTimeToDateConverter().convert(source);
        }
    }
}
