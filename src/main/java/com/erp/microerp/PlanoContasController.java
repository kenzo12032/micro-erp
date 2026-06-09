package com.erp.microerp.controller;

import com.erp.microerp.model.PlanoContas;
import com.erp.microerp.repository.PlanoContasRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/plano-contas")
public class PlanoContasController {

    private final PlanoContasRepository planoContasRepository;

    public PlanoContasController(PlanoContasRepository planoContasRepository) {
        this.planoContasRepository = planoContasRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contas", planoContasRepository.findAll());
        model.addAttribute("conta", new PlanoContas());
        return "plano-contas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {

        PlanoContas conta = planoContasRepository.findById(id).orElseThrow();

        model.addAttribute("contas", planoContasRepository.findAll());
        model.addAttribute("conta", conta);

        return "plano-contas";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute PlanoContas conta) {

        if (conta.getId() == null) {
            conta.setAtivo(true);
        } else {
            PlanoContas antiga = planoContasRepository.findById(conta.getId()).orElseThrow();
            conta.setAtivo(antiga.getAtivo());
        }

        planoContasRepository.save(conta);

        return "redirect:/plano-contas";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {

        planoContasRepository.deleteById(id);

        return "redirect:/plano-contas";
    }

    @GetMapping("/desabilitar/{id}")
    public String desabilitar(@PathVariable Integer id) {

        PlanoContas conta = planoContasRepository.findById(id).orElseThrow();

        conta.setAtivo(false);

        planoContasRepository.save(conta);

        return "redirect:/plano-contas";
    }

    @GetMapping("/habilitar/{id}")
    public String habilitar(@PathVariable Integer id) {

        PlanoContas conta = planoContasRepository.findById(id).orElseThrow();

        conta.setAtivo(true);

        planoContasRepository.save(conta);

        return "redirect:/plano-contas";
    }
}