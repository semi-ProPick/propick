package com.ezen.propick.survey.controller;
//
//import com.ezen.propick.survey.service.SurveyResponseService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin/survey-responses")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')") // Spring Security 권한 제어
//public class AdminSurveyResponseController {
//
//    private final SurveyResponseService surveyResponseService;
//
//    @GetMapping
//    public ResponseEntity<List<SurveyResponseAdminDTO>> getAllResponses() {
//        return ResponseEntity.ok(surveyResponseService.getAllResponses());
//    }
//
//    @DeleteMapping("/{responseId}")
//    public ResponseEntity<Void> deleteResponse(@PathVariable Integer responseId) {
//        surveyResponseService.deletePermanently(responseId); // 실제 DB 삭제
//        return ResponseEntity.noContent().build();
//    }
//}