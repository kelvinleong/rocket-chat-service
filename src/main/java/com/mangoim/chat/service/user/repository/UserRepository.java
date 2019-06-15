package com.mangoim.chat.service.user.repository;

import com.mangoim.chat.service.user.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author duc-d
 */
@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByLogin(String login);
    Mono<User> findByUsername(String username);
}
