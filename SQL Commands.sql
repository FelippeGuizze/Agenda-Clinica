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
    contato VARCHAR(20) NOT NULL
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
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (profissional_id) REFERENCES profissionais(id)
);

CREATE TABLE observacoes_atendimento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    atendimento_id BIGINT NOT NULL,
    autor VARCHAR(255) NOT NULL,
    texto LONGTEXT NOT NULL,
    datahora DATETIME NOT NULL,
    FOREIGN KEY (atendimento_id) REFERENCES atendimentos(id)
);
