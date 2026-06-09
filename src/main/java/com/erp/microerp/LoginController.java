package com.erp.microerp.controller;

import com.erp.microerp.model.Usuario;
import com.erp.microerp.repository.UsuarioRepository;
import com.erp.microerp.service.AuditoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public LoginController(UsuarioRepository usuarioRepository,
                           AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String usuario,
                             @RequestParam String senha,
                             HttpSession session,
                             Model model) {

        Usuario usuarioBanco = usuarioRepository.findByUsernameAndSenha(usuario, senha);

        if (usuarioBanco != null) {
            session.setAttribute("usuarioLogado", usuarioBanco.getUsername());
            session.setAttribute("perfil", usuarioBanco.getPerfil());

            auditoriaService.registrar(
                    session,
                    "LOGIN",
                    "ENTRADA",
                    "Usuário " + usuarioBanco.getUsername() + " acessou o sistema"
            );

            return "redirect:/home";
        }

        model.addAttribute("erro", "Usuário ou senha inválidos.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        auditoriaService.registrar(
                session,
                "LOGIN",
                "SAÍDA",
                "Usuário saiu do sistema"
        );

        session.invalidate();
        return "redirect:/login";
    }
}