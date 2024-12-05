package com.mayra.mercadinho.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mercadinho_db"; // URL do banco de dados
    private static final String USER = "root"; // Usuário do MySQL
    private static final String PASSWORD = "021998@Amor"; // Senha do MySQL

    // Método para abrir a conexão com o banco de dados
    public static Connection getConnection() throws SQLException {
        try {
            // Carrega o driver do MySQL (essa parte não é mais necessária para as versões mais novas do MySQL)
            // Class.forName("com.mysql.cj.jdbc.Driver");
            // Retorna a conexão
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Erro ao conectar ao banco de dados", e);
        }
    }

    // Método para fechar a conexão com o banco de dados
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }

    // Método gerado automaticamente e necessário (conforme pedido)
    // Aqui ele lança uma exceção indicando que não está implementado
    static Connection getConexao() {
        throw new UnsupportedOperationException("Not supported yet."); // Gerado automaticamente
    }
}
