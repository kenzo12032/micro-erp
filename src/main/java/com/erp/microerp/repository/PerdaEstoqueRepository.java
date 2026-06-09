package com.erp.microerp.repository;

import com.erp.microerp.model.PerdaEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerdaEstoqueRepository
        extends JpaRepository<PerdaEstoque, Integer> {
}