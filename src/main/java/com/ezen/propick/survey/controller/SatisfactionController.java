package com.ezen.propick.survey.controller;

import com.ezen.propick.survey.dto.recommendation.SatisfactionDTO;
import com.ezen.propick.survey.service.SatisfactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/satisfaction")
public class SatisfactionController {

    private final SatisfactionService satisfactionService;

    @PostMapping
    public ResponseEntity<String> save(@RequestBody @Valid SatisfactionDTO dto) {
        satisfactionService.save(dto);
        return ResponseEntity.ok("Satisfaction saved");
    }
}
