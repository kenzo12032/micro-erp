package com.erp.microerp.controller;

import com.erp.microerp.model.*;
import com.erp.microerp.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class BalancoController {

    private final ProdutoRepository produtoRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final MovimentacaoFinanceiraRepository movimentacaoRepository;

    public BalancoController(
            ProdutoRepository produtoRepository,
            ContaFinanceiraRepository contaFinanceiraRepository,
            MovimentacaoFinanceiraRepository movimentacaoRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @GetMapping("/balanco")
    public String balanco(Model model) {

        BigDecimal caixa = movimentacaoRepository.findAll()
                .stream()
                .map(m -> {
                    if ("CREDITO".equalsIgnoreCase(m.getTipo())) {
                        return m.getValor();
                    } else {
                        return m.getValor().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal estoque = produtoRepository.findAll()
                .stream()
                .map(p -> {
                    Double custo = p.getCusto_medio() == null ? 0.0 : p.getCusto_medio();
                    Integer qtd = p.getEstoque() == null ? 0 : p.getEstoque();
                    return BigDecimal.valueOf(custo * qtd);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal contasReceber = contaFinanceiraRepository.findAll()
                .stream()
                .filter(c -> "RECEBER".equalsIgnoreCase(c.getTipo()))
                .filter(c -> "PENDENTE".equalsIgnoreCase(c.getStatus()) || "ATRASADO".equalsIgnoreCase(c.getStatus()))
                .map(ContaFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal contasPagar = contaFinanceiraRepository.findAll()
                .stream()
                .filter(c -> "PAGAR".equalsIgnoreCase(c.getTipo()))
                .filter(c -> "PENDENTE".equalsIgnoreCase(c.getStatus()) || "ATRASADO".equalsIgnoreCase(c.getStatus()))
                .map(ContaFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAtivo = caixa.add(estoque).add(contasReceber);
        BigDecimal totalPassivo = contasPagar;
        BigDecimal patrimonioLiquido = totalAtivo.subtract(totalPassivo);

        model.addAttribute("caixa", caixa);
        model.addAttribute("estoque", estoque);
        model.addAttribute("contasReceber", contasReceber);
        model.addAttribute("contasPagar", contasPagar);
        model.addAttribute("totalAtivo", totalAtivo);
        model.addAttribute("totalPassivo", totalPassivo);
        model.addAttribute("patrimonioLiquido", patrimonioLiquido);

        return "balanco";
    }
}