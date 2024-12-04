package com.mercadinho;

import com.mayra.mercadinho.controller.ProdutoController;
import com.mayra.mercadinho.model.Produto;
import java.sql.SQLException;
import java.util.Scanner;

public class Mercadinho {
    public static void main(String[] args) {
        ProdutoController produtoController = new ProdutoController();
        Scanner scanner = new Scanner(System.in);
        
        // Variáveis globais, que são usadas em mais de um caso do switch
        Produto novoProduto = null;
        int quantidade = 0;
        int estoqueMinimo = 0;

        while (true) {
            System.out.println("Escolha uma opção:");
            System.out.println("1 - Adicionar um novo produto");
            System.out.println("2 - Listar produtos");
            System.out.println("3 - Excluir produto");
            System.out.println("4 - Editar produto");
            System.out.println("5 - Localizar produto");
            System.out.println("6 - Sair");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha

            switch (opcao) {
                case 1 -> {
                    // Variáveis locais que são usadas apenas dentro deste caso
                    System.out.println("Digite o nome do produto:");
                    String nome = scanner.nextLine();
                    System.out.println("Digite o preço do produto:");
                    double preco = 0;
                    while (true) {
                        try {
                            preco = Double.parseDouble(scanner.nextLine().replace(",", ".")); // Garantir que o preço seja numérico
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Por favor, insira um valor numérico válido para o preço.");
                        }
                    }
                    System.out.println("Digite o código de barras do produto:");
                    String codigoBarras = scanner.nextLine();

                    System.out.println("Digite a quantidade inicial do produto:");
                    quantidade = scanner.nextInt();
                    System.out.println("Digite o estoque mínimo do produto:");
                    estoqueMinimo = scanner.nextInt();
                    scanner.nextLine(); // Consumir a quebra de linha

                    novoProduto = new Produto(0, nome, preco, codigoBarras); // Instanciando o produto
                    try {
                        produtoController.adicionarProduto(novoProduto, quantidade, estoqueMinimo);
                        System.out.println("Produto adicionado com sucesso!");
                    } catch (SQLException e) {
                        System.err.println("Erro ao adicionar produto: " + e.getMessage());
                    }
                }

                case 2 -> {
                    // Variáveis locais para o caso de listar produtos
                    try {
                        produtoController.listarProdutos().forEach(p ->
                                System.out.println(p.getNome() + " - " + p.getPreco()));
                    } catch (SQLException e) {
                        System.err.println("Erro ao listar produtos: " + e.getMessage());
                    }
                }

                case 3 -> {
                    // Variáveis locais para o caso de excluir produto
                    System.out.println("Digite o nome ou código de barras do produto que deseja excluir:");
                    String excluirProduto = scanner.nextLine();
                    try {
                        if (produtoController.excluirProduto(excluirProduto)) {
                            System.out.println("Produto excluído com sucesso!");
                        } else {
                            System.out.println("Produto não encontrado.");
                        }
                    } catch (SQLException e) {
                        System.err.println("Erro ao excluir produto: " + e.getMessage());
                    }
                }

                case 4 -> {
                    // Variáveis locais para o caso de editar produto
                    System.out.println("Digite o nome ou código de barras do produto que deseja editar:");
                    String editarProduto = scanner.nextLine();
                    Produto produtoEncontrado;
                    try {
                        produtoEncontrado = produtoController.localizarProduto(editarProduto);
                        if (produtoEncontrado != null) {
                            System.out.println("Produto encontrado: " + produtoEncontrado.getNome());
                            System.out.println("Digite o novo nome do produto:");
                            String novoNome = scanner.nextLine();
                            System.out.println("Digite o novo preço do produto:");
                            double novoPreco = 0;
                            while (true) {
                                try {
                                    novoPreco = Double.parseDouble(scanner.nextLine().replace(",", ".")); // Garantir que o preço seja numérico
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.println("Por favor, insira um valor numérico válido para o preço.");
                                }
                            }
                            System.out.println("Digite o novo código de barras do produto:");
                            String novoCodigoBarras = scanner.nextLine();

                            Produto produtoEditado = new Produto(produtoEncontrado.getId(), novoNome, novoPreco, novoCodigoBarras);
                            try {
                                if (produtoController.editarProduto(produtoEditado)) {
                                    System.out.println("Produto editado com sucesso!");
                                } else {
                                    System.out.println("Erro ao editar produto.");
                                }
                            } catch (SQLException e) {
                                System.err.println("Erro ao editar produto: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Produto não encontrado.");
                        }
                    } catch (SQLException e) {
                        System.err.println("Erro ao localizar produto: " + e.getMessage());
                    }
                }

                case 5 -> {
                    // Variáveis locais para o caso de localizar produto
                    System.out.println("Digite o nome ou código de barras do produto que deseja localizar:");
                    String localizarProduto = scanner.nextLine();
                    try {
                        Produto produtoLocalizado = produtoController.localizarProduto(localizarProduto);
                        if (produtoLocalizado != null) {
                            System.out.println("Produto encontrado: " + produtoLocalizado.getNome() + " - " + produtoLocalizado.getPreco());
                        } else {
                            System.out.println("Produto não encontrado.");
                        }
                    } catch (SQLException e) {
                        System.err.println("Erro ao localizar produto: " + e.getMessage());
                    }
                }

                case 6 -> {
                    // Sair do loop
                    System.out.println("Saindo...");
                    scanner.close();
                    return; // Encerra o loop e sai do método main
                }
                
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}