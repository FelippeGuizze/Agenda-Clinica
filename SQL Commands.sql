CREATE DATABASE agenda_clinica;
USE agenda_clinica;

-- Tabela de Usuários (com autenticação - SENHAS CRIPTOGRAFADAS EM BCRYPT)
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL COMMENT 'Armazenada em BCrypt',
    categoria INT NOT NULL COMMENT '0 = Admin, 1 = Paciente, 2 = Profissional/Médico',
    paciente_id BIGINT UNIQUE NULL,
    profissional_id BIGINT UNIQUE NULL
);

-- Tabela de Pacientes
CREATE TABLE pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    contato VARCHAR(20) NOT NULL
);

-- Tabela de Profissionais
CREATE TABLE profissionais (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    especialidade VARCHAR(100) NOT NULL
);

-- Tabela de Disponibilidade de Profissionais
CREATE TABLE disponibilidade_profissionais (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profissional_id BIGINT NOT NULL COMMENT '-1 = disponibilidade padrão para todos',
    dia_semana VARCHAR(20) NOT NULL COMMENT 'SEGUNDA, TERÇA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO',
    horario VARCHAR(5) NOT NULL COMMENT 'Formato HH:mm',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE KEY unique_prof_dia_hora (profissional_id, dia_semana, horario)
);

-- Tabela de Atendimentos
CREATE TABLE atendimentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    paciente_id BIGINT NOT NULL,
    profissional_id BIGINT NOT NULL,
    datahora DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL COMMENT 'Agendado, Em progresso, Concluído, Cancelado',
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (profissional_id) REFERENCES profissionais(id)
);

-- Tabela de Observações de Atendimento
CREATE TABLE observacoes_atendimento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    atendimento_id BIGINT NOT NULL,
    autor VARCHAR(255) NOT NULL,
    texto LONGTEXT NOT NULL,
    datahora DATETIME NOT NULL,
    FOREIGN KEY (atendimento_id) REFERENCES atendimentos(id)
);

-- ========================================
-- DADOS DE TESTE (REFERÊNCIA)
-- ========================================

-- Inserindo usuário administrador (senha criptografada em BCrypt)
-- Email: admin@agenda.com
-- Senha: admin123 (hash BCrypt)
INSERT INTO usuarios (nome, email, senha, categoria, paciente_id, profissional_id) VALUES
('Administrador', 'admin@agenda.com', '$2a$12$admin123hash', 0, NULL, NULL);

-- Inserindo pacientes
INSERT INTO pacientes (nome, contato) VALUES
('João Silva', '11987654321'),
('Maria Santos', '21987654321');

-- Inserindo profissionais
INSERT INTO profissionais (nome, categoria, especialidade) VALUES
('Dr. Carlos Medico', 'Médico', 'Cardiologia'),
('Dra. Ana Especialista', 'Médica', 'Dermatologia');

-- Inserindo usuários pacientes (senhas criptografadas)
INSERT INTO usuarios (nome, email, senha, categoria, paciente_id, profissional_id) VALUES
('João Silva', 'joao.paciente@email.com', '$2a$12$senha123hash', 1, 1, NULL),
('Maria Santos', 'maria.paciente@email.com', '$2a$12$senha456hash', 1, 2, NULL);

-- Inserindo usuários profissionais (senhas criptografadas)
INSERT INTO usuarios (nome, email, senha, categoria, paciente_id, profissional_id) VALUES
('Dr. Carlos Medico', 'carlos.medico@email.com', '$2a$12$senha789hash', 2, NULL, 1),
('Dra. Ana Especialista', 'ana.especialista@email.com', '$2a$12$senha000hash', 2, NULL, 2);

-- Inserindo disponibilidades padrão (12:00 e 16:00 para todos os dias)
-- profissional_id = -1 significa que é padrão para todos
INSERT INTO disponibilidade_profissionais (profissional_id, dia_semana, horario, ativo) VALUES
(-1, 'SEGUNDA', '12:00', TRUE),
(-1, 'SEGUNDA', '16:00', TRUE),
(-1, 'TERÇA', '12:00', TRUE),
(-1, 'TERÇA', '16:00', TRUE),
(-1, 'QUARTA', '12:00', TRUE),
(-1, 'QUARTA', '16:00', TRUE),
(-1, 'QUINTA', '12:00', TRUE),
(-1, 'QUINTA', '16:00', TRUE),
(-1, 'SEXTA', '12:00', TRUE),
(-1, 'SEXTA', '16:00', TRUE),
(-1, 'SABADO', '12:00', TRUE),
(-1, 'SABADO', '16:00', TRUE),
(-1, 'DOMINGO', '12:00', TRUE),
(-1, 'DOMINGO', '16:00', TRUE);

-- Inserindo atendimentos de teste
INSERT INTO atendimentos (tipo, paciente_id, profissional_id, datahora, status) VALUES
('Consulta', 1, 1, '2026-04-15 12:00:00', 'Agendado'),
('Consulta', 2, 2, '2026-04-16 16:00:00', 'Agendado'),
('Procedimento', 1, 2, '2026-04-20 12:00:00', 'Concluído');

-- Inserindo observações de teste
INSERT INTO observacoes_atendimento (atendimento_id, autor, texto, datahora) VALUES
(1, 'Dr. Carlos Medico', 'Paciente apresenta sintomas de hipertensão. Necessário acompanhamento.', '2026-04-15 12:30:00'),
(3, 'Dra. Ana Especialista', 'Procedimento realizado com sucesso. Sem intercorrências.', '2026-04-20 12:45:00');

