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
            // Carrega o driver do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Retorna a conexão
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Erro ao carregar o driver MySQL", e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ou trate o erro conforme necessário
                
            }
        }
    }
}