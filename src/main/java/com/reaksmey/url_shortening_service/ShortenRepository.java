package com.reaksmey.url_shortening_service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortenRepository extends JpaRepository<Shorten, UUID> {
    @Query(value = "SELECT nextval('short_code_seq')", nativeQuery = true)
    Long getNextShortCodeSeq();

	@Modifying
	@Query("UPDATE Shorten s SET s.accessCount = s.accessCount + 1 WHERE s.shortCode = :shortCode")
	void incrementAccessCount(@Param("shortCode") String shortCode);

    Optional<Shorten> findByShortCode(String shortCode);

    void deleteByShortCode(String shortCode);
}
