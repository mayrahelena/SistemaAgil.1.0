/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mayra.mercadinho.model;

public class Estoque {
    private int id;
    private Produto produto;
    private int quantidade;
    private int estoqueMinimo;

    // Construtor vazio
    public Estoque() {}

    // Construtor com parâmetros
    public Estoque(int id, Produto produto, int quantidade, int estoqueMinimo) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
        this.estoqueMinimo = estoqueMinimo;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
}