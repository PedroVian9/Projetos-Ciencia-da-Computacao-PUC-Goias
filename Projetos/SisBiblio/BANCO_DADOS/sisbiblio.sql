-- =====================================================================
-- SisBiblio - Sistema de Gestao da Biblioteca
-- Script de criacao do banco de dados (MySQL)
-- Aluno: Pedro Macedo Paronetto Faria Viana - 20221002800297
-- Disciplina: CMP1611 - Mini-Projeto de Software
--
-- Banco:    sisbiblio
-- Usuario:  root
-- Senha:    root
-- (Ajuste em src/util/Conexao.java se necessario)
-- =====================================================================

DROP DATABASE IF EXISTS sisbiblio;
CREATE DATABASE sisbiblio CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sisbiblio;

-- ------------------------------------------------------------
-- Tabela: autor
-- ------------------------------------------------------------
CREATE TABLE autor (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(120) NOT NULL UNIQUE,
    nacionalidade   VARCHAR(60)  NOT NULL
);

-- ------------------------------------------------------------
-- Tabela: categoria
-- ------------------------------------------------------------
CREATE TABLE categoria (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(80) NOT NULL UNIQUE,
    descricao   VARCHAR(255)
);

-- ------------------------------------------------------------
-- Tabela: livro
-- ------------------------------------------------------------
CREATE TABLE livro (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    titulo          VARCHAR(150) NOT NULL,
    isbn            VARCHAR(20)  NOT NULL UNIQUE,
    ano_publicacao  INT          NOT NULL,
    editora         VARCHAR(100) NOT NULL,
    qtd_total       INT          NOT NULL DEFAULT 0,
    qtd_disponivel  INT          NOT NULL DEFAULT 0,
    id_autor        INT          NOT NULL,
    id_categoria    INT          NOT NULL,
    CONSTRAINT fk_livro_autor     FOREIGN KEY (id_autor)     REFERENCES autor(id),
    CONSTRAINT fk_livro_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id)
);

-- ------------------------------------------------------------
-- Tabela: leitor
-- ------------------------------------------------------------
CREATE TABLE leitor (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(120) NOT NULL,
    cpf         VARCHAR(14)  NOT NULL UNIQUE,
    email       VARCHAR(120),
    telefone    VARCHAR(20),
    matricula   VARCHAR(30)  NOT NULL UNIQUE,
    curso       VARCHAR(80)
);

-- ------------------------------------------------------------
-- Tabela: emprestimo
-- ------------------------------------------------------------
CREATE TABLE emprestimo (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    data_emprestimo      DATE        NOT NULL,
    data_prev_devolucao  DATE        NOT NULL,
    data_devolucao       DATE        NULL,
    status               VARCHAR(20) NOT NULL,
    id_leitor            INT         NOT NULL,
    id_livro             INT         NOT NULL,
    CONSTRAINT fk_emp_leitor FOREIGN KEY (id_leitor) REFERENCES leitor(id),
    CONSTRAINT fk_emp_livro  FOREIGN KEY (id_livro)  REFERENCES livro(id)
);

-- ------------------------------------------------------------
-- Tabela: usuario (acesso ao sistema)
-- ------------------------------------------------------------
CREATE TABLE usuario (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nome    VARCHAR(120) NOT NULL,
    login   VARCHAR(50)  NOT NULL UNIQUE,
    senha   VARCHAR(50)  NOT NULL,
    email   VARCHAR(120),
    idioma  VARCHAR(10)  NOT NULL DEFAULT 'pt_BR'
);

-- =====================================================================
-- DADOS DE TESTE
-- =====================================================================

-- Usuario padrao do sistema
INSERT INTO usuario (nome, login, senha, email, idioma) VALUES
('Administrador',                'admin', 'admin', 'admin@sisbiblio.local', 'pt_BR'),
('Pedro Macedo Paronetto Faria Viana', 'pedro', '1234', 'pedro@puc.local',  'pt_BR');

-- Autores
INSERT INTO autor (nome, nacionalidade) VALUES
('Machado de Assis',   'Brasileira'),
('Clarice Lispector',  'Brasileira'),
('George Orwell',      'Britanica'),
('Robert C. Martin',   'Norte-americana'),
('Andrew Tanenbaum',   'Holandesa');

-- Categorias
INSERT INTO categoria (nome, descricao) VALUES
('Romance',          'Obras de ficcao literaria'),
('Ficcao Cientifica','Obras de ficcao cientifica e distopias'),
('Engenharia de Software','Livros tecnicos de desenvolvimento de software'),
('Redes de Computadores', 'Livros tecnicos de redes'),
('Conto',            'Coletaneas de contos');

-- Livros
INSERT INTO livro (titulo, isbn, ano_publicacao, editora, qtd_total, qtd_disponivel, id_autor, id_categoria) VALUES
('Dom Casmurro',          '9788535910663', 1899, 'Companhia das Letras', 5, 5, 1, 1),
('A Hora da Estrela',     '9788532530802', 1977, 'Rocco',                3, 3, 2, 1),
('1984',                  '9788535914849', 1949, 'Companhia das Letras', 4, 4, 3, 2),
('Codigo Limpo',          '9788576082675', 2008, 'Alta Books',           2, 2, 4, 3),
('Redes de Computadores', '9788576059240', 2011, 'Pearson',              2, 2, 5, 4),
('Memorias Postumas',     '9788535914855', 1881, 'Companhia das Letras', 3, 3, 1, 1);

-- Leitores
INSERT INTO leitor (nome, cpf, email, telefone, matricula, curso) VALUES
('Ana Souza',     '111.111.111-11', 'ana@puc.local',   '62 99999-1111', '2022100001', 'Engenharia de Computacao'),
('Bruno Lima',    '222.222.222-22', 'bruno@puc.local', '62 99999-2222', '2022100002', 'Ciencia da Computacao'),
('Carla Mendes',  '333.333.333-33', 'carla@puc.local', '62 99999-3333', '2022100003', 'Sistemas de Informacao'),
('Diego Pereira', '444.444.444-44', 'diego@puc.local', '62 99999-4444', '2022100004', 'Engenharia de Software');

-- Emprestimos
INSERT INTO emprestimo (data_emprestimo, data_prev_devolucao, data_devolucao, status, id_leitor, id_livro) VALUES
('2026-05-10','2026-05-24', NULL,         'ABERTO',     1, 1),
('2026-05-12','2026-05-26', '2026-05-22', 'DEVOLVIDO',  2, 3),
('2026-05-15','2026-05-29', NULL,         'ABERTO',     3, 4),
('2026-05-18','2026-06-01', NULL,         'ABERTO',     4, 5);
