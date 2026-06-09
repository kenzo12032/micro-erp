package com.erp.microerp.repository;

import com.erp.microerp.model.ContaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaFinanceiraRepository
        extends JpaRepository<ContaFinanceira, Integer> {
}