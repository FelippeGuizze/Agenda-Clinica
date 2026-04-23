CREATE DATABASE IF NOT EXISTS agenda_clinica;
USE agenda_clinica;

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    categoria INT NOT NULL,
    paciente_id BIGINT UNIQUE NULL,
    profissional_id BIGINT UNIQUE NULL
);

CREATE TABLE pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    contato VARCHAR(15) NOT NULL
);

CREATE TABLE profissionais (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    especialidade VARCHAR(100) NOT NULL
);

CREATE TABLE atendimentos (
    tipo_classe VARCHAR(31) NOT NULL,
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    paciente_id BIGINT NULL,
    profissional_id BIGINT NOT NULL,
    datahora DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    preco DECIMAL(10,2) NULL,
    valor_taxa DECIMAL(10,2) NULL,
    preco_final DECIMAL(10,2) NULL,
    incluir_taxa_laboratorial TINYINT(1) DEFAULT 0,
    disponibilidade_id BIGINT NULL,
    orientacao_medico TEXT NULL,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (profissional_id) REFERENCES profissionais(id)
);

CREATE TABLE disponibilidade_profissionais (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profissional_id BIGINT NOT NULL,
    horario VARCHAR(10) NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (profissional_id) REFERENCES profissionais(id)
);



