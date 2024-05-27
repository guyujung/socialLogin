package com.example.oauth.repository;

import com.example.oauth.entity.InterestingSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestingSportRepository extends JpaRepository<InterestingSport,Long> {

}
