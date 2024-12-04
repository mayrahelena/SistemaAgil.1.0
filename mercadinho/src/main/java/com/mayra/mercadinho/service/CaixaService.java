package com.mayra.mercadinho.service;

import com.mayra.mercadinho.dao.CaixaDAO;
import com.mayra.mercadinho.dao.ProdutoDAO;
import com.mayra.mercadinho.model.Venda;
import com.mayra.mercadinho.model.ItemVenda;
import com.mayra.mercadinho.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaixaService {
    private CaixaDAO caixaDAO;
    private ProdutoDAO produtoDAO;
    private EstoqueService estoqueService;

    public CaixaService(CaixaDAO caixaDAO, ProdutoDAO produtoDAO, EstoqueService estoqueService) {
        this.caixaDAO = caixaDAO;
        this.produtoDAO = produtoDAO;
        this.estoqueService = estoqueService;
    }

    // Método para abrir o caixa com o saldo inicial
    public void abrirCaixa(double saldoInicial) {
        caixaDAO.abrirCaixa(saldoInicial);
        System.out.println("Caixa aberto com saldo inicial de: R$" + saldoInicial);
    }

    // Método para fechar o caixa, calculando o total de entradas/saídas
    public void fecharCaixa() {
        double totalVendas = caixaDAO.getTotalVendas();
        double totalEntradas = caixaDAO.getTotalEntradas();
        double totalSaidas = caixaDAO.getTotalSaidas();

        System.out.println("Relatório do Caixa:");
        System.out.println("Total de Vendas: R$" + totalVendas);
        System.out.println("Total de Entradas: R$" + totalEntradas);
        System.out.println("Total de Saídas: R$" + totalSaidas);

        caixaDAO.fecharCaixa();
        System.out.println("Caixa fechado com sucesso!");
    }

    // Método para registrar uma venda e atualizar o estoque
    public void registrarVenda(Venda venda) {
        venda.registrarVenda();
        System.out.println("Venda registrada com sucesso!");
        estoqueService.atualizarEstoque(venda);
    }

    // Método para registrar uma entrada de valor no caixa (exemplo: pagamento de uma venda)
    public void registrarEntrada(double valor, String metodoPagamento) {
        caixaDAO.registrarEntrada(valor, metodoPagamento);
        System.out.println("Entrada registrada: R$" + valor + " (Método de pagamento: " + metodoPagamento + ")");
    }

    // Método para registrar uma saída de valor do caixa (exemplo: troco ou pagamento de despesas)
    public void registrarSaida(double valor, String motivo) {
        caixaDAO.registrarSaida(valor, motivo);
        System.out.println("Saída registrada: R$" + valor + " (Motivo: " + motivo + ")");
    }

    // Método para registrar uma venda específica com produto, quantidade, pagamento e troco
    public void registrarVenda(int produtoId, int quantidadeVendida, String metodoPagamento, double troco) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (verificarEstoque(produtoId) < quantidadeVendida) {
                System.out.println("Estoque insuficiente para realizar a venda.");
                return;
            }
            // Registrar venda (inserir no banco de dados)
            double totalVenda = calcularTotalVenda(produtoId, quantidadeVendida);
            caixaDAO.registrarEntrada(totalVenda, metodoPagamento);

            // Atualizar estoque
            ItemVenda item = new ItemVenda(produtoDAO.getProdutoById(produtoId), quantidadeVendida);
            Venda venda = new Venda();
            venda.adicionarItem(item);
            registrarVenda(venda);

            System.out.println("Venda registrada com sucesso. Troco: R$" + troco);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para verificar a quantidade disponível no estoque de um produto
    public int verificarEstoque(int produtoId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT quantidade FROM estoque WHERE produto_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
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

    // Método para calcular o total da venda baseado no produto e quantidade vendida
    public double calcularTotalVenda(int produtoId, int quantidadeVendida) throws SQLException {
        double precoUnitario = produtoDAO.getProdutoById(produtoId).getPreco();
        return precoUnitario * quantidadeVendida;
    }
}

