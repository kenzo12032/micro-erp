package com.erp.microerp.controller;

import com.erp.microerp.model.Produto;
import com.erp.microerp.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        return "produtos";
    }

    @GetMapping("/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto-form";
    }

    @GetMapping("/editar/{id}")
    public String editarProduto(@PathVariable Integer id, Model model) {
        Produto produto = produtoRepository.findById(id).orElseThrow();
        model.addAttribute("produto", produto);
        return "produto-form";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {

        if (produto.getId() == null) {
            produto.setSku(gerarSku());
            produto.setAtivo(true);

            if (produto.getCusto_medio() == null) {
                produto.setCusto_medio(produto.getPreco_custo());
            }

        } else {
            Produto antigo = produtoRepository.findById(produto.getId()).orElseThrow();

            produto.setSku(antigo.getSku());
            produto.setAtivo(antigo.getAtivo());

            if (produto.getCusto_medio() == null) {
                produto.setCusto_medio(antigo.getCusto_medio());
            }
        }

        produtoRepository.save(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/desabilitar/{id}")
    public String desabilitarProduto(@PathVariable Integer id) {
        Produto produto = produtoRepository.findById(id).orElseThrow();
        produto.setAtivo(false);
        produtoRepository.save(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/habilitar/{id}")
    public String habilitarProduto(@PathVariable Integer id) {
        Produto produto = produtoRepository.findById(id).orElseThrow();
        produto.setAtivo(true);
        produtoRepository.save(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable Integer id) {
        produtoRepository.deleteById(id);
        return "redirect:/produtos";
    }

    private String gerarSku() {
        return "SKU-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}