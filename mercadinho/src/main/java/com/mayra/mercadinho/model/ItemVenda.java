package com.mayra.mercadinho.model;

public class ItemVenda {
    private Produto produto;
    private int quantidade;
    private double precoUnitario;

    // Construtor principal
    public ItemVenda(Produto produto, int quantidade, double precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // Construtor adicional (caso precise de um objeto Produto por ID ou outra lógica)
    public ItemVenda(Object produtoById, int quantidadeVendida) {
        // Aqui você pode implementar a lógica para inicializar o produto e a quantidade.
        // Por exemplo, se o produtoById for um objeto Produto, você pode inicializar assim:
        if (produtoById instanceof Produto) {
            this.produto = (Produto) produtoById;
            this.quantidade = quantidadeVendida;
            this.precoUnitario = produto.getPreco(); // Supondo que o Produto tenha um método getPreco()
        }
    }

    // Método para calcular o subtotal (quantidade * preço unitário)
    public double calcularSubtotal() {
        return precoUnitario * quantidade;
    }

    // Getters e Setters
    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
