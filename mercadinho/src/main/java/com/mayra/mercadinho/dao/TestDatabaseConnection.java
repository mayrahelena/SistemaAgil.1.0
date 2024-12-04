package com.mayra.mercadinho.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Tenta estabelecer a conexão
            connection = DatabaseConnection.getConnection();
            System.out.println("Conexão estabelecida com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao estabelecer a conexão: " + e.getMessage());
        } finally {
            // Fecha a conexão
            DatabaseConnection.closeConnection(connection);
        }
    }
}