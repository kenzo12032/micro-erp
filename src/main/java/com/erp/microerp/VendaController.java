package com.erp.microerp.controller;

import com.erp.microerp.model.ContaFinanceira;
import com.erp.microerp.model.MovimentacaoFinanceira;
import com.erp.microerp.model.Produto;
import com.erp.microerp.model.Venda;
import com.erp.microerp.model.VendaItem;
import com.erp.microerp.repository.ClienteRepository;
import com.erp.microerp.repository.ContaFinanceiraRepository;
import com.erp.microerp.repository.MovimentacaoFinanceiraRepository;
import com.erp.microerp.repository.ProdutoRepository;
import com.erp.microerp.repository.VendaRepository;
import com.erp.microerp.service.AuditoriaService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/vendas")
public class VendaController {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final AuditoriaService auditoriaService;

    public VendaController(
            VendaRepository vendaRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository,
            MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository,
            ContaFinanceiraRepository contaFinanceiraRepository,
            AuditoriaService auditoriaService
    ) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.movimentacaoFinanceiraRepository = movimentacaoFinanceiraRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vendas", vendaRepository.findAll());
        return "vendas";
    }

    @GetMapping("/nova")
    public String novaVenda(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("produtos", produtoRepository.findAll());
        return "venda-form";
    }

    @PostMapping("/salvar")
    public String salvarVenda(
            @RequestParam Integer clienteId,
            @RequestParam List<Integer> produtoId,
            @RequestParam List<Integer> quantidade,
            @RequestParam List<Double> valorUnitario,
            @RequestParam String formaPagamento,
            Model model,
            HttpSession session
    ) {

        Venda venda = new Venda();
        venda.setCliente(clienteRepository.findById(clienteId).orElseThrow());
        venda.setStatus("FINALIZADA");
        venda.setFormaPagamento(formaPagamento);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (int i = 0; i < produtoId.size(); i++) {

            Produto produto = produtoRepository.findById(produtoId.get(i)).orElseThrow();

            Integer qtd = quantidade.get(i);
            Double valor = valorUnitario.get(i);

            Integer estoqueAtual = produto.getEstoque() == null ? 0 : produto.getEstoque();

            if (qtd > estoqueAtual) {
                model.addAttribute("erro", "Estoque insuficiente para o produto: " + produto.getNome());
                model.addAttribute("clientes", clienteRepository.findAll());
                model.addAttribute("produtos", produtoRepository.findAll());
                return "venda-form";
            }

            BigDecimal subtotal = BigDecimal.valueOf(qtd * valor);

            VendaItem item = new VendaItem();
            item.setVenda(venda);
            item.setProduto(produto);
            item.setQuantidade(qtd);
            item.setValorUnitario(BigDecimal.valueOf(valor));
            item.setSubtotal(subtotal);

            venda.getItens().add(item);

            produto.setEstoque(estoqueAtual - qtd);
            produtoRepository.save(produto);

            valorTotal = valorTotal.add(subtotal);
        }

        venda.setValorTotal(valorTotal);

        Venda vendaSalva = vendaRepository.save(venda);

        ContaFinanceira contaReceber = new ContaFinanceira();
        contaReceber.setDescricao("Conta a receber da venda ID " + vendaSalva.getId());
        contaReceber.setTipo("RECEBER");
        contaReceber.setValor(vendaSalva.getValorTotal());
        contaReceber.setDataVencimento(LocalDate.now());
        contaReceber.setStatus("PENDENTE");
        contaReceber.setOrigem("VENDA");
        contaReceber.setReferenciaId(vendaSalva.getId());

        contaFinanceiraRepository.save(contaReceber);

        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
        movimentacao.setDescricao("Venda realizada - ID " + vendaSalva.getId() + " - " + formaPagamento);
        movimentacao.setTipo("CREDITO");
        movimentacao.setValor(vendaSalva.getValorTotal());
        movimentacao.setOrigem("VENDA");
        movimentacao.setReferenciaId(vendaSalva.getId());
        movimentacao.setStatus("EFETIVADA");

        movimentacaoFinanceiraRepository.save(movimentacao);

        auditoriaService.registrar(
                session,
                "VENDAS",
                "NOVA VENDA",
                "Venda ID " + vendaSalva.getId() + " realizada no valor de R$ " + vendaSalva.getValorTotal()
        );

        return "redirect:/vendas";
    }

    @GetMapping("/pdf/{id}")
    public void gerarPdf(@PathVariable Integer id,
                         HttpServletResponse response) throws Exception {

        Venda venda = vendaRepository.findById(id).orElseThrow();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "inline; filename=venda-" + id + ".pdf");

        Document document = new Document();

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.add(new Paragraph("MICRO ERP"));
        document.add(new Paragraph("----------------------------------"));
        document.add(new Paragraph("COMPROVANTE DE VENDA"));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Venda Nº: " + venda.getId()));
        document.add(new Paragraph("Cliente: " + venda.getCliente().getNome()));
        document.add(new Paragraph("Data: " + venda.getDataVenda()));
        document.add(new Paragraph("Status: " + venda.getStatus()));
        document.add(new Paragraph("Forma de pagamento: " + venda.getFormaPagamento()));

        document.add(new Paragraph(" "));
        document.add(new Paragraph("ITENS"));
        document.add(new Paragraph("----------------------------------"));

        for (VendaItem item : venda.getItens()) {
            document.add(new Paragraph(
                    item.getProduto().getNome()
                            + " | Qtd: " + item.getQuantidade()
                            + " | Valor: R$ " + item.getValorUnitario()
                            + " | Subtotal: R$ " + item.getSubtotal()
            ));
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("VALOR TOTAL: R$ " + venda.getValorTotal()));

        document.close();
    }
}