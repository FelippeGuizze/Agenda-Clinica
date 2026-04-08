# Agenda Clínica

Uma Agenda Clínica desenvolvida para o projeto da faculdade do professor Genivaldo Carlos Da Silva.

## Sumário
- [Visão Geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Primeiros Passos](#primeiros-passos)
- [Notas Importantes](#notas-importantes)

## Visão Geral
Este projeto envolve várias áreas do conhecimento de Java Web Application, desenvolvido para uso acadêmico, servindo como uma plataforma para gerenciamento de uma agenda clínica. No backend foram utilizados vários conceitos de Programação Orientada a Objetos (POO), baseando-se principalmente nos pilares de polimorfismo e herança para a gestão de atendimentos.

## Funcionalidades
- **Autenticação por Usuário**: O painel possui diferentes níveis de acesso e fluxos de login para pacientes, médicos e administradores.
- **Segurança com BCrypt**: As senhas são protegidas no banco de dados através de algoritmos de hash criptográfico.
- **Sistema de Agendamento:** Sistema avançado de horários usando o seletor nativo do navegador `[datetime-local]`, integrado diretamente no painel do médico.
- **Relatórios Financeiros Dinâmicos (Admin):** Gráficos de barras via AJAX usando `Chart.js` (CDN), exibindo o faturamento bruto x líquido filtrados por ano, com base nos dados reais do banco.
- **Cancelamento Inteligente:** Pacientes podem cancelar consultas sem comprometer a integridade dos dados; o horário é automaticamente re-disponibilizado para outros pacientes.
- **Utilitário de Backup:** Motor de serialização completo do banco de dados em formato `JSON`, gerado via administrador e recuperável através do mesmo painel.

## Arquitetura
- **Backend**: Java 21, Jakarta EE Servlets.
- **ORM Framework**: Hibernate 6 / JPA (MySQL dialect).
- **Frontend**: HTML5, CSS3, JSP (Jakarta Standard Tag Library).
- **Design Patterns**: MVC (Model-View-Controller), Factory Method (para instanciamento polimórfico).

## Primeiros Passos

### Pré-requisitos
- JDK 21
- Apache Tomcat 10+
- Maven 3.8+
- MySQL Server 8.0+

### Instalação
1. Certifique-se de ter o MySQL instalado em sua máquina.
2. Inicialize o seu schema conforme o arquivo `SQL Commands.sql` ou execute no MySQL: `CREATE DATABASE agenda_clinica;`.
3. Atualize o arquivo `src/main/resources/hibernate.cfg.xml` com as suas credenciais locais do MySQL.
4. Execute o comando `mvn clean compile war:war`.
5. Realize o deploy do arquivo WAR gerado no Tomcat 10.
6. Ao iniciar, o sistema criará automaticamente as tabelas e os dados de teste iniciais.

## Notas Importantes
- O sistema gera automaticamente dados padrões (como o administrador e um médico de teste). Caso queira desabilitar ou alterar esse comportamento, modifique a classe `SeedService.java`.
