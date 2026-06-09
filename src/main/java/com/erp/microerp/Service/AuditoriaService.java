package com.erp.microerp.service;

import com.erp.microerp.model.Auditoria;
import com.erp.microerp.repository.AuditoriaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(HttpSession session,
                          String modulo,
                          String acao,
                          String descricao) {

        String usuario = "DESCONHECIDO";
        String perfil = "SEM_PERFIL";

        if (session != null) {
            Object usuarioSessao = session.getAttribute("usuarioLogado");
            Object perfilSessao = session.getAttribute("perfil");

            if (usuarioSessao != null) {
                usuario = usuarioSessao.toString();
            }

            if (perfilSessao != null) {
                perfil = perfilSessao.toString();
            }
        }

        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setPerfil(perfil);
        auditoria.setModulo(modulo);
        auditoria.setAcao(acao);
        auditoria.setDescricao(descricao);

        auditoriaRepository.save(auditoria);
    }
}