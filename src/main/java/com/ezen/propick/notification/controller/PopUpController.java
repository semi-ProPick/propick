package com.ezen.propick.notification.controller;

import com.ezen.propick.notification.dto.PopUpDTO;
import com.ezen.propick.notification.service.PopUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/popup")
@RequiredArgsConstructor
public class PopUpController {

    private final PopUpService popUpService;

    @GetMapping("/event")
    public String getEventPage(Model model) {
        List<PopUpDTO> popUps = popUpService.getAllPopUps();
        model.addAttribute("popUps", popUps);
        return "main/event";
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<PopUpDTO>> getAllPopUps() {
        List<PopUpDTO> popUps = popUpService.getAllPopUps();
        return ResponseEntity.ok(popUps);
    }

    @GetMapping("/in-progress")
    @ResponseBody
    public ResponseEntity<List<PopUpDTO>> getInProgressPopUps() {
        List<PopUpDTO> popUps = popUpService.getInProgressPopUps();
        return ResponseEntity.ok(popUps);
    }

    @GetMapping("/completed")
    @ResponseBody
    public ResponseEntity<List<PopUpDTO>> getCompletedPopUps() {
        List<PopUpDTO> popUps = popUpService.getCompletedPopUps();
        return ResponseEntity.ok(popUps);
    }

    @GetMapping("/{pId}")
    @ResponseBody
    public ResponseEntity<PopUpDTO> getPopUp(@PathVariable Integer pId) {
        PopUpDTO popUpDTO = popUpService.getPopUpById(pId);
        return ResponseEntity.ok(popUpDTO);
    }
}