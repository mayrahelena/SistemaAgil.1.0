package com.mayra.mercadinho.dao;

import com.mayra.mercadinho.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProdutoDAO {

    private Connection connection;

    public ProdutoDAO() {
        conectar();
    }

    public ProdutoDAO(Connection connection) {
        this.connection = connection;
    }

    private void conectar() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mercadinho_db", "root", "Senac@2024");

            if (this.connection != null) {
                System.out.println("Conexão estabelecida com o banco de dados.");
            } else {
                System.out.println("Falha ao estabelecer a conexão com o banco de dados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public void adicionar(Produto produto, int quantidade, int estoqueMinimo) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        String sqlVerificacao = "SELECT * FROM produto WHERE nome = ? OR codigo_barras = ?";
        try (PreparedStatement stmtVerificacao = connection.prepareStatement(sqlVerificacao)) {
            stmtVerificacao.setString(1, produto.getNome());
            stmtVerificacao.setString(2, produto.getCodigoBarras());
            ResultSet rs = stmtVerificacao.executeQuery();

            if (rs.next()) {
                int produtoId = rs.getInt("id");
                int quantidadeAtual = rs.getInt("quantidade");
                int estoqueMinimoAtual = rs.getInt("estoque_minimo");

                System.out.println("Produto já existente. Nome: " + produto.getNome() + " - Código de Barras: " + produto.getCodigoBarras());
                System.out.println("Estoque Atual: " + quantidadeAtual + ", Estoque Mínimo Atual: " + estoqueMinimoAtual);
                System.out.println("Deseja atualizar a quantidade e o estoque mínimo? (S/N)");

                Scanner scanner = new Scanner(System.in);
                String resposta = scanner.nextLine().toUpperCase();

                if (resposta.equals("S")) {
                    String sqlAtualizacaoEstoque = "UPDATE estoque SET quantidade = ?, estoque_minimo = ? WHERE produto_id = ?";
                    try (PreparedStatement stmtAtualizacaoEstoque = connection.prepareStatement(sqlAtualizacaoEstoque)) {
                        int novaQuantidade = quantidadeAtual + quantidade;
                        stmtAtualizacaoEstoque.setInt(1, novaQuantidade);
                        stmtAtualizacaoEstoque.setInt(2, estoqueMinimo);
                        stmtAtualizacaoEstoque.setInt(3, produtoId);
                        stmtAtualizacaoEstoque.executeUpdate();
                        System.out.println("Estoque e estoque mínimo atualizados com sucesso!");
                    }
                } else {
                    System.out.println("Por favor, digite um novo código de barras para o produto:");
                    String novoCodigoBarras = scanner.nextLine();
                    produto.setCodigoBarras(novoCodigoBarras);
                    adicionar(produto, quantidade, estoqueMinimo);
                }
            } else {
                String sqlProduto = "INSERT INTO produto (nome, preco, codigo_barras) VALUES (?, ?, ?)";
                try (PreparedStatement stmtProduto = connection.prepareStatement(sqlProduto, Statement.RETURN_GENERATED_KEYS)) {
                    stmtProduto.setString(1, produto.getNome());
                    stmtProduto.setDouble(2, produto.getPreco());
                    stmtProduto.setString(3, produto.getCodigoBarras());
                    stmtProduto.executeUpdate();

                    ResultSet rsProduto = stmtProduto.getGeneratedKeys();
                    if (rsProduto.next()) {
                        int produtoId = rsProduto.getInt(1);

                        String sqlEstoque = "INSERT INTO estoque (produto_id, quantidade, estoque_minimo) VALUES (?, ?, ?)";
                        try (PreparedStatement stmtEstoque = connection.prepareStatement(sqlEstoque)) {
                            stmtEstoque.setInt(1, produtoId);
                            stmtEstoque.setInt(2, quantidade);
                            stmtEstoque.setInt(3, estoqueMinimo);
                            stmtEstoque.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    public Produto getProdutoById(int id) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        String sql = "SELECT * FROM produto WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getString("codigo_barras")
                    );
                }
            }
        }
        return null;
    }

    public double calcularTotalVenda(int produtoId, int quantidadeVendida) throws SQLException {
        Produto produto = getProdutoById(produtoId);
        if (produto == null) {
            throw new SQLException("Produto com ID " + produtoId + " não encontrado.");
        }
        return produto.getPreco() * quantidadeVendida;
    }

    public int adicionarProduto(Produto produto) throws SQLException {
        String sql = "INSERT INTO produto (nome, preco, codigo_barras) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setString(3, produto.getCodigoBarras());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Produto> listar() throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = new Produto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getString("codigo_barras")
                );
                produtos.add(produto);
            }
        }
        return produtos;
    }

    public boolean excluir(String codigoOuNome) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        String sql = "DELETE FROM produto WHERE codigo_barras = ? OR nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codigoOuNome);
            stmt.setString(2, codigoOuNome);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    public boolean editar(Produto produto) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        String sql = "UPDATE produto SET nome = ?, preco = ?, codigo_barras = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setString(3, produto.getCodigoBarras());
            stmt.setInt(4, produto.getId());
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    public Produto localizar(String codigoOuNome) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        String sql = "SELECT * FROM produto WHERE codigo_barras = ? OR nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codigoOuNome);
            stmt.setString(2, codigoOuNome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getString("codigo_barras")
                    );
                }
            }
        }
        return null;
    }

    // Método para obter o estoque de um produto
    public int obterEstoqueProduto(int produtoId) {
        String sql = "SELECT quantidade FROM estoque WHERE produto_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantidade");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retorna 0 se não encontrar o produto
    }

    public void fecharConexao() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão com o banco de dados encerrada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }
}
