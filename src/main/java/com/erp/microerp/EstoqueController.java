package com.erp.microerp.controller;

import com.erp.microerp.model.Produto;
import com.erp.microerp.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class EstoqueController {

    private final ProdutoRepository produtoRepository;

    public EstoqueController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/estoque")
    public String estoque(Model model) {
        List<Produto> produtos = produtoRepository.findAll();
        model.addAttribute("produtos", produtos);
        return "estoque";
    }
}