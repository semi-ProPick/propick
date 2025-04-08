package com.ezen.propick.notification.service;

import com.ezen.propick.notification.Enum.PopUpStatus;
import com.ezen.propick.notification.dto.PopUpDTO;
import com.ezen.propick.notification.entity.PopUp;
import com.ezen.propick.notification.repository.PopUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopUpService {

    private final PopUpRepository popUpRepository;

    // ID로 팝업 조회
    public PopUpDTO getPopUpById(Integer pId) {
        PopUp popUp = popUpRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팝업이 존재하지 않습니다: " + pId));
        return convertToDTO(popUp);
    }

    // 모든 팝업 조회
    public List<PopUpDTO> getAllPopUps() {
        List<PopUp> popUps = popUpRepository.findAll();
        return popUps.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // IN_PROGRESS 상태의 팝업 조회
    public List<PopUpDTO> getInProgressPopUps() {
        return getPopUpsByStatus(PopUpStatus.IN_PROGRESS);
    }

    // COMPLETED 상태의 팝업 조회
    public List<PopUpDTO> getCompletedPopUps() {
        return getPopUpsByStatus(PopUpStatus.COMPLETED);
    }

    // 상태별 팝업 조회 (동적 구동용)
    public List<PopUpDTO> getPopUpsByStatus(PopUpStatus status) {
        List<PopUp> popUps = popUpRepository.findByPStatus(status);
        return popUps.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    private PopUpDTO convertToDTO(PopUp popUp) {
        PopUpDTO dto = new PopUpDTO();
        dto.setPId(popUp.getPId());
        dto.setTitle(popUp.getTitle());
        dto.setPStatus(String.valueOf(popUp.getPStatus()));
        dto.setCreatedAt(popUp.getCreatedAt());
        dto.setUpdatedAt(popUp.getUpdatedAt());
        dto.setPType(String.valueOf(popUp.getPType()));
        dto.setBrandId(popUp.getBrandId());
        return dto;
    }
}