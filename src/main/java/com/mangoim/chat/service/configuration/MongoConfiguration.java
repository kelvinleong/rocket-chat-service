package com.mangoim.chat.service.configuration;

import com.google.common.collect.Lists;
import com.mangoim.chat.service.user.repository.AuthorityRepository;
import com.mangoim.chat.service.user.repository.UserRepository;
import com.mangoim.chat.service.utils.SystemTime;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = {UserRepository.class, AuthorityRepository.class})
@EnableMongoAuditing(dateTimeProviderRef = "rocketDateTimeProvider")
@Slf4j
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {
    private final String mongoHost;
    private final int mongoPort;
    private final String mongoDatabase;
    private final String username;
    private final String password;

    public MongoConfiguration(@Value("${rocket.mongodb.host}") String mongoHost,
                              @Value("${rocket.mongodb.port}") int mongoPort,
                              @Value("${rocket.mongodb.username}") String username,
                              @Value("${rocket.mongodb.password}") String password,
                              @Value("${rocket.mongodb.database}") String mongoDatabase) {
        this.mongoHost = mongoHost;
        this.mongoPort = mongoPort;
        this.mongoDatabase = mongoDatabase;
        this.username = username;
        this.password = password;
    }

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @Override
    public MongoClient reactiveMongoClient() {
        String url = "mongodb://" + username + ":" + password +
                                "@" + mongoHost + ":" + mongoPort +
                                "/" + getDatabaseName() +
                                "?authSource=" + getDatabaseName();
        return MongoClients.create(url);
    }

    /**
     * Returns the list of custom converters that will be used by the MongoDB template
     *
     **/
    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = Lists.newArrayList();
        converterList.add(new ZonedDateTimeToDateConverter());
        converterList.add(new DateToZonedDateTimeConverter());
        return new MongoCustomConversions(converterList);
    }

    @Bean
    @Override
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        var template =  new ReactiveMongoTemplate(reactiveMongoClient(), mongoDatabase);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) template.getConverter();
        mongoMapping.setCustomConversions(customConversions());
        mongoMapping.afterPropertiesSet();
        return template;
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

    @Bean("rocketDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(SystemTime.now());
    }
}
