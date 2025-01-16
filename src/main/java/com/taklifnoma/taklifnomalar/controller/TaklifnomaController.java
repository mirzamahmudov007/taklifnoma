package com.taklifnoma.taklifnomalar.controller;

import com.taklifnoma.taklifnomalar.dto.TaklifnomaDTO;
import com.taklifnoma.taklifnomalar.entity.Taklifnoma;
import com.taklifnoma.taklifnomalar.service.TaklifnomaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/taklifnomalar")
public class TaklifnomaController {

    @Autowired
    private TaklifnomaService taklifnomaService;

    @GetMapping
    public ResponseEntity<List<TaklifnomaDTO>> getAllTaklifnomalar() {
        List<TaklifnomaDTO> taklifnomalar = taklifnomaService.getAllTaklifnomalar();
        return ResponseEntity.ok(taklifnomalar);
    }
    @GetMapping("/get")
    public ResponseEntity<List<Taklifnoma>> getAllr() {
        List<Taklifnoma> taklifnomalar = taklifnomaService.getAll();
        return ResponseEntity.ok(taklifnomalar);
    }
}