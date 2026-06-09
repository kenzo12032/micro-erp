package com.erp.microerp.repository;

import com.erp.microerp.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface CompraRepository extends JpaRepository<Compra, Integer> {

    @Query("SELECT COALESCE(SUM(c.valorTotal), 0) FROM Compra c")
    BigDecimal totalCompras();
}