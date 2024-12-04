package com.mayra.mercadinho.controller;

import com.mayra.mercadinho.dao.ProdutoDAO;
import com.mayra.mercadinho.model.Produto;
import com.mayra.mercadinho.dao.EstoqueDAO;

import java.sql.SQLException;
import java.util.List;

public class ProdutoController {
    private ProdutoDAO produtoDAO;  // A variável de instância que representa a camada de acesso ao banco (DAO)
    private EstoqueDAO estoqueDAO = new EstoqueDAO();  // Adicionando a dependência de EstoqueDAO
     
    // Construtor que inicializa o ProdutoDAO
    public ProdutoController() {
        this.produtoDAO = new ProdutoDAO();  // Criamos o objeto ProdutoDAO para interagir com o banco de dados
        this.estoqueDAO = new EstoqueDAO();  // Criamos o objeto ProdutoDAO para interagir com o banco de dados
    }

 
    public void adicionarProduto(Produto produto, int quantidade, int estoqueMinimo) throws SQLException {
    // Adicionar o produto
    int produtoId = produtoDAO.adicionarProduto(produto);

    // Agora insira os dados no estoque
    estoqueDAO.adicionarEstoque(produtoId, quantidade, estoqueMinimo);
}
    
   // Método para listar todos os produtos
    public List<Produto> listarProdutos() throws SQLException {
        return produtoDAO.listar();  // Chamamos o método 'listar' do ProdutoDAO para pegar os produtos
    }

    // Método para excluir um produto pelo código ou nome
    public boolean excluirProduto(String codigoOuNome) throws SQLException {
        return produtoDAO.excluir(codigoOuNome);  // Delegamos a chamada para o método 'excluir' do ProdutoDAO
    }

    // Método para editar um produto
    public boolean editarProduto(Produto produto) throws SQLException {
        return produtoDAO.editar(produto);  // Delegamos a chamada para o método 'editar' do ProdutoDAO
    }

    // Método para localizar um produto pelo código de barras ou nome
    public Produto localizarProduto(String codigoOuNome) throws SQLException {
        return produtoDAO.localizar(codigoOuNome);  // Delegamos a chamada para o método 'localizar' do ProdutoDAO
    }
    
}