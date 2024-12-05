package com.mayra.mercadinho.dao;

import com.mayra.mercadinho.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ProdutoDAO {

    private Connection connection;

    // Construtor sem parâmetros que chama o método para conectar ao banco
    public ProdutoDAO() {
        conectar();
    }

    // Construtor com conexão já estabelecida
    public ProdutoDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para conectar ao banco de dados
    private void conectar() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mercadinho_db", "root", "021998@Amor");
            if (this.connection != null) {
                System.out.println("Conexão estabelecida com o banco de dados.");
            } else {
                System.out.println("Falha ao estabelecer a conexão com o banco de dados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    // Método para adicionar um produto ao banco, com verificação de produto existente
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

    // Método para buscar produto por ID
    public Produto buscarProdutoPorId(int produtoId) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        String sql = "SELECT * FROM produto WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, produtoId); // Setando o ID do produto
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Retorna o produto encontrado no banco de dados
                    return new Produto(
                        rs.getInt("id"), // ID do produto
                        rs.getString("nome"), // Nome do produto
                        rs.getDouble("preco"), // Preço do produto
                        rs.getString("codigo_barras") // Código de barras do produto
                    );
                }
            }
        }
        return null; // Retorna null se não encontrar o produto
    }

    // Método para calcular o total da venda (preço do produto * quantidade)
    public double calcularTotalVenda(int produtoId, int quantidadeVendida) throws SQLException {
        Produto produto = buscarProdutoPorId(produtoId);
        if (produto == null) {
            throw new SQLException("Produto com ID " + produtoId + " não encontrado.");
        }
        return produto.getPreco() * quantidadeVendida;
    }

    // Método para listar todos os produtos
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

    // Método para excluir produto pelo código de barras ou nome
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

    // Método para editar informações do produto
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

    // Método para localizar produto por código de barras ou nome
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
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantidade");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Método para buscar o preço de um produto por ID
    public double buscarPrecoPorId(int produtoId) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        String sql = "SELECT preco FROM produto WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("preco");
                }
            }
        }
        return 0.0; // Retorna 0.0 se não encontrar o preço
    }

    // Método para obter um produto por ID
    public Produto getProdutoById(int produtoId) throws SQLException {
        return buscarProdutoPorId(produtoId);
    }
}
