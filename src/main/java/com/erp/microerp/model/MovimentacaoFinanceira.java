package com.erp.microerp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_financeira")
public class MovimentacaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "plano_conta_id")
    private PlanoContas planoConta;

    private String descricao;

    private String tipo;

    private BigDecimal valor;

    @Column(name = "data_movimentacao")
    private LocalDateTime dataMovimentacao = LocalDateTime.now();

    private String origem;

    @Column(name = "referencia_id")
    private Integer referenciaId;

    private String status = "EFETIVADA";

    public MovimentacaoFinanceira() {
    }

    public Integer getId() {
        return id;
    }

    public PlanoContas getPlanoConta() {
        return planoConta;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public String getOrigem() {
        return origem;
    }

    public Integer getReferenciaId() {
        return referenciaId;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPlanoConta(PlanoContas planoConta) {
        this.planoConta = planoConta;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public void setReferenciaId(Integer referenciaId) {
        this.referenciaId = referenciaId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}