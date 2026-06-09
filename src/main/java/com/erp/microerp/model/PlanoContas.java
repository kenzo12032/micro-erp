package com.erp.microerp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plano_contas")
public class PlanoContas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String descricao;

    private String tipo;

    private Boolean ativo = true;

    public PlanoContas() {
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}