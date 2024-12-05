package com.mayra.mercadinho.dao;

import com.mayra.mercadinho.model.Pagamento;
import java.sql.*;
import java.util.List;

public class CaixaDAO {
    private Connection connection;

    // Construtor que inicializa a conexão com o banco de dados
    public CaixaDAO() {
        this.connection = DatabaseConnection.getConexao(); // Método da classe Conexao para pegar a conexão
    }

    // Construtor da classe, agora corretamente implementado
    public CaixaDAO(Connection connection) {
        this.connection = connection; // Atribui a conexão recebida ao atributo da classe
    }

    // Método para abrir o caixa com o saldo inicial
    public void abrirCaixa(double saldoInicial, int operadorId) {
        try {
            String sql = "INSERT INTO caixa (saldo_inicial, saldo_final, status, operador_id) VALUES (?, ?, 'Aberto', ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, saldoInicial);
            stmt.setDouble(2, saldoInicial);
            stmt.setInt(3, operadorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para fechar o caixa
    public void fecharCaixa() {
        // Implementação mínima, você pode fazer um fechamento mais elaborado com base na sua lógica de negócios
        try {
            String sql = "UPDATE caixa SET status = 'Fechado' WHERE status = 'Aberto' ORDER BY id DESC LIMIT 1";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obter o total de vendas

    /**
     *
     * @return
     */
    public double getTotalVendas() {
        double totalVendas = 0;
        try {
            String sql = "SELECT SUM(total) FROM vendas";
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

    // Para obter o total de entradas
public double getTotalEntradas() {
    double totalEntradas = 0;
    try {
        String sql = "SELECT SUM(valor) FROM entrada_saida WHERE tipo = 'Entrada'";
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

// Para obter o total de saídas
public double getTotalSaidas() {
    double totalSaidas = 0;
    try {
        String sql = "SELECT SUM(valor) FROM entrada_saida WHERE tipo = 'Saída'";
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


 // Método para registrar uma entrada
public void registrarEntrada(double valor, String metodoPagamento) {
    try {
        String sql = "INSERT INTO entrada_saida (caixa_id, valor, tipo, metodo_pagamento) VALUES (?, ?, 'Entrada', ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getCaixaIdAtual());  // Substitua obterCaixaId() com o método para obter o ID do caixa
        stmt.setDouble(2, valor);
        stmt.setString(3, metodoPagamento);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Método para registrar uma saída
public void registrarSaida(double valor, String metodoPagamento) {
    try {
        String sql = "INSERT INTO entrada_saida (caixa_id, valor, tipo, metodo_pagamento) VALUES (?, ?, 'Saída', ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, getCaixaIdAtual());  // Substitua obterCaixaId() com o método para obter o ID do caixa
        stmt.setDouble(2, valor);
        stmt.setString(3, metodoPagamento);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public int getCaixaIdAtual() {
    int caixaId = 0;
    try {
        String sql = "SELECT id FROM caixa WHERE status = 'Aberto' ORDER BY id DESC LIMIT 1";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            caixaId = rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return caixaId;
}


 

    // Método para registrar uma venda
    public void registrarVenda(double totalVenda, List<Pagamento> pagamentos) {
        String sqlVenda = "INSERT INTO vendas (caixa_id, total, data) VALUES (?, ?, NOW())";
        String sqlEntradaSaida = "INSERT INTO entrada_saida (caixa_id, valor, tipo, metodo_pagamento) VALUES (?, ?, ?, ?)";

        try {
            // Inicia a transação
            connection.setAutoCommit(false);

            // Registra a venda
            PreparedStatement stmtVenda = connection.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            stmtVenda.setInt(1, getCaixaIdAtual());  // Obtém o ID do caixa atual
            stmtVenda.setDouble(2, totalVenda);
            stmtVenda.executeUpdate();

            // Obtém o ID da venda registrada
            ResultSet rs = stmtVenda.getGeneratedKeys();
            int vendaId = 0;
            if (rs.next()) {
                vendaId = rs.getInt(1);
            }

            // Registra os pagamentos na tabela entrada_saida
            for (Pagamento pagamento : pagamentos) {
                PreparedStatement stmtPagamento = connection.prepareStatement(sqlEntradaSaida);
                stmtPagamento.setInt(1, getCaixaIdAtual());  // Obtém o ID do caixa atual
                stmtPagamento.setDouble(2, pagamento.getValor());
                stmtPagamento.setString(3, pagamento.getTipo());
                stmtPagamento.setString(4, pagamento.getMetodoPagamento());
                stmtPagamento.executeUpdate();
            }

            // Comita a transação
            connection.commit();
            System.out.println("Venda registrada com sucesso!");

        } catch (SQLException e) {
            try {
                // Em caso de erro, desfaz a transação
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);  // Restaura o modo de commit automático
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


 