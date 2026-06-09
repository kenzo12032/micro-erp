package com.erp.microerp.controller;

import com.erp.microerp.model.*;
import com.erp.microerp.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final MovimentacaoFinanceiraRepository financeiroRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final CompraRepository compraRepository;
    private final PerdaEstoqueRepository perdaEstoqueRepository;

    public DashboardController(
            MovimentacaoFinanceiraRepository financeiroRepository,
            ContaFinanceiraRepository contaFinanceiraRepository,
            ProdutoRepository produtoRepository,
            VendaRepository vendaRepository,
            CompraRepository compraRepository,
            PerdaEstoqueRepository perdaEstoqueRepository
    ) {
        this.financeiroRepository = financeiroRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
        this.compraRepository = compraRepository;
        this.perdaEstoqueRepository = perdaEstoqueRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "MES") String periodo,
                            Model model) {

        LocalDateTime inicio = definirInicio(periodo);
        LocalDateTime fim = LocalDateTime.now();

        List<MovimentacaoFinanceira> movimentacoesFiltradas =
                financeiroRepository.findAll()
                        .stream()
                        .filter(m -> m.getDataMovimentacao() != null)
                        .filter(m -> !m.getDataMovimentacao().isBefore(inicio))
                        .filter(m -> !m.getDataMovimentacao().isAfter(fim))
                        .collect(Collectors.toList());

        BigDecimal totalCreditos = movimentacoesFiltradas.stream()
                .filter(m -> "CREDITO".equalsIgnoreCase(m.getTipo()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebitos = movimentacoesFiltradas.stream()
                .filter(m -> "DEBITO".equalsIgnoreCase(m.getTipo()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalCreditos.subtract(totalDebitos);

        BigDecimal totalVendas = movimentacoesFiltradas.stream()
                .filter(m -> "VENDA".equalsIgnoreCase(m.getOrigem()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCompras = movimentacoesFiltradas.stream()
                .filter(m -> "COMPRA".equalsIgnoreCase(m.getOrigem()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPerdas = movimentacoesFiltradas.stream()
                .filter(m -> "PERDA_ESTOQUE".equalsIgnoreCase(m.getOrigem()))
                .map(MovimentacaoFinanceira::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lucroEstimado = totalVendas.subtract(totalCompras).subtract(totalPerdas);

        List<Produto> estoqueBaixo = produtoRepository.findAll()
                .stream()
                .filter(p -> p.getEstoque() != null)
                .filter(p -> p.getEstoque_minimo() != null)
                .filter(p -> p.getEstoque() <= p.getEstoque_minimo())
                .collect(Collectors.toList());

        long contasPendentes = contaFinanceiraRepository.findAll()
                .stream()
                .filter(c -> "PENDENTE".equalsIgnoreCase(c.getStatus()))
                .count();

        String produtoMaisVendido = calcularProdutoMaisVendido();
        String clienteMaisComprou = calcularClienteMaisComprou();
        String fornecedorMaisCompras = calcularFornecedorMaisCompras();
        String maiorPrejuizoProduto = calcularMaiorPrejuizoProduto();

        model.addAttribute("periodo", periodo);

        model.addAttribute("totalCreditos", totalCreditos);
        model.addAttribute("totalDebitos", totalDebitos);
        model.addAttribute("saldo", saldo);

        model.addAttribute("totalVendas", totalVendas);
        model.addAttribute("totalCompras", totalCompras);
        model.addAttribute("totalPerdas", totalPerdas);
        model.addAttribute("lucroEstimado", lucroEstimado);

        model.addAttribute("qtdEstoqueBaixo", estoqueBaixo.size());
        model.addAttribute("contasPendentes", contasPendentes);

        model.addAttribute("produtoMaisVendido", produtoMaisVendido);
        model.addAttribute("clienteMaisComprou", clienteMaisComprou);
        model.addAttribute("fornecedorMaisCompras", fornecedorMaisCompras);
        model.addAttribute("maiorPrejuizoProduto", maiorPrejuizoProduto);

        return "dashboard";
    }

    private String calcularProdutoMaisVendido() {
        Map<String, Integer> mapa = new HashMap<>();

        for (Venda venda : vendaRepository.findAll()) {
            for (VendaItem item : venda.getItens()) {
                String nome = item.getProduto().getNome();
                mapa.put(nome, mapa.getOrDefault(nome, 0) + item.getQuantidade());
            }
        }

        return mapa.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + " un.)")
                .orElse("Sem vendas");
    }

    private String calcularClienteMaisComprou() {
        Map<String, BigDecimal> mapa = new HashMap<>();

        for (Venda venda : vendaRepository.findAll()) {
            String nome = venda.getCliente().getNome();
            BigDecimal total = venda.getValorTotal();

            mapa.put(nome, mapa.getOrDefault(nome, BigDecimal.ZERO).add(total));
        }

        return mapa.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " - R$ " + e.getValue())
                .orElse("Sem clientes");
    }

    private String calcularFornecedorMaisCompras() {
        Map<String, BigDecimal> mapa = new HashMap<>();

        for (Compra compra : compraRepository.findAll()) {
            String nome = compra.getFornecedor().getNome();
            BigDecimal total = compra.getValorTotal();

            mapa.put(nome, mapa.getOrDefault(nome, BigDecimal.ZERO).add(total));
        }

        return mapa.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " - R$ " + e.getValue())
                .orElse("Sem compras");
    }

    private String calcularMaiorPrejuizoProduto() {
        Map<String, BigDecimal> mapa = new HashMap<>();

        for (PerdaEstoque perda : perdaEstoqueRepository.findAll()) {
            String nome = perda.getProduto().getNome();
            BigDecimal prejuizo = perda.getValorPrejuizo();

            mapa.put(nome, mapa.getOrDefault(nome, BigDecimal.ZERO).add(prejuizo));
        }

        return mapa.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " - R$ " + e.getValue())
                .orElse("Sem perdas");
    }

    private LocalDateTime definirInicio(String periodo) {
        LocalDate hoje = LocalDate.now();

        switch (periodo) {
            case "HOJE":
                return hoje.atStartOfDay();

            case "SEMANA":
                return hoje.minusDays(7).atStartOfDay();

            case "ANO":
                return hoje.withDayOfYear(1).atStartOfDay();

            case "MES":
            default:
                return hoje.withDayOfMonth(1).atStartOfDay();
        }
    }
}