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
    // Verificar se o produto já existe no banco
    Produto produtoExistente = produtoDAO.localizar(produto.getCodigoBarras());

    if (produtoExistente != null) {
        // Se o produto já existe, usar o ID do produto existente para atualizar o estoque
        System.out.println("Produto já existente: " + produtoExistente.getNome() + " - Código de Barras: " + produtoExistente.getCodigoBarras());
        // Atualizar o estoque para o produto existente
        estoqueDAO.atualizarEstoque(produtoExistente.getId(), quantidade, estoqueMinimo);
        System.out.println("Estoque atualizado com sucesso!");
    } else {
        // Se o produto não existe, adicionar o novo produto e o estoque
        System.out.println("Produto não encontrado. Adicionando novo produto...");
        produtoDAO.adicionar(produto, quantidade, estoqueMinimo);
        System.out.println("Produto e estoque adicionados com sucesso!");
    }
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