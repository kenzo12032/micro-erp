package com.erp.microerp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String usuario;
    private String perfil;
    private String modulo;
    private String acao;
    private String descricao;

    @Column(name = "data_hora")
    private LocalDateTime dataHora = LocalDateTime.now();

    public Integer getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPerfil() {
        return perfil;
    }

    public String getModulo() {
        return modulo;
    }

    public String getAcao() {
        return acao;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}