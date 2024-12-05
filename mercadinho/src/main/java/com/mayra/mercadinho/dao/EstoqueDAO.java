package com.mayra.mercadinho.dao;

import com.mayra.mercadinho.model.Estoque;
import com.mayra.mercadinho.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAO {

    private Connection connection;  // A conexão com o banco de dados será mantida aqui

    // Construtor para conectar ao banco de dados
    public EstoqueDAO() {
        conectar();  // Ao instanciar a classe, chamamos o método para estabelecer a conexão com o banco
    }

    // Método para conectar ao banco de dados
    private void conectar() {
        try {
            // Usando a classe DatabaseConnection para obter a conexão
            this.connection = DatabaseConnection.getConnection();
            System.out.println("Conexão estabelecida com o banco de dados.");
        } catch (SQLException e) {
            // Se a conexão falhar, captura a exceção e imprime a mensagem
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

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
    
   public void atualizarEstoque(int produtoId, int quantidade, int estoqueMinimo) throws SQLException {
    if (connection == null) {
        throw new SQLException("Conexão não estabelecida com o banco.");
    }

    // Atualizar o estoque do produto
    String sqlAtualizarEstoque = "UPDATE estoque SET quantidade = ?, estoque_minimo = ? WHERE produto_id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sqlAtualizarEstoque)) {
        stmt.setInt(1, quantidade);  // Define a nova quantidade
        stmt.setInt(2, estoqueMinimo);  // Define o novo estoque mínimo
        stmt.setInt(3, produtoId);  // Relaciona com o ID do produto
        stmt.executeUpdate();  // Executa a atualização
    }
}
    
}  