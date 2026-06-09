package com.erp.microerp.repository;

import com.erp.microerp.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface VendaRepository extends JpaRepository<Venda, Integer> {

    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v")
    BigDecimal totalVendas();

    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v " +
            "WHERE FUNCTION('MONTH', v.dataVenda) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', v.dataVenda) = FUNCTION('YEAR', CURRENT_DATE)")
    BigDecimal totalVendasMes();

    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v " +
            "WHERE FUNCTION('YEAR', v.dataVenda) = FUNCTION('YEAR', CURRENT_DATE)")
    BigDecimal totalVendasAno();
}