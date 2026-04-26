package com.reaksmey.url_shortening_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortenRepository extends JpaRepository<Shorten, Long> {
    @Query(value = "SELECT nextval('short_code_seq')", nativeQuery = true)
    Long getNextShortCodeSeq();
}
