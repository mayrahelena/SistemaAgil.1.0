package com.mayra.mercadinho.controller;

import com.mayra.mercadinho.dao.EstoqueDAO;
import com.mayra.mercadinho.model.Estoque;

import java.sql.SQLException;
import java.util.List;

public class EstoqueController {
    private final EstoqueDAO estoqueDAO = new EstoqueDAO();

    // Lista todo o estoque
    public List<Estoque> listarEstoque() throws SQLException {
        return estoqueDAO.listarEstoque();
    }
}