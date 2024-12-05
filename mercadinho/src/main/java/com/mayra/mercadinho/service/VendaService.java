
package com.mayra.mercadinho.service;

import java.util.Scanner;
import com.mayra.mercadinho.dao.ProdutoDAO;
import java.sql.SQLException;

public class VendaService {

    // Método para realizar uma venda
    public void realizarVenda(Scanner scanner) throws SQLException {
        System.out.print("Digite o ID do produto: ");
        int produtoId = scanner.nextInt();

        System.out.print("Digite a quantidade vendida: ");
        int quantidade = scanner.nextInt();

        // Obter o preço do produto no banco de dados
        double precoProduto = buscarPrecoProduto(produtoId); // Método que busca o preço do produto

        if (precoProduto != -1) {
            // Calcular o total
            double totalVenda = precoProduto * quantidade;

            // Exibir o total
            System.out.println("Total da venda: R$" + totalVenda);

            // Agora, exibir as opções de pagamento
            System.out.println("Escolha o meio de pagamento:");
            System.out.println("1. Dinheiro");
            System.out.println("2. Cartão");
            System.out.println("3. PIX");

            int opcaoPagamento = scanner.nextInt();

            // Aqui você pode continuar com a lógica para processar o pagamento
            processarPagamento(opcaoPagamento, totalVenda);
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    // Método para buscar o preço do produto no banco de dados
    private double buscarPrecoProduto(int produtoId) throws SQLException {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        double precoProduto = produtoDAO.buscarPrecoPorId(produtoId); // Lógica de busca do preço
        return precoProduto;
    }

    // Método para processar o pagamento
    private void processarPagamento(int opcaoPagamento, double totalVenda) {
        switch (opcaoPagamento) {
            case 1:
                System.out.println("Pagamento feito em Dinheiro. Total: R$" + totalVenda);
                break;
            case 2:
                System.out.println("Pagamento feito no Cartão. Total: R$" + totalVenda);
                break;
            case 3:
                System.out.println("Pagamento feito via PIX. Total: R$" + totalVenda);
                break;
            default:
                System.out.println("Opção de pagamento inválida.");
        }
    }
}
