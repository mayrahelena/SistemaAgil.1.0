
package com.mayra.mercadinho.controller;

import com.mayra.mercadinho.model.Venda;
import com.mayra.mercadinho.service.CaixaService;

public class CaixaController {
    private CaixaService CaixaService;

    // Injeção via construtor
    public CaixaController(CaixaService caixaService) {
        this.CaixaService = caixaService;
    }

    // Método para abrir o caixa com saldo inicial
    public void abrirCaixa(double saldoInicial) {
        try {
            CaixaService.abrirCaixa(saldoInicial);
            // Atualizar UI com "Caixa aberto"
            System.out.println("Caixa aberto com saldo inicial de: R$" + saldoInicial);
        } catch (Exception e) {
            // Tratar exceções e mostrar mensagem de erro na UI
            System.err.println("Erro ao abrir o caixa: " + e.getMessage());
        }
    }

    // Método para fechar o caixa
    public void fecharCaixa() {
        try {
            CaixaService.fecharCaixa();
            // Exibir relatório e atualizar UI
            System.out.println("Caixa fechado com sucesso!");
            // Aqui você pode chamar um método que exibe os totais de vendas, entradas e saídas
        } catch (Exception e) {
            // Tratar exceções e mostrar mensagem de erro na UI
            System.err.println("Erro ao fechar o caixa: " + e.getMessage());
        }
    }

    // Método para registrar uma venda
    public void registrarVenda(Venda venda) {
        try {
            CaixaService.registrarVenda(venda);
            // Atualizar UI com "Venda registrada"
            System.out.println("Venda registrada com sucesso! Produto: " + venda.getProdutoId() + ", Quantidade: " + venda.getQuantidade());
        } catch (Exception e) {
            // Tratar exceções e mostrar mensagem de erro na UI
            System.err.println("Erro ao registrar a venda: " + e.getMessage());
        }
    }
}
