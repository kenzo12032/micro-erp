package com.erp.microerp.controller;

import com.erp.microerp.model.Fornecedor;
import com.erp.microerp.repository.FornecedorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorController(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fornecedores", fornecedorRepository.findAll());
        model.addAttribute("fornecedor", new Fornecedor());
        return "fornecedores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow();
        model.addAttribute("fornecedores", fornecedorRepository.findAll());
        model.addAttribute("fornecedor", fornecedor);
        return "fornecedores";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Fornecedor fornecedor) {

        if (fornecedor.getId() == null) {
            fornecedor.setAtivo(true);
        } else {
            Fornecedor antigo = fornecedorRepository.findById(fornecedor.getId()).orElseThrow();
            fornecedor.setAtivo(antigo.getAtivo());
        }

        fornecedorRepository.save(fornecedor);
        return "redirect:/fornecedores";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        fornecedorRepository.deleteById(id);
        return "redirect:/fornecedores";
    }

    @GetMapping("/desabilitar/{id}")
    public String desabilitar(@PathVariable Integer id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow();
        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);
        return "redirect:/fornecedores";
    }

    @GetMapping("/habilitar/{id}")
    public String habilitar(@PathVariable Integer id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow();
        fornecedor.setAtivo(true);
        fornecedorRepository.save(fornecedor);
        return "redirect:/fornecedores";
    }
}