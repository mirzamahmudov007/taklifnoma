package com.taklifnoma.taklifnomalar.repository;

import com.taklifnoma.taklifnomalar.entity.Taklifnoma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaklifnomaRepository extends JpaRepository<Taklifnoma, String> {
}