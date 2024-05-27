package com.example.oauth.service;

import com.example.oauth.entity.InterestingSport;
import com.example.oauth.repository.InterestingSportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class InterestingSportService {
    private final InterestingSportRepository interestingSportRepository;

    public void SaveAllInterestingSport(List<InterestingSport> interestingSportList){
    interestingSportRepository.saveAll(interestingSportList);
    }
}
