package com.erp.microerp;

import com.erp.microerp.model.Compra;
import com.erp.microerp.model.ContaFinanceira;
import com.erp.microerp.model.Produto;
import com.erp.microerp.repository.CompraRepository;
import com.erp.microerp.repository.ContaFinanceiraRepository;
import com.erp.microerp.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProdutoRepository produtoRepository;
    private final CompraRepository compraRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;

    public HomeController(
            ProdutoRepository produtoRepository,
            CompraRepository compraRepository,
            ContaFinanceiraRepository contaFinanceiraRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.compraRepository = compraRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {

        // ESTOQUE BAIXO
        List<Produto> estoqueBaixo = produtoRepository.findAll()
                .stream()
                .filter(p -> p.getEstoque() != null)
                .filter(p -> p.getEstoque_minimo() != null)
                .filter(p -> p.getEstoque() <= p.getEstoque_minimo())
                .collect(Collectors.toList());

        // COMPRAS PENDENTES
        List<Compra> comprasPendentes = compraRepository.findAll()
                .stream()
                .filter(c -> "PENDENTE".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());

        // CONTAS VENCIDAS
        List<ContaFinanceira> contasVencidas = contaFinanceiraRepository.findAll()
                .stream()
                .filter(c -> "ATRASADO".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());

        // CONTAS PENDENTES
        List<ContaFinanceira> contasPendentes = contaFinanceiraRepository.findAll()
                .stream()
                .filter(c -> "PENDENTE".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("qtdEstoqueBaixo", estoqueBaixo.size());

        model.addAttribute("qtdComprasPendentes", comprasPendentes.size());

        model.addAttribute("qtdContasVencidas", contasVencidas.size());

        model.addAttribute("qtdContasPendentes", contasPendentes.size());

        return "home";
    }
}