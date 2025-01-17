package com.taklifnoma.taklifnomalar.repository;

import com.taklifnoma.taklifnomalar.entity.Taklifnoma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaklifnomaRepository extends JpaRepository<Taklifnoma, String> {

    Optional<Taklifnoma> findById(Long id);
}