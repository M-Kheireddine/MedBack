package tn.iteam.medcoreservice.services.specs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.iteam.medcoreservice.dtos.responses.PrescriptionResponseDto;
import tn.iteam.medcoreservice.mappers.PrescriptionMapper;
import tn.iteam.medcoreservice.repositories.PrescriptionRepository;
import tn.iteam.medcoreservice.services.impls.IPrescriptionService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PrescriptionService implements IPrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Override
    public List<PrescriptionResponseDto> getAllPrescriptions() {
        return this.prescriptionRepository.findAll()
                .stream()
                .map(prescriptionMapper::toPrescriptionResponseDto)
                .toList();
    }

    @Override
    public PrescriptionResponseDto getPrescriptionById(String prescriptionId) {
        return this.prescriptionRepository.findById(prescriptionId)
                .map(prescriptionMapper::toPrescriptionResponseDto)
                .orElse(null);
    }
}
