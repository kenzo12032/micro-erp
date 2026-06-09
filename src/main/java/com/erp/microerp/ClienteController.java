package com.erp.microerp.controller;

import com.erp.microerp.model.Cliente;
import com.erp.microerp.repository.ClienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("cliente", new Cliente());
        return "clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow();
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("cliente", cliente);
        return "clientes";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cliente cliente) {

        if (cliente.getId() == null) {
            cliente.setAtivo(true);
        } else {
            Cliente antigo = clienteRepository.findById(cliente.getId()).orElseThrow();
            cliente.setAtivo(antigo.getAtivo());
        }

        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        clienteRepository.deleteById(id);
        return "redirect:/clientes";
    }

    @GetMapping("/desabilitar/{id}")
    public String desabilitar(@PathVariable Integer id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow();
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/habilitar/{id}")
    public String habilitar(@PathVariable Integer id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow();
        cliente.setAtivo(true);
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }
}