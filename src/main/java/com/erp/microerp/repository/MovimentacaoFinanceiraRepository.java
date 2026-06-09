package com.erp.microerp.repository;

import com.erp.microerp.model.MovimentacaoFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface MovimentacaoFinanceiraRepository
        extends JpaRepository<MovimentacaoFinanceira, Integer> {

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoFinanceira m WHERE m.tipo = 'CREDITO'")
    BigDecimal totalCreditos();

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoFinanceira m WHERE m.tipo = 'DEBITO'")
    BigDecimal totalDebitos();

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoFinanceira m WHERE m.origem = 'VENDA'")
    BigDecimal totalVendas();

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoFinanceira m WHERE m.origem = 'COMPRA'")
    BigDecimal totalCompras();

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoFinanceira m WHERE m.origem = 'PERDA_ESTOQUE'")
    BigDecimal totalPerdas();
}