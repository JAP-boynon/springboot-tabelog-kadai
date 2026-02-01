package com.example.nagoyamesi.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>{

}

