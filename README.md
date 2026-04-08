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
- **Agendamento Dinâmico**: O médico possui autonomia para disponibilizar horários específicos utilizando o calendário nativo do navegador (`datetime-local`).
- **Seeding Service**: Ferramenta automatizada para popular o banco de dados com usuários iniciais ao detectar um banco vazio.

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
