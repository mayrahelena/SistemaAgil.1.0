CREATE DATABASE mercadinho_db;
USE mercadinho_db;

CREATE TABLE produtos (
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
preco DECIMAL(10, 2) NOT NULL,
codigo_barras VARCHAR(100)
);

CREATE TABLE estoque (
id INT AUTO_INCREMENT PRIMARY KEY,
produto_id INT,
quantidade INT NOT NULL,
estoque_minimo INT NOT NULL,
FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

INSERT INTO produtos (nome, preco, codigo_barras) VALUES
('Arroz', 20.50, '123456789'),
('Feijão', 8.75, '987654321'),
('Açúcar', 3.40, '192837465');

INSERT INTO estoque (produto_id, quantidade, estoque_minimo) VALUES
(1, 100, 10),  -- Arroz
(2, 50, 5),    -- Feijão
(3, 200, 20);  -- Açúcar

SELECT * FROM produtos;
