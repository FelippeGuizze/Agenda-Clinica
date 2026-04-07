CREATE DATABASE agenda_clinica;
USE agenda_clinica;

-- Tabela de Usuários (com autenticação)
-- Todos os usuários (Admin, Paciente, Profissional) ficam aqui
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    categoria INT NOT NULL COMMENT '0 = Admin, 1 = Paciente, 2 = Profissional/Médico',
    paciente_id BIGINT UNIQUE NULL COMMENT 'Link para tabela pacientes (se categoria = 1)',
    profissional_id BIGINT UNIQUE NULL COMMENT 'Link para tabela profissionais (se categoria = 2)'
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

-- Tabela de Atendimentos (sem links por enquanto)
CREATE TABLE atendimentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    paciente_id BIGINT NOT NULL,
    profissional_id BIGINT NOT NULL,
    datahora DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL COMMENT 'Agendado, Em progresso, Concluído, Cancelado',
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (profissional_id) REFERENCES profisionais(id)
);

-- Tabela de Observações de Atendimento (sem links por enquanto)
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

-- Inserindo usuário administrador
INSERT INTO usuarios (nome, email, senha, categoria, paciente_id, profissional_id) VALUES
('Administrador', 'admin@agenda.com', 'admin123', 0, NULL, NULL);

-- Inserindo pacientes
INSERT INTO pacientes (nome, contato) VALUES
('João Silva', '11987654321'),
('Maria Santos', '21987654321');

-- Inserindo profissionais
INSERT INTO profissionais (nome, categoria, especialidade) VALUES
('Dr. Carlos Medico', 'Médico', 'Cardiologia'),
('Dra. Ana Especialista', 'Médica', 'Dermatologia');

-- Inserindo usuários pacientes com links para pacientes
INSERT INTO usuarios (nome, email, senha, categoria, paciente_id, profissional_id) VALUES
('João Silva', 'joao.paciente@email.com', 'senha123', 1, 1, NULL),
('Maria Santos', 'maria.paciente@email.com', 'senha456', 1, 2, NULL);

-- Inserindo usuários profissionais com links para profissionais
INSERT INTO usuarios (nome, email, senha, categoria, paciente_id, profissional_id) VALUES
('Dr. Carlos Medico', 'carlos.medico@email.com', 'senha789', 2, NULL, 1),
('Dra. Ana Especialista', 'ana.especialista@email.com', 'senha000', 2, NULL, 2);

-- Inserindo atendimentos de teste
INSERT INTO atendimentos (tipo, paciente_id, profissional_id, datahora, status) VALUES
('Consulta', 1, 1, '2026-04-15 10:00:00', 'Agendado'),
('Consulta', 2, 2, '2026-04-16 14:30:00', 'Agendado'),
('Procedimento', 1, 2, '2026-04-20 09:00:00', 'Concluído');

-- Inserindo observações de teste
INSERT INTO observacoes_atendimento (atendimento_id, autor, texto, datahora) VALUES
(1, 'Dr. Carlos Medico', 'Paciente apresenta sintomas de hipertensão. Necessário acompanhamento.', '2026-04-15 10:30:00'),
(3, 'Dra. Ana Especialista', 'Procedimento realizado com sucesso. Sem intercorrências.', '2026-04-20 09:45:00');
