package com.bitsall.service;

import com.bitsall.model.entity.CarpoolRequest;
import com.bitsall.repository.CarpoolRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRequestRepository carpoolRequestRepository;

    public CarpoolRequest createCarpoolRequest(CarpoolRequest carpoolRequest) {
        return carpoolRequestRepository.save(carpoolRequest);
    }

    public List<CarpoolRequest> getAllCarpoolRequests() {
        return carpoolRequestRepository.findAll();
    }

    public void deleteCarpoolRequest(Long id) {
        carpoolRequestRepository.deleteById(id);
    }
}
