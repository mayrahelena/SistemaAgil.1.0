package com.mayra.mercadinho.dao;

import com.mayra.mercadinho.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProdutoDAO {

    private Connection connection;  // A conexão com o banco de dados será mantida aqui

    // Construtor para conectar ao banco de dados
    public ProdutoDAO() {
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

    // Método para adicionar ou atualizar produto
    public void adicionar(Produto produto, int quantidade, int estoqueMinimo) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        // Verificar se o produto já existe no banco de dados, seja por nome ou código de barras
        String sqlVerificacao = "SELECT * FROM produto WHERE nome = ? OR codigo_barras = ?";
        try (PreparedStatement stmtVerificacao = connection.prepareStatement(sqlVerificacao)) {
            stmtVerificacao.setString(1, produto.getNome());  // Passa o nome do produto
            stmtVerificacao.setString(2, produto.getCodigoBarras());  // Passa o código de barras
            ResultSet rs = stmtVerificacao.executeQuery();  // Executa a consulta no banco de dados

            if (rs.next()) {
                // Produto já existe no banco, verificamos a quantidade atual no estoque
                int produtoId = rs.getInt("id");

                // Consultar o estoque atual
                String sqlEstoqueVerificacao = "SELECT * FROM estoque WHERE produto_id = ?";
                try (PreparedStatement stmtEstoque = connection.prepareStatement(sqlEstoqueVerificacao)) {
                    stmtEstoque.setInt(1, produtoId);
                    ResultSet rsEstoque = stmtEstoque.executeQuery();
                    if (rsEstoque.next()) {
                        int quantidadeAtual = rsEstoque.getInt("quantidade");
                        int estoqueMinimoAtual = rsEstoque.getInt("estoque_minimo");

                        // Exibe informações do produto já existente
                        System.out.println("Produto já existente. Nome: " + produto.getNome() + " - Código de Barras: " + produto.getCodigoBarras());
                        System.out.println("Estoque Atual: " + quantidadeAtual + ", Estoque Mínimo Atual: " + estoqueMinimoAtual);
                        System.out.println("Deseja atualizar a quantidade e o estoque mínimo? (S/N)");

                        Scanner scanner = new Scanner(System.in);  // Leitura da entrada do usuário
                        String resposta = scanner.nextLine().toUpperCase();

                        if (resposta.equals("S")) {
                            // Atualiza a quantidade e o estoque mínimo no banco de dados
                            String sqlAtualizacaoEstoque = "UPDATE estoque SET quantidade = ?, estoque_minimo = ? WHERE produto_id = ?";
                            try (PreparedStatement stmtAtualizacaoEstoque = connection.prepareStatement(sqlAtualizacaoEstoque)) {
                                int novaQuantidade = quantidadeAtual + quantidade;  // Soma a quantidade atual com a nova
                                stmtAtualizacaoEstoque.setInt(1, novaQuantidade);  // Atualiza a quantidade
                                stmtAtualizacaoEstoque.setInt(2, estoqueMinimo);  // Atualiza o estoque mínimo
                                stmtAtualizacaoEstoque.setInt(3, produtoId);  // Relaciona com o ID do produto
                                stmtAtualizacaoEstoque.executeUpdate();
                                System.out.println("Estoque e estoque mínimo atualizados com sucesso!");
                            }
                        }
                    }
                }
            } else {
                // Caso o produto não exista, insere um novo produto no banco
                String sqlProduto = "INSERT INTO produto (nome, preco, codigo_barras) VALUES (?, ?, ?)";
                try (PreparedStatement stmtProduto = connection.prepareStatement(sqlProduto, Statement.RETURN_GENERATED_KEYS)) {
                    stmtProduto.setString(1, produto.getNome());
                    stmtProduto.setDouble(2, produto.getPreco());
                    stmtProduto.setString(3, produto.getCodigoBarras());
                    stmtProduto.executeUpdate();

                    // Recupera o ID do produto recém-inserido
                    ResultSet rsProduto = stmtProduto.getGeneratedKeys();
                    if (rsProduto.next()) {
                        int produtoId = rsProduto.getInt(1);  // ID do produto recém-inserido

                        // Agora insere o estoque do produto na tabela 'estoque'
                        String sqlEstoque = "INSERT INTO estoque (produto_id, quantidade, estoque_minimo) VALUES (?, ?, ?)";
                        try (PreparedStatement stmtEstoque = connection.prepareStatement(sqlEstoque)) {
                            stmtEstoque.setInt(1, produtoId);  // Relaciona com o ID do produto
                            stmtEstoque.setInt(2, quantidade);  // Define a quantidade inicial
                            stmtEstoque.setInt(3, estoqueMinimo);  // Define o estoque mínimo
                            stmtEstoque.executeUpdate();
                        }
                    }
                }
            }
        }
    }


    
    // Método para listar todos os produtos
    public List<Produto> listar() throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto";  // Consulta SQL para listar todos os produtos
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = new Produto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getString("codigo_barras")
                );
                produtos.add(produto);  // Adiciona o produto à lista
            }
        }
        return produtos;  // Retorna a lista de produtos
    }

    // Método para excluir um produto pelo código de barras ou nome
    public boolean excluir(String codigoOuNome) throws SQLException {
        if (connection == null) {
            throw new SQLException("Conexão não estabelecida com o banco.");
        }

        String sql = "DELETE FROM produto WHERE codigo_barras = ? OR nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codigoOuNome);
            stmt.setString(2, codigoOuNome);
            int linhasAfetadas = stmt.executeUpdate();  // Executa o comando de exclusão
            return linhasAfetadas > 0;  // Retorna true se alguma linha foi excluída
        }
    }

    // Método para editar um produto
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
            int linhasAfetadas = stmt.executeUpdate();  // Executa a atualização
            return linhasAfetadas > 0;  // Retorna true se a edição foi bem-sucedida
        }
    }

    // Método para localizar um produto por código de barras ou nome
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
        return null;  // Retorna null se não encontrar o produto
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

    public int adicionarProduto(Produto produto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}