package com.bitsall.service;

import com.bitsall.model.dto.CarpoolRequestDTO;
import com.bitsall.model.entity.CarpoolRequest;
import com.bitsall.model.entity.User;
import com.bitsall.repository.CarpoolRequestRepository;
import com.bitsall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRequestRepository carpoolRequestRepository;
    private final UserRepository userRepository;

    public CarpoolRequestDTO createCarpoolRequest(CarpoolRequestDTO carpoolRequestDTO) {
        User user = userRepository.findById(carpoolRequestDTO.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + carpoolRequestDTO.getCreatedBy()));

        CarpoolRequest carpoolRequest = CarpoolRequest.builder()
                .pickupLocation(carpoolRequestDTO.getPickupLocation())
                .destination(carpoolRequestDTO.getDestination())
                .pickupTime(carpoolRequestDTO.getPickupTime())
                .phoneNumber(carpoolRequestDTO.getPhoneNumber())
                .requestType(carpoolRequestDTO.getRequestType())
                .createdBy(user)
                .build();

        CarpoolRequest saved = carpoolRequestRepository.save(carpoolRequest);
        return toDto(saved);
    }

    public List<CarpoolRequestDTO> getAllCarpoolRequests() {
        return carpoolRequestRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CarpoolRequestDTO getCarpoolRequestById(UUID id) {
        CarpoolRequest carpoolRequest = carpoolRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carpool request not found with id: " + id));
        return toDto(carpoolRequest);
    }

    private CarpoolRequestDTO toDto(CarpoolRequest carpoolRequest) {
        CarpoolRequestDTO dto = new CarpoolRequestDTO();
        dto.setId(carpoolRequest.getId());
        dto.setPickupLocation(carpoolRequest.getPickupLocation());
        dto.setDestination(carpoolRequest.getDestination());
        dto.setPickupTime(carpoolRequest.getPickupTime());
        dto.setPhoneNumber(carpoolRequest.getPhoneNumber());
        dto.setRequestType(carpoolRequest.getRequestType());
        dto.setCreatedBy(carpoolRequest.getCreatedBy() != null ? carpoolRequest.getCreatedBy().getId() : null);
        return dto;
    }

    public void deleteCarpoolRequest(UUID id) {
        if (!carpoolRequestRepository.existsById(id)) {
            throw new IllegalArgumentException("Carpool request not found with id: " + id);
        }
        carpoolRequestRepository.deleteById(id);
    }
}
