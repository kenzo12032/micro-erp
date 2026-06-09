package com.erp.microerp.controller;

import com.erp.microerp.model.ContaFinanceira;
import com.erp.microerp.model.MovimentacaoFinanceira;
import com.erp.microerp.repository.ContaFinanceiraRepository;
import com.erp.microerp.repository.MovimentacaoFinanceiraRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/contas-financeiras")
public class ContaFinanceiraController {

    private final ContaFinanceiraRepository contaRepository;
    private final MovimentacaoFinanceiraRepository movimentacaoRepository;

    public ContaFinanceiraController(
            ContaFinanceiraRepository contaRepository,
            MovimentacaoFinanceiraRepository movimentacaoRepository
    ) {
        this.contaRepository = contaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @GetMapping
    public String listar(Model model) {

        atualizarContasAtrasadas();

        model.addAttribute("contas", contaRepository.findAll());
        model.addAttribute("conta", new ContaFinanceira());

        return "contas-financeiras";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ContaFinanceira conta) {
        conta.setStatus("PENDENTE");
        conta.setOrigem("MANUAL");
        contaRepository.save(conta);

        return "redirect:/contas-financeiras";
    }

    @GetMapping("/baixar/{id}")
    public String baixar(@PathVariable Integer id) {

        ContaFinanceira conta = contaRepository.findById(id).orElseThrow();

        if (!"PENDENTE".equalsIgnoreCase(conta.getStatus())
                && !"ATRASADO".equalsIgnoreCase(conta.getStatus())) {
            return "redirect:/contas-financeiras";
        }

        conta.setDataPagamento(LocalDate.now());

        if ("PAGAR".equalsIgnoreCase(conta.getTipo())) {
            conta.setStatus("PAGO");
        } else {
            conta.setStatus("RECEBIDO");
        }

        contaRepository.save(conta);

        MovimentacaoFinanceira mov = new MovimentacaoFinanceira();
        mov.setDescricao("Baixa de conta - " + conta.getDescricao());
        mov.setValor(conta.getValor());
        mov.setOrigem("CONTA_FINANCEIRA");
        mov.setReferenciaId(conta.getId());
        mov.setStatus("EFETIVADA");

        if ("PAGAR".equalsIgnoreCase(conta.getTipo())) {
            mov.setTipo("DEBITO");
        } else {
            mov.setTipo("CREDITO");
        }

        movimentacaoRepository.save(mov);

        return "redirect:/contas-financeiras";
    }

    private void atualizarContasAtrasadas() {
        LocalDate hoje = LocalDate.now();

        for (ContaFinanceira conta : contaRepository.findAll()) {
            if ("PENDENTE".equalsIgnoreCase(conta.getStatus())
                    && conta.getDataVencimento() != null
                    && conta.getDataVencimento().isBefore(hoje)) {

                conta.setStatus("ATRASADO");
                contaRepository.save(conta);
            }
        }
    }
}