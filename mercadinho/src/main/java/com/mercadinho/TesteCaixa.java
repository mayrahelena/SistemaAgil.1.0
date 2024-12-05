package com.mercadinho;

import com.mayra.mercadinho.dao.CaixaDAO;
import com.mayra.mercadinho.dao.ProdutoDAO;
import com.mayra.mercadinho.service.CaixaService;
import com.mayra.mercadinho.service.EstoqueService;
import com.mayra.mercadinho.model.Produto;
import javax.swing.SwingUtilities;
import com.mayra.mercadinho.view.CaixaView;
import java.sql.*;
import java.util.Scanner;

public class TesteCaixa {

    private static Connection connection;
    private static CaixaService caixaService;
    private static CaixaDAO caixaDAO;
    private static ProdutoDAO produtoDAO;
    private static EstoqueService estoqueService;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CaixaView caixaView = new CaixaView();
            caixaView.setVisible(true);
        });
        
        try {
            // Estabelecendo a conexão com o banco de dados
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mercadinho_db", "root", "021998@Amor");

            // Inicializando as classes DAO e Service
            caixaDAO = new CaixaDAO(connection);
            produtoDAO = new ProdutoDAO(connection);
            estoqueService = new EstoqueService(produtoDAO);
            caixaService = new CaixaService(caixaDAO, produtoDAO, estoqueService);

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

            connection.close(); // Fechando a conexão ao final
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Abrir o caixa com saldo inicial
    private static void abrirCaixa(Scanner scanner) {
        System.out.print("Digite o saldo inicial para o caixa: ");
        double saldoInicial = scanner.nextDouble();

        // Substitua pelo ID do operador correto (ex.: operador logado no sistema)
        int operadorId = 1; // Este é um valor de exemplo, ajuste conforme necessário.

        // Cria uma instância de CaixaService para chamar o método de instância
        CaixaService caixaService = new CaixaService();
        caixaService.abrirCaixa(saldoInicial, operadorId); // Passe os dois parâmetros.

        System.out.println("Caixa aberto com saldo inicial de: R$" + saldoInicial);
    }

    // Registrar uma venda no caixa com opção de pagamento
    public static void registrarVenda(Scanner scanner) throws SQLException {
    System.out.print("Digite o ID do produto: ");
    int produtoId = scanner.nextInt();
    System.out.print("Digite a quantidade vendida: ");
    int quantidadeVendida = scanner.nextInt();

    // Verificar estoque antes de registrar a venda
    int estoqueDisponivel = caixaService.verificarEstoque(produtoId);

    if (estoqueDisponivel >= quantidadeVendida) {
        // Buscar o preço do produto no banco de dados
        Produto produto = produtoDAO.buscarProdutoPorId(produtoId);
        double precoUnitario = produto.getPreco();

        // Calcular o valor total da venda
        double totalVenda = precoUnitario * quantidadeVendida;
        System.out.println("Total da venda: R$ " + totalVenda);

        // Primeiro pagamento
        System.out.println("Escolha o  meio de pagamento:");
        System.out.println("1. Dinheiro");
        System.out.println("2. Cartão");
        System.out.println("3. Pix");
        System.out.print("Digite a opção do pagamento: ");
        int opcaoPagamento1 = scanner.nextInt();

        String metodoPagamento1 = "";
        double valorPago1 = 0.0;

        switch (opcaoPagamento1) {
            case 1:
                metodoPagamento1 = "Dinheiro";
                System.out.print("Digite o valor pago pelo cliente: ");
                valorPago1 = scanner.nextDouble();
                break;
            case 2:
                metodoPagamento1 = "Cartão";
                break;
            case 3:
                metodoPagamento1 = "Pix";
                break;
            default:
                System.out.println("Opção inválida! Considerando pagamento em Dinheiro.");
                metodoPagamento1 = "Dinheiro";
                break;
        }

        // Segundo pagamento se necessário
        double valorPago2 = 0.0;
        String metodoPagamento2 = "";
        double totalPago = valorPago1; // Inicializa com o primeiro pagamento

        if (valorPago1 < totalVenda) {
            System.out.println("Escolha o segundo meio de pagamento (caso queira dividir o pagamento):");
            System.out.println("1. Dinheiro");
            System.out.println("2. Cartão");
            System.out.println("3. Pix");
            System.out.print("Digite a opção do segundo pagamento: ");
            int opcaoPagamento2 = scanner.nextInt();

            switch (opcaoPagamento2) {
                case 1:
                    metodoPagamento2 = "Dinheiro";
                    System.out.print("Digite o valor pago pelo cliente (segundo pagamento): ");
                    valorPago2 = scanner.nextDouble();
                    break;
                case 2:
                    metodoPagamento2 = "Cartão";
                    break;
                case 3:
                    metodoPagamento2 = "Pix";
                    break;
                default:
                    System.out.println("Opção inválida! Considerando pagamento em Dinheiro.");
                    metodoPagamento2 = "Dinheiro";
                    break;
            }

            totalPago += valorPago2; // Soma os dois pagamentos
        }

        // Calcular o troco
        double troco = totalPago - totalVenda;

        // Verificar se o total pago cobre o valor total da venda
        if (totalPago >= totalVenda) {
            // Registrar a venda com os dois pagamentos
            caixaService.registrarVenda(produtoId, quantidadeVendida, metodoPagamento1, metodoPagamento2, troco);
            System.out.println("Venda registrada com sucesso!");
            if (troco > 0) {
                System.out.println("Troco a ser devolvido: R$ " + troco);
            }
        } else {
            System.out.println("Valor total não coberto! Venda cancelada.");
        }
    } else {
        System.out.println("Estoque insuficiente! Disponível: " + estoqueDisponivel);
    }
}



    // Fechar o caixa
    private static void fecharCaixa() {
        caixaService.fecharCaixa();
        System.out.println("Caixa fechado com sucesso!");
    }
}
