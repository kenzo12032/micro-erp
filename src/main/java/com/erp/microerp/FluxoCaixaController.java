package com.erp.microerp.controller;

import com.erp.microerp.model.ContaFinanceira;
import com.erp.microerp.model.MovimentacaoFinanceira;
import com.erp.microerp.repository.ContaFinanceiraRepository;
import com.erp.microerp.repository.MovimentacaoFinanceiraRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FluxoCaixaController {

    private final MovimentacaoFinanceiraRepository movimentacaoRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;

    public FluxoCaixaController(
            MovimentacaoFinanceiraRepository movimentacaoRepository,
            ContaFinanceiraRepository contaFinanceiraRepository
    ) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
    }

    @GetMapping("/fluxo-caixa")
    public String fluxoCaixa(Model model) {

        atualizarContasAtrasadas();

        List<MovimentacaoFinanceira> movimentacoes = movimentacaoRepository.findAll();

        BigDecimal entradas = movimentacoes.stream()
                .filter(m -> "CREDITO".equalsIgnoreCase(m.getTipo()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saidas = movimentacoes.stream()
                .filter(m -> "DEBITO".equalsIgnoreCase(m.getTipo()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = entradas.subtract(saidas);

        LocalDate hoje = LocalDate.now();
        LocalDate amanha = hoje.plusDays(1);

        List<ContaFinanceira> vencendoHoje = contaFinanceiraRepository.findAll()
                .stream()
                .filter(c -> "PENDENTE".equalsIgnoreCase(c.getStatus()))
                .filter(c -> c.getDataVencimento() != null)
                .filter(c -> c.getDataVencimento().isEqual(hoje))
                .collect(Collectors.toList());

        List<ContaFinanceira> vencendoAmanha = contaFinanceiraRepository.findAll()
                .stream()
                .filter(c -> "PENDENTE".equalsIgnoreCase(c.getStatus()))
                .filter(c -> c.getDataVencimento() != null)
                .filter(c -> c.getDataVencimento().isEqual(amanha))
                .collect(Collectors.toList());

        List<ContaFinanceira> atrasadas = contaFinanceiraRepository.findAll()
                .stream()
                .filter(c -> "ATRASADO".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("movimentacoes", movimentacoes);
        model.addAttribute("entradas", entradas);
        model.addAttribute("saidas", saidas);
        model.addAttribute("saldo", saldo);

        model.addAttribute("vencendoHoje", vencendoHoje);
        model.addAttribute("vencendoAmanha", vencendoAmanha);
        model.addAttribute("atrasadas", atrasadas);

        model.addAttribute("qtdHoje", vencendoHoje.size());
        model.addAttribute("qtdAmanha", vencendoAmanha.size());
        model.addAttribute("qtdAtrasadas", atrasadas.size());

        return "fluxo-caixa";
    }

    private void atualizarContasAtrasadas() {
        LocalDate hoje = LocalDate.now();

        for (ContaFinanceira conta : contaFinanceiraRepository.findAll()) {
            if ("PENDENTE".equalsIgnoreCase(conta.getStatus())
                    && conta.getDataVencimento() != null
                    && conta.getDataVencimento().isBefore(hoje)) {

                conta.setStatus("ATRASADO");
                contaFinanceiraRepository.save(conta);
            }
        }
    }
}