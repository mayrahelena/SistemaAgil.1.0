package com.mayra.mercadinho.dao;

import com.mayra.mercadinho.model.Estoque;
import com.mayra.mercadinho.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAO {

    // Método para listar o estoque
    public List<Estoque> listarEstoque() throws SQLException {
        List<Estoque> estoques = new ArrayList<>();
        String sql = "SELECT e.id, p.nome, p.preco, p.codigo_barras, e.quantidade, e.estoque_minimo "
                   + "FROM estoque e "
                   + "JOIN produtos p ON e.produto_id = p.id";
        try (Connection conn = DatabaseConnection.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setCodigoBarras(rs.getString("codigo_barras"));

                Estoque estoque = new Estoque();
                estoque.setId(rs.getInt("id"));
                estoque.setProduto(produto);
                estoque.setQuantidade(rs.getInt("quantidade"));
                estoque.setEstoqueMinimo(rs.getInt("estoque_minimo"));
                estoques.add(estoque);
            }
        }
        return estoques;
    }
    
    // Método para adicionar estoque
    public void adicionarEstoque(int produtoId, int quantidade, int estoqueMinimo) throws SQLException {
        String sql = "INSERT INTO estoque (produto_id, quantidade, estoque_minimo) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);  // Relacionando com o produto
            stmt.setInt(2, quantidade);  // Definindo a quantidade
            stmt.setInt(3, estoqueMinimo);  // Definindo o estoque mínimo
            stmt.executeUpdate();  // Executa a inserção
        }
    }
    
}  