package com.erp.microerp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sku;
    private String nome;

    private Double preco_custo;
    private Double preco_venda;

    private Integer estoque;

    @Column(name = "estoque_minimo")
    private Integer estoque_minimo;

    @Column(name = "custo_medio")
    private Double custo_medio;

    private Boolean ativo = true;

    public Produto() {
    }

    public Integer getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getNome() {
        return nome;
    }

    public Double getPreco_custo() {
        return preco_custo;
    }

    public Double getPreco_venda() {
        return preco_venda;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public Integer getEstoque_minimo() {
        return estoque_minimo;
    }

    public Double getCusto_medio() {
        return custo_medio;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPreco_custo(Double preco_custo) {
        this.preco_custo = preco_custo;
    }

    public void setPreco_venda(Double preco_venda) {
        this.preco_venda = preco_venda;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public void setEstoque_minimo(Integer estoque_minimo) {
        this.estoque_minimo = estoque_minimo;
    }

    public void setCusto_medio(Double custo_medio) {
        this.custo_medio = custo_medio;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}