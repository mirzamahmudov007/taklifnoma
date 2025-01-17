package com.taklifnoma.taklifnomalar.service;

import com.taklifnoma.taklifnomalar.dto.TaklifnomaDTO;
import com.taklifnoma.taklifnomalar.entity.Taklifnoma;
import com.taklifnoma.taklifnomalar.repository.TaklifnomaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaklifnomaService {

    @Autowired
    private TaklifnomaRepository taklifnomaRepository;

    public List<TaklifnomaDTO> getAllTaklifnomalar() {
        List<Taklifnoma> taklifnomalar = taklifnomaRepository.findAll();
        return taklifnomalar.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Taklifnoma> getAll() {
        List<Taklifnoma> taklifnomalar = taklifnomaRepository.findAll();
        return taklifnomalar;
    }

    private TaklifnomaDTO convertToDTO(Taklifnoma taklifnoma) {
        TaklifnomaDTO dto = new TaklifnomaDTO();
        dto.setId(taklifnoma.getId());
        dto.setBuyurtmachiId(taklifnoma.getBuyurtmachiId());
        dto.setType(taklifnoma.getType());
        dto.setStatus(taklifnoma.getStatus());
        return dto;
    }

    public Taklifnoma update(Long id , String status) {
        Optional<Taklifnoma> taklifnoma = taklifnomaRepository.findById(id);
        taklifnoma.get().setStatus(Taklifnoma.TaklifnomaStatus.valueOf(status));
        return  taklifnomaRepository.save(taklifnoma.get());
    }
}