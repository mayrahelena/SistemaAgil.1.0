package com.mayra.mercadinho.service;

import com.mayra.mercadinho.model.ItemVenda;
import com.mayra.mercadinho.model.Venda;
import com.mayra.mercadinho.dao.DatabaseConnection;
import com.mayra.mercadinho.dao.ProdutoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EstoqueService {

    private ProdutoDAO produtoDAO;

    // Construtor que recebe um ProdutoDAO para realizar operações relacionadas ao estoque
    public EstoqueService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    // Método que verifica o estoque de um produto
    public int verificarEstoque(int produtoId) {
        return produtoDAO.obterEstoqueProduto(produtoId);  // Supondo que ProdutoDAO tenha esse método
    }

    // Método que atualiza o estoque após uma venda
    public void atualizarEstoque(Venda venda) {
        // Usa a DatabaseConnection para obter a conexão
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Atualiza o estoque de cada item da venda
            for (ItemVenda item : venda.getItens()) {
                atualizarEstoqueProduto(item, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar o estoque de um único produto
    private void atualizarEstoqueProduto(ItemVenda item, Connection connection) {
        // Atualiza o estoque decrementando a quantidade vendida
        String sqlEstoque = "UPDATE estoque SET quantidade = quantidade - ? WHERE produto_id = ?";
        try (PreparedStatement stmtEstoque = connection.prepareStatement(sqlEstoque)) {
            stmtEstoque.setInt(1, item.getQuantidade()); // Decrementa a quantidade vendida
            stmtEstoque.setInt(2, item.getProduto().getId()); // ID do produto
            stmtEstoque.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
