package com.erp.microerp.controller;

import com.erp.microerp.model.MovimentacaoFinanceira;
import com.erp.microerp.model.PerdaEstoque;
import com.erp.microerp.model.Produto;
import com.erp.microerp.repository.MovimentacaoFinanceiraRepository;
import com.erp.microerp.repository.PerdaEstoqueRepository;
import com.erp.microerp.repository.ProdutoRepository;
import com.erp.microerp.service.AuditoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/perdas")
public class PerdaEstoqueController {

    private final ProdutoRepository produtoRepository;
    private final PerdaEstoqueRepository perdaRepository;
    private final MovimentacaoFinanceiraRepository financeiroRepository;
    private final AuditoriaService auditoriaService;

    public PerdaEstoqueController(
            ProdutoRepository produtoRepository,
            PerdaEstoqueRepository perdaRepository,
            MovimentacaoFinanceiraRepository financeiroRepository,
            AuditoriaService auditoriaService
    ) {
        this.produtoRepository = produtoRepository;
        this.perdaRepository = perdaRepository;
        this.financeiroRepository = financeiroRepository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("perdas", perdaRepository.findAll());
        return "perdas";
    }

    @PostMapping("/salvar")
    public String salvar(
            @RequestParam Integer produtoId,
            @RequestParam Integer quantidade,
            @RequestParam String motivo,
            @RequestParam String observacao,
            HttpSession session
    ) {

        Produto produto = produtoRepository.findById(produtoId).orElseThrow();

        Integer estoqueAtual = produto.getEstoque() == null ? 0 : produto.getEstoque();

        if (quantidade > estoqueAtual) {
            return "redirect:/perdas";
        }

        produto.setEstoque(estoqueAtual - quantidade);

        BigDecimal prejuizo = BigDecimal.valueOf(
                produto.getCusto_medio() * quantidade
        );

        produtoRepository.save(produto);

        PerdaEstoque perda = new PerdaEstoque();
        perda.setProduto(produto);
        perda.setQuantidade(quantidade);
        perda.setMotivo(motivo);
        perda.setObservacao(observacao);
        perda.setValorPrejuizo(prejuizo);

        PerdaEstoque perdaSalva = perdaRepository.save(perda);

        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
        movimentacao.setDescricao("Perda de estoque - " + produto.getNome());
        movimentacao.setTipo("DEBITO");
        movimentacao.setValor(prejuizo);
        movimentacao.setOrigem("PERDA_ESTOQUE");
        movimentacao.setReferenciaId(perdaSalva.getId());
        movimentacao.setStatus("EFETIVADA");

        financeiroRepository.save(movimentacao);

        auditoriaService.registrar(
                session,
                "ESTOQUE",
                "PERDA",
                "Perda registrada do produto " + produto.getNome()
                        + " na quantidade " + quantidade
                        + " por motivo: " + motivo
        );

        return "redirect:/perdas";
    }
}