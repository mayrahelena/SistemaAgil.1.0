package com.mercadinho;

import com.mayra.mercadinho.dao.CaixaDAO;
import com.mayra.mercadinho.service.CaixaService;
import com.mayra.mercadinho.dao.ProdutoDAO;
import com.mayra.mercadinho.service.EstoqueService;
import com.mayra.mercadinho.model.Venda;
import java.sql.*;
import java.util.Scanner;

public class TesteCaixa {
    private static Connection connection;
    private static CaixaService CaixaService;
    private static CaixaDAO CaixaDAO;
    private static ProdutoDAO ProdutoDAO;
    private static EstoqueService EstoqueService;
    
    public static void main(String[] args) {
        try {
            // Configuração da conexão com o banco de dados
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mercadinho_db", "root", "senha");
            CaixaDAO = new CaixaDAO(connection);
            ProdutoDAO = new ProdutoDAO(connection);
            EstoqueService = new EstoqueService(ProdutoDAO);
            CaixaService = new CaixaService(CaixaDAO, ProdutoDAO, EstoqueService);

            Scanner scanner = new Scanner(System.in);
            int opcao;
            
            do {
                System.out.println("\n===== MENU CAIXA =====");
                System.out.println("1. Abrir caixa");
                System.out.println("2. Registrar venda");
                System.out.println("3. Fechar caixa");
                System.out.println("4. Sair");
                System.out.print("Escolha uma opção: ");
                opcao = scanner.nextInt();

                switch (opcao) {
                    case 1:
                        abrirCaixa(scanner);
                        break;
                    case 2:
                        registrarVenda(scanner);
                        break;
                    case 3:
                        fecharCaixa();
                        break;
                    case 4:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } while (opcao != 4);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Abrir o caixa com saldo inicial
    private static void abrirCaixa(Scanner scanner) {
        System.out.print("Digite o saldo inicial para o caixa: ");
        double saldoInicial = scanner.nextDouble();
        CaixaService.abrirCaixa(saldoInicial);
        System.out.println("Caixa aberto com saldo inicial de: R$" + saldoInicial);
    }

    // Registrar uma venda no caixa com opção de pagamento
    private static void registrarVenda(Scanner scanner) {
        System.out.print("Digite o ID do produto: ");
        int produtoId = scanner.nextInt();
        System.out.print("Digite a quantidade vendida: ");
        int quantidadeVendida = scanner.nextInt();
        
        // Verificar estoque antes de registrar a venda
        int estoqueDisponivel = CaixaService.verificarEstoque(produtoId);
        
        if (estoqueDisponivel >= quantidadeVendida) {
            // Registrar a venda
            System.out.println("Escolha o meio de pagamento:");
            System.out.println("1. Dinheiro");
            System.out.println("2. Cartão");
            System.out.println("3. Pix");
            System.out.print("Digite a opção de pagamento: ");
            int opcaoPagamento = scanner.nextInt();
            
            String metodoPagamento = "";
            double valorPago = 0.0;
            double troco = 0.0;
            
            switch (opcaoPagamento) {
                case 1:
                    metodoPagamento = "Dinheiro";
                    System.out.print("Digite o valor pago pelo cliente: ");
                    valorPago = scanner.nextDouble();
                    
                    // Calcular o troco
                    double totalVenda = CaixaService.calcularTotalVenda(produtoId, quantidadeVendida);
                    if (valorPago >= totalVenda) {
                        troco = valorPago - totalVenda;
                        System.out.println("Troco a ser devolvido: R$" + troco);
                    } else {
                        System.out.println("Valor insuficiente para o pagamento! Venda cancelada.");
                        return; // Retorna sem registrar a venda
                    }
                    break;
                case 2:
                    metodoPagamento = "Cartão";
                    break;
                case 3:
                    metodoPagamento = "Pix";
                    break;
                default:
                    System.out.println("Opção inválida! Considerando pagamento em Dinheiro.");
                    metodoPagamento = "Dinheiro";
            }

            // Registrar a venda e atualizar o estoque
            CaixaService.registrarVenda(produtoId, quantidadeVendida, metodoPagamento, troco);
            System.out.println("Venda registrada com sucesso!");
        } else {
            System.out.println("Estoque insuficiente! Disponível: " + estoqueDisponivel);
        }
    }

    // Fechar o caixa
    private static void fecharCaixa() {
        CaixaService.fecharCaixa();
        System.out.println("Caixa fechado com sucesso!");
    }
}


