package com.erp.microerp.controller;

import com.erp.microerp.model.Produto;
import com.erp.microerp.repository.MovimentacaoFinanceiraRepository;
import com.erp.microerp.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RelatorioController {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoFinanceiraRepository financeiroRepository;

    public RelatorioController(
            ProdutoRepository produtoRepository,
            MovimentacaoFinanceiraRepository financeiroRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.financeiroRepository = financeiroRepository;
    }

    @GetMapping("/relatorios")
    public String relatorios(Model model) {

        List<Produto> produtos = produtoRepository.findAll();

        List<Produto> estoqueBaixo = produtos.stream()
                .filter(p -> p.getEstoque() != null)
                .filter(p -> p.getEstoque_minimo() != null)
                .filter(p -> p.getEstoque() <= p.getEstoque_minimo())
                .collect(Collectors.toList());

        BigDecimal totalCreditos = financeiroRepository.totalCreditos();
        BigDecimal totalDebitos = financeiroRepository.totalDebitos();
        BigDecimal saldo = totalCreditos.subtract(totalDebitos);

        model.addAttribute("produtos", produtos);
        model.addAttribute("estoqueBaixo", estoqueBaixo);

        model.addAttribute("totalCreditos", totalCreditos);
        model.addAttribute("totalDebitos", totalDebitos);
        model.addAttribute("saldo", saldo);

        model.addAttribute("totalVendas", financeiroRepository.totalVendas());
        model.addAttribute("totalCompras", financeiroRepository.totalCompras());
        model.addAttribute("totalPerdas", financeiroRepository.totalPerdas());

        return "relatorios";
    }
}