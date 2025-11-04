package com.bitsall.service;

import com.bitsall.mapper.CarpoolRequestMapper;
import com.bitsall.model.dto.CarpoolRequestDTO;
import com.bitsall.model.entity.CarpoolRequest;
import com.bitsall.repository.CarpoolRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private CarpoolRequestRepository carpoolRequestRepository;
    private CarpoolRequestMapper carpoolRequestMapper;

    public CarpoolRequest createCarpoolRequest(CarpoolRequestDTO carpoolRequestDTO) {
        CarpoolRequest carpoolRequest = carpoolRequestMapper.toEntity(carpoolRequestDTO);
        return carpoolRequestRepository.save(carpoolRequest);
    }

    public List<CarpoolRequest> getAllCarpoolRequests() {
        return carpoolRequestRepository.findAll();
    }

    public void deleteCarpoolRequest(Long id) {
        carpoolRequestRepository.deleteById(id);
    }
}
