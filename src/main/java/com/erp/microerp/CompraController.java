package com.erp.microerp.controller;

import com.erp.microerp.model.Compra;
import com.erp.microerp.model.CompraItem;
import com.erp.microerp.model.ContaFinanceira;
import com.erp.microerp.model.MovimentacaoFinanceira;
import com.erp.microerp.model.Produto;
import com.erp.microerp.repository.CompraRepository;
import com.erp.microerp.repository.ContaFinanceiraRepository;
import com.erp.microerp.repository.FornecedorRepository;
import com.erp.microerp.repository.MovimentacaoFinanceiraRepository;
import com.erp.microerp.repository.ProdutoRepository;
import com.erp.microerp.service.AuditoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/compras")
public class CompraController {

    private final CompraRepository compraRepository;
    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final AuditoriaService auditoriaService;

    public CompraController(
            CompraRepository compraRepository,
            ProdutoRepository produtoRepository,
            FornecedorRepository fornecedorRepository,
            MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository,
            ContaFinanceiraRepository contaFinanceiraRepository,
            AuditoriaService auditoriaService
    ) {
        this.compraRepository = compraRepository;
        this.produtoRepository = produtoRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.movimentacaoFinanceiraRepository = movimentacaoFinanceiraRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("compras", compraRepository.findAll());
        return "compras";
    }

    @GetMapping("/nova")
    public String novaCompra(Model model) {
        model.addAttribute("fornecedores", fornecedorRepository.findAll());
        model.addAttribute("produtos", produtoRepository.findAll());
        return "compra-form";
    }

    @PostMapping("/salvar")
    public String salvarCompra(
            @RequestParam Integer fornecedorId,
            @RequestParam List<Integer> produtoId,
            @RequestParam List<Integer> quantidade,
            @RequestParam List<Double> valorUnitario,
            HttpSession session
    ) {
        Compra compra = new Compra();
        compra.setFornecedor(fornecedorRepository.findById(fornecedorId).orElseThrow());
        compra.setStatus("PENDENTE");

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (int i = 0; i < produtoId.size(); i++) {
            Produto produto = produtoRepository.findById(produtoId.get(i)).orElseThrow();

            Integer qtd = quantidade.get(i);
            Double valor = valorUnitario.get(i);

            BigDecimal subtotal = BigDecimal.valueOf(qtd * valor);

            CompraItem item = new CompraItem();
            item.setCompra(compra);
            item.setProduto(produto);
            item.setQuantidade(qtd);
            item.setValorUnitario(BigDecimal.valueOf(valor));
            item.setSubtotal(subtotal);

            compra.getItens().add(item);

            valorTotal = valorTotal.add(subtotal);
        }

        compra.setValorTotal(valorTotal);

        Compra compraSalva = compraRepository.save(compra);

        auditoriaService.registrar(
                session,
                "COMPRAS",
                "NOVA COMPRA",
                "Compra ID " + compraSalva.getId() + " cadastrada como PENDENTE no valor de R$ " + compraSalva.getValorTotal()
        );

        return "redirect:/compras";
    }

    @GetMapping("/confirmar/{id}")
    public String confirmarRecebimento(@PathVariable Integer id,
                                       HttpSession session) {

        Compra compra = compraRepository.findById(id).orElseThrow();

        if ("RECEBIDA".equalsIgnoreCase(compra.getStatus())) {
            return "redirect:/compras";
        }

        for (CompraItem item : compra.getItens()) {
            Produto produto = item.getProduto();

            Integer estoqueAtual = produto.getEstoque() == null ? 0 : produto.getEstoque();
            Double custoMedioAtual = produto.getCusto_medio() == null ? 0.0 : produto.getCusto_medio();

            Integer qtd = item.getQuantidade();
            Double valorUnitario = item.getValorUnitario().doubleValue();

            Integer novoEstoque = estoqueAtual + qtd;

            Double novoCustoMedio =
                    ((estoqueAtual * custoMedioAtual) + (qtd * valorUnitario)) / novoEstoque;

            produto.setEstoque(novoEstoque);
            produto.setCusto_medio(novoCustoMedio);

            produtoRepository.save(produto);
        }

        compra.setStatus("RECEBIDA");
        Compra compraSalva = compraRepository.save(compra);

        ContaFinanceira contaPagar = new ContaFinanceira();
        contaPagar.setDescricao("Conta a pagar da compra ID " + compraSalva.getId());
        contaPagar.setTipo("PAGAR");
        contaPagar.setValor(compraSalva.getValorTotal());
        contaPagar.setDataVencimento(LocalDate.now());
        contaPagar.setStatus("PENDENTE");
        contaPagar.setOrigem("COMPRA");
        contaPagar.setReferenciaId(compraSalva.getId());

        contaFinanceiraRepository.save(contaPagar);

        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
        movimentacao.setDescricao("Compra recebida - ID " + compraSalva.getId());
        movimentacao.setTipo("DEBITO");
        movimentacao.setValor(compraSalva.getValorTotal());
        movimentacao.setOrigem("COMPRA");
        movimentacao.setReferenciaId(compraSalva.getId());
        movimentacao.setStatus("EFETIVADA");

        movimentacaoFinanceiraRepository.save(movimentacao);

        auditoriaService.registrar(
                session,
                "COMPRAS",
                "RECEBIMENTO",
                "Compra ID " + compraSalva.getId() + " confirmada e lançada no financeiro"
        );

        return "redirect:/compras";
    }
}