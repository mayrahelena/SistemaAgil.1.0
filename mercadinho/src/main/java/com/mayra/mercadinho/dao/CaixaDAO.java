
package com.mayra.mercadinho.dao;

import java.sql.*;

public class CaixaDAO {
    private Connection connection;

    public CaixaDAO(Connection connection) {
        this.connection = connection;
    }

    public void abrirCaixa(double saldoInicial) {
        try {
            String sql = "INSERT INTO caixa (saldo_inicial, saldo_atual) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, saldoInicial);
            stmt.setDouble(2, saldoInicial);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fecharCaixa() {
        try {
            String sql = "UPDATE caixa SET saldo_atual = ? WHERE id = (SELECT MAX(id) FROM caixa)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, getSaldoAtual());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getSaldoAtual() {
        double saldoAtual = 0;
        try {
            String sql = "SELECT saldo_atual FROM caixa ORDER BY id DESC LIMIT 1";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                saldoAtual = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return saldoAtual;
    }

    // Métodos para registrar entradas e saídas
    public void registrarEntrada(double valor, String metodoPagamento) {
        try {
            String sql = "INSERT INTO entradas (valor, metodo_pagamento) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, valor);
            stmt.setString(2, metodoPagamento);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registrarSaida(double valor, String motivo) {
        try {
            String sql = "INSERT INTO saidas (valor, motivo) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, valor);
            stmt.setString(2, motivo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getTotalVendas() {
        double totalVendas = 0;
        try {
            String sql = "SELECT SUM(valor) FROM vendas";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                totalVendas = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalVendas;
    }

    public double getTotalEntradas() {
        double totalEntradas = 0;
        try {
            String sql = "SELECT SUM(valor) FROM entradas";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                totalEntradas = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalEntradas;
    }

    public double getTotalSaidas() {
        double totalSaidas = 0;
        try {
            String sql = "SELECT SUM(valor) FROM saidas";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                totalSaidas = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalSaidas;
    }
}
