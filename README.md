# 🏥 Agenda Clínica

Uma Agenda Clínica desenvolvida para o projeto da faculdade do professor Genivaldo Carlos Da Silva.

## 📋 Sumário
- [Visão Geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Primeiros Passos](#primeiros-passos)
- [Notas Importantes](#notas-importantes)

## ✅ Visão Geral
Este projeto envolve várias áreas do conhecimento de Java Web Application, desenvolvido para uso acadêmico, servindo como uma plataforma para gerenciamento de uma agenda clínica. No backend foram utilizados vários conceitos de Programação Orientada a Objetos (POO), baseando-se principalmente nos pilares de polimorfismo e herança para a gestão de atendimentos.

## 🚀 Funcionalidades
- **Autenticação por Usuário**: O painel possui diferentes níveis de acesso e fluxos de login para pacientes, médicos e administradores.
- **Segurança com BCrypt**: As senhas são protegidas no banco de dados através de algoritmos de hash criptográfico.
- **Sistema de Agendamento:** Sistema avançado de horários usando o seletor nativo do navegador `[datetime-local]`, integrado diretamente no painel do médico.
- **Relatórios Financeiros Dinâmicos (Admin):** Gráficos de barras via AJAX usando `Chart.js` (CDN).
- **Cancelamento Inteligente:** Pacientes podem cancelar consultas sem comprometer a integridade dos dados; o horário é automaticamente re-disponibilizado para outros pacientes.
- **Utilitário de Backup:** Motor de serialização completo do banco de dados em formato `JSON`, gerado via administrador e recuperável através do mesmo painel.

## 🏗️ Arquitetura
- **Backend**: Java 21, Jakarta EE Servlets.
- **ORM Framework**: Hibernate 6 / JPA (MySQL dialect).
- **Frontend**: HTML5, CSS3, JSP (Jakarta Standard Tag Library).
- **Design Patterns**: MVC (Model-View-Controller), Factory Method (para instanciamento polimórfico).

## 👣 Primeiros Passos

### 📌 Pré-requisitos
- JDK 21
- Apache Tomcat 10.1+
- Maven 3.8+
- MySQL Server 8.0+

### 🗄️ Banco de Dados
1. Certifique-se de ter o MySQL instalado e em execução.
2. Execute o arquivo `SQL Commands.sql` no seu cliente MySQL para criar o banco e as tabelas, ou rode manualmente: `CREATE DATABASE agenda_clinica;`.
3. Atualize o arquivo `src/main/resources/hibernate.cfg.xml` com suas credenciais locais do MySQL (usuário e senha).

---

### 🌑 Instalação no Eclipse

#### 1. Instalar o plugin do Tomcat (Eclipse Web Tools Platform)
O Eclipse IDE for Java Developers **não** inclui suporte a servidores web por padrão. É necessário instalar o **Eclipse Web Tools Platform (WTP)**:
- Vá em **Help → Eclipse Marketplace**.
- Pesquise por `Eclipse Enterprise Java and Web Developer Tools` (ou `WTP`) e instale.
- Reinicie o Eclipse quando solicitado.

#### 2. Configurar o Tomcat no Eclipse
- Vá em **Window → Preferences → Server → Runtime Environments**.
- Clique em **Add**, selecione **Apache Tomcat v10.1** e aponte para o diretório de instalação do Tomcat na sua máquina.
- Clique em **Finish**.

#### 3. Habilitar o projeto como Dynamic Web Project (faceta Maven)
Por ser um projeto Maven com packaging `war`, é necessário ativar a faceta de projeto web:
- Clique com o botão direito no projeto → **Properties**.
- Vá em **Project Facets**.
- Marque **Dynamic Web Module** (versão 6.0) e **Java** (versão 21).
- Clique em **Apply and Close**. Isso permite que o Eclipse reconheça o projeto como uma aplicação web e o disponibilize para deploy no Tomcat.

#### 4. Adicionar o projeto ao servidor Tomcat
- Na aba **Servers** (parte inferior do Eclipse), clique com o botão direito no servidor Tomcat que você criou → **Add and Remove...**.
- Mova o projeto `agenda-clinica` para o lado **Configured** e clique em **Finish**.

#### 5. Configurar o caminho de deploy (Context Path)
- Na aba **Servers**, dê duplo clique no servidor Tomcat para abrir as configurações.
- Vá na aba **Modules** e selecione o módulo `agenda-clinica`.
- Clique em **Edit** e defina o **Path** como `/` (barra) para acessar a aplicação diretamente em `http://localhost:8080/`.
- Salve e feche.

#### 6. Executar
- Clique com o botão direito no servidor → **Start**.
- Acesse `http://localhost:8080/` no navegador.

---

### ☀️ Instalação no NetBeans

#### 1. Baixar e instalar o Apache Tomcat
- Baixe o Tomcat 10.1 em [tomcat.apache.org](https://tomcat.apache.org/) e extraia em uma pasta de sua preferência.

#### 2. Registrar o Tomcat no NetBeans
- Vá em **Tools → Servers**.
- Clique em **Add Server**, selecione **Apache Tomcat or TomEE** e clique em **Next**.
- Aponte para o diretório raiz do Tomcat que você extraiu.
- Defina um **Username** (ex: `admin`) e uma **Password** de sua escolha — esses dados são usados pelo NetBeans para autenticar o manager do Tomcat.
- Clique em **Finish**.

#### 3. Abrir o projeto
- Vá em **File → Open Project** e selecione a pasta `agenda-clinica`.
- O NetBeans reconhece automaticamente projetos Maven.

#### 4. Executar
- Clique com o botão direito no projeto → **Run** (ou pressione `F6`).
- O NetBeans compilará, empacotará e fará o deploy automaticamente no Tomcat.
- Acesse `http://localhost:8080/agenda-clinica/` no navegador.

---

## ‼️ Notas Importantes
- O sistema gera automaticamente dados padrões (como o administrador e um médico de teste). Caso queira desabilitar ou alterar esse comportamento, modifique a classe `SeedService.java`.