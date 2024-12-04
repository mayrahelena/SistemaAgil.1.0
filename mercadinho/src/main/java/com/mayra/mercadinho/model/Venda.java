
package com.mayra.mercadinho.model;

import com.mayra.mercadinho.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Venda {
    private int id;
    private List<ItemVenda> itens;
    private double total;

    public Venda() {
        // Inicializa a lista de itens
        this.itens = new ArrayList<>();
        this.total = 0.0;
    }

    public void adicionarItem(ItemVenda item) {
        itens.add(item);
    }

    public void calcularTotal() {
        // Calcula o total da venda somando os subtotais de todos os itens
        total = 0;
        for (ItemVenda item : itens) {
            total += item.calcularSubtotal();
        }
    }

    public void registrarVenda() {
        // Usa a DatabaseConnection para obter a conexão
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Registra a venda na tabela de vendas
            String sqlVenda = "INSERT INTO vendas (total) VALUES (?)";
            try (PreparedStatement stmtVenda = connection.prepareStatement(sqlVenda, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmtVenda.setDouble(1, total);
                stmtVenda.executeUpdate();

                // Obtém o ID da venda gerado automaticamente
                try (ResultSet rs = stmtVenda.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }

                // Registra os itens da venda na tabela de itens_venda
                String sqlItemVenda = "INSERT INTO itens_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmtItemVenda = connection.prepareStatement(sqlItemVenda)) {
                    for (ItemVenda item : itens) {
                        stmtItemVenda.setInt(1, id); // Associa a venda aos itens
                        stmtItemVenda.setInt(2, item.getProduto().getId()); // ID do produto
                        stmtItemVenda.setInt(3, item.getQuantidade()); // Quantidade vendida
                        stmtItemVenda.setDouble(4, item.getPrecoUnitario()); // Preço unitário do produto
                        stmtItemVenda.executeUpdate();
                        // Atualiza o estoque após a venda
                        atualizarEstoque(item, connection);
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void atualizarEstoque(ItemVenda item, Connection connection) {
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

    public double getTotal() {
        return total;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    // Método que retorna o ID do produto, com base no primeiro item da venda
    public int getProdutoId() {
        if (!itens.isEmpty()) {
            return itens.get(0).getProduto().getId(); // Retorna o ID do produto do primeiro item
        }
        return -1; // Caso não tenha itens, retorna um valor inválido
    }

    // Método que retorna a quantidade do primeiro item da venda
    public int getQuantidade() {
        if (!itens.isEmpty()) {
            return itens.get(0).getQuantidade(); // Retorna a quantidade do primeiro item
        }
        return 0; // Caso não tenha itens, retorna 0
    }
}

