package com.bitsall.service;

import com.bitsall.mapper.CarpoolRequestMapper;
import com.bitsall.model.dto.CarpoolRequestDTO;
import com.bitsall.model.entity.CarpoolRequest;
import com.bitsall.repository.CarpoolRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRequestRepository carpoolRequestRepository;
    private final CarpoolRequestMapper carpoolRequestMapper;

    public CarpoolRequestDTO createCarpoolRequest(CarpoolRequestDTO carpoolRequestDTO) {
        CarpoolRequest carpoolRequest = carpoolRequestMapper.toEntity(carpoolRequestDTO);
        CarpoolRequest saved = carpoolRequestRepository.save(carpoolRequest);
        return carpoolRequestMapper.toDto(saved);
    }

    public List<CarpoolRequestDTO> getAllCarpoolRequests() {
        return carpoolRequestRepository.findAll().stream()
                .map(carpoolRequestMapper::toDto)
                .toList();
    }

    public CarpoolRequestDTO getCarpoolRequestById(UUID id) {
        CarpoolRequest carpoolRequest = carpoolRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carpool request not found with id: " + id));
        return carpoolRequestMapper.toDto(carpoolRequest);
    }

    public void deleteCarpoolRequest(UUID id) {
        if (!carpoolRequestRepository.existsById(id)) {
            throw new IllegalArgumentException("Carpool request not found with id: " + id);
        }
        carpoolRequestRepository.deleteById(id);
    }
}
