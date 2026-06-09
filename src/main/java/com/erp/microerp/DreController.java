package com.erp.microerp.controller;

import com.erp.microerp.model.MovimentacaoFinanceira;
import com.erp.microerp.repository.MovimentacaoFinanceiraRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
public class DreController {

    private final MovimentacaoFinanceiraRepository movimentacaoRepository;

    public DreController(MovimentacaoFinanceiraRepository movimentacaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @GetMapping("/dre")
    public String dre(Model model) {

        List<MovimentacaoFinanceira> movimentacoes =
                movimentacaoRepository.findAll();

        BigDecimal receitaBruta = movimentacoes.stream()
                .filter(m -> "VENDA".equalsIgnoreCase(m.getOrigem()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal compras = movimentacoes.stream()
                .filter(m -> "COMPRA".equalsIgnoreCase(m.getOrigem()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal perdas = movimentacoes.stream()
                .filter(m -> "PERDA_ESTOQUE".equalsIgnoreCase(m.getOrigem()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Simples Nacional fixo 10%
        BigDecimal impostoSimples = receitaBruta
                .multiply(new BigDecimal("0.10"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal lucroLiquido = receitaBruta
                .subtract(impostoSimples)
                .subtract(compras)
                .subtract(perdas);

        model.addAttribute("receitaBruta", receitaBruta);
        model.addAttribute("compras", compras);
        model.addAttribute("perdas", perdas);
        model.addAttribute("impostoSimples", impostoSimples);
        model.addAttribute("lucroLiquido", lucroLiquido);

        return "dre";
    }
}