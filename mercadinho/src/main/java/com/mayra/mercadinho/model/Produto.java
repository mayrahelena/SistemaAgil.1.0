package com.mayra.mercadinho.model;

public class Produto {
    private int id;
    private String nome;
    private double preco;
    private String codigoBarras;

    // Construtor vazio
    public Produto() {}

    // Construtor com parâmetros
    public Produto(int id, String nome, double preco, String codigoBarras) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.codigoBarras = codigoBarras;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {  // Método para obter o nome do produto
        return nome;
    }

    public void setNome(String nome) {  // Método para definir o nome do produto
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getCodigoBarras() {  // Método para obter o código de barras do produto
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {  // Método para definir o código de barras
        this.codigoBarras = codigoBarras;
    }
}