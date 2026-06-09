package com.erp.microerp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "perda_estoque")
public class PerdaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Integer quantidade;

    private String motivo;

    private String observacao;

    @Column(name = "valor_prejuizo")
    private BigDecimal valorPrejuizo;

    @Column(name = "data_perda")
    private LocalDateTime dataPerda = LocalDateTime.now();

    public Integer getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public BigDecimal getValorPrejuizo() {
        return valorPrejuizo;
    }

    public LocalDateTime getDataPerda() {
        return dataPerda;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setValorPrejuizo(BigDecimal valorPrejuizo) {
        this.valorPrejuizo = valorPrejuizo;
    }

    public void setDataPerda(LocalDateTime dataPerda) {
        this.dataPerda = dataPerda;
    }
}