package com.erp.microerp.controller;

import com.erp.microerp.model.MovimentacaoFinanceira;
import com.erp.microerp.repository.MovimentacaoFinanceiraRepository;
import com.erp.microerp.repository.PlanoContasRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    private final MovimentacaoFinanceiraRepository movimentacaoRepository;
    private final PlanoContasRepository planoContasRepository;

    public FinanceiroController(
            MovimentacaoFinanceiraRepository movimentacaoRepository,
            PlanoContasRepository planoContasRepository
    ) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.planoContasRepository = planoContasRepository;
    }

    @GetMapping
    public String financeiro(Model model) {

        List<MovimentacaoFinanceira> movimentacoes = movimentacaoRepository.findAll();

        BigDecimal totalCreditos = movimentacoes.stream()
                .filter(m -> "CREDITO".equalsIgnoreCase(m.getTipo()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebitos = movimentacoes.stream()
                .filter(m -> "DEBITO".equalsIgnoreCase(m.getTipo()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalCreditos.subtract(totalDebitos);

        model.addAttribute("movimentacoes", movimentacoes);
        model.addAttribute("contas", planoContasRepository.findAll());
        model.addAttribute("movimentacao", new MovimentacaoFinanceira());
        model.addAttribute("totalCreditos", totalCreditos);
        model.addAttribute("totalDebitos", totalDebitos);
        model.addAttribute("saldo", saldo);

        return "financeiro";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute MovimentacaoFinanceira movimentacao) {
        movimentacao.setStatus("EFETIVADA");
        movimentacao.setOrigem("MANUAL");
        movimentacaoRepository.save(movimentacao);

        return "redirect:/financeiro";
    }
}