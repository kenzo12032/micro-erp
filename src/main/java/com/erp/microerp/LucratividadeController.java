package com.erp.microerp.controller;

import com.erp.microerp.model.Produto;
import com.erp.microerp.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LucratividadeController {

    private final ProdutoRepository produtoRepository;

    public LucratividadeController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/lucratividade")
    public String lucratividade(Model model) {

        List<Produto> produtos = produtoRepository.findAll();

        List<ProdutoLucratividadeDTO> relatorio = new ArrayList<>();

        for (Produto produto : produtos) {

            Double precoVenda =
                    produto.getPreco_venda() == null
                            ? 0.0
                            : produto.getPreco_venda();

            Double custoMedio =
                    produto.getCusto_medio() == null ? 0.0 : produto.getCusto_medio();

            BigDecimal lucroUnitario =
                    BigDecimal.valueOf(precoVenda - custoMedio);

            BigDecimal margem =
                    precoVenda == 0
                            ? BigDecimal.ZERO
                            : lucroUnitario
                            .divide(BigDecimal.valueOf(precoVenda), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

            relatorio.add(new ProdutoLucratividadeDTO(
                    produto.getNome(),
                    BigDecimal.valueOf(precoVenda),
                    BigDecimal.valueOf(custoMedio),
                    lucroUnitario,
                    margem
            ));
        }

        model.addAttribute("relatorio", relatorio);

        return "lucratividade";
    }

    public static class ProdutoLucratividadeDTO {

        private String produto;
        private BigDecimal precoVenda;
        private BigDecimal custoMedio;
        private BigDecimal lucroUnitario;
        private BigDecimal margem;

        public ProdutoLucratividadeDTO(
                String produto,
                BigDecimal precoVenda,
                BigDecimal custoMedio,
                BigDecimal lucroUnitario,
                BigDecimal margem
        ) {
            this.produto = produto;
            this.precoVenda = precoVenda;
            this.custoMedio = custoMedio;
            this.lucroUnitario = lucroUnitario;
            this.margem = margem;
        }

        public String getProduto() {
            return produto;
        }

        public BigDecimal getPrecoVenda() {
            return precoVenda;
        }

        public BigDecimal getCustoMedio() {
            return custoMedio;
        }

        public BigDecimal getLucroUnitario() {
            return lucroUnitario;
        }

        public BigDecimal getMargem() {
            return margem;
        }
    }
}