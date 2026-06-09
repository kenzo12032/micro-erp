package com.erp.microerp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        HttpSession session = request.getSession(false);

        boolean logado =
                session != null &&
                        session.getAttribute("usuarioLogado") != null;

        if (!logado) {
            response.sendRedirect("/login");
            return false;
        }

        String perfil = session.getAttribute("perfil").toString();

        // ADMIN acessa tudo
        if ("ADMIN".equals(perfil)) {
            return true;
        }

        // AUDITORIA
        if (uri.startsWith("/auditoria")) {
            response.sendRedirect("/home");
            return false;
        }

        // USUÁRIOS
        if (uri.startsWith("/usuarios")) {
            response.sendRedirect("/home");
            return false;
        }

        // VENDAS
        if (uri.startsWith("/vendas") ||
                uri.startsWith("/clientes")) {

            if (!perfil.equals("VENDAS")) {
                response.sendRedirect("/home");
                return false;
            }
        }

        // ESTOQUE
        if (uri.startsWith("/produtos") ||
                uri.startsWith("/estoque") ||
                uri.startsWith("/perdas")) {

            if (!perfil.equals("ESTOQUE")) {
                response.sendRedirect("/home");
                return false;
            }
        }

        // COMPRAS
        if (uri.startsWith("/compras") ||
                uri.startsWith("/fornecedores")) {

            if (!perfil.equals("COMPRAS")) {
                response.sendRedirect("/home");
                return false;
            }
        }

        // FINANCEIRO
        if (uri.startsWith("/financeiro") ||
                uri.startsWith("/relatorios") ||
                uri.startsWith("/dashboard") ||
                uri.startsWith("/plano-contas") ||
                uri.startsWith("/contas-financeiras") ||
                uri.startsWith("/fluxo-caixa") ||
                uri.startsWith("/dre") ||
                uri.startsWith("/balanco") ||
                uri.startsWith("/lucratividade")) {

            if (!perfil.equals("FINANCEIRO")) {
                response.sendRedirect("/home");
                return false;
            }
        }

        return true;
    }
}