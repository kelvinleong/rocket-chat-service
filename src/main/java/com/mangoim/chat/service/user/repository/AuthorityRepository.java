package com.mangoim.chat.service.user.repository;

import com.mangoim.chat.service.user.domain.Authority;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author duc-d
 * Spring Data MongoDB repository for the Authority entity.
 */
@Repository
public interface AuthorityRepository extends ReactiveMongoRepository<Authority, String> {
    Mono<Authority> findByName(String name);
}
