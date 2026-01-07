package com.linkup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linkup.model.UrlMapping;

import jakarta.transaction.Transactional;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE UrlMapping u SET u.clickCount = u.clickCount + :clicks WHERE u.shortCode = :shortCode")
    void updateClickCount(String shortCode, Long clicks);
}