package com.erp.microerp.controller;

import com.erp.microerp.model.Usuario;
import com.erp.microerp.repository.UsuarioRepository;
import com.erp.microerp.service.AuditoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("usuario", new Usuario());
        return "usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();

        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("usuario", usuario);

        return "usuarios";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario,
                         HttpSession session) {

        if (usuario.getId() == null) {
            usuario.setAtivo(true);

            auditoriaService.registrar(
                    session,
                    "USUÁRIOS",
                    "NOVO USUÁRIO",
                    "Usuário " + usuario.getUsername() + " criado com perfil " + usuario.getPerfil()
            );

        } else {
            Usuario antigo = usuarioRepository.findById(usuario.getId()).orElseThrow();
            usuario.setAtivo(antigo.getAtivo());

            auditoriaService.registrar(
                    session,
                    "USUÁRIOS",
                    "EDITAR USUÁRIO",
                    "Usuário " + usuario.getUsername() + " editado"
            );
        }

        usuarioRepository.save(usuario);

        return "redirect:/usuarios";
    }

    @GetMapping("/desabilitar/{id}")
    public String desabilitar(@PathVariable Integer id,
                              HttpSession session) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                session,
                "USUÁRIOS",
                "DESABILITAR",
                "Usuário " + usuario.getUsername() + " foi desabilitado"
        );

        return "redirect:/usuarios";
    }

    @GetMapping("/habilitar/{id}")
    public String habilitar(@PathVariable Integer id,
                            HttpSession session) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                session,
                "USUÁRIOS",
                "HABILITAR",
                "Usuário " + usuario.getUsername() + " foi habilitado"
        );

        return "redirect:/usuarios";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id,
                          HttpSession session) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow();

        auditoriaService.registrar(
                session,
                "USUÁRIOS",
                "EXCLUIR",
                "Usuário " + usuario.getUsername() + " foi excluído"
        );

        usuarioRepository.deleteById(id);

        return "redirect:/usuarios";
    }
}