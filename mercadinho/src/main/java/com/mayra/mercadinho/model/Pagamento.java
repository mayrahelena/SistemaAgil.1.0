
package com.mayra.mercadinho.model;

public class Pagamento {
    private double valor;
    private String tipo;  // Entrada ou Saída
    private String metodoPagamento; // Dinheiro, Cartão, PIX

    public Pagamento(double valor, String tipo, String metodoPagamento) {
        this.valor = valor;
        this.tipo = tipo;
        this.metodoPagamento = metodoPagamento;
    }

    public double getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }
}