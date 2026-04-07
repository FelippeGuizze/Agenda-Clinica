# AGENDA CLÍNICA - GUIA DE IMPLEMENTAÇÃO

## ✅ O QUE FOI IMPLEMENTADO

### 1. **SeedService** (Serviço de Inicialização)
- ✅ Insere automaticamente um usuário **Administrador** ao iniciar a aplicação
  - Email: `admin@agenda.com`
  - Senha: `admin123`
- ✅ Verifica se usuário admin já existe para não duplicar
- ✅ Fornece método `emailJaExiste()` para validação de emails duplicados

### 2. **Fluxo de Dados Corrigido**
Agora o sistema funciona assim:
- **TODOS os usuários** (Admin, Paciente, Profissional) ficam na tabela `usuarios`
- **Tabela usuarios** possui dois campos de link:
  - `paciente_id`: Link para tabela pacientes (apenas se categoria=1)
  - `profissional_id`: Link para tabela profissionais (apenas se categoria=2)
- **Atendimentos e Observações**: Sem links por enquanto, conforme solicitado

### 3. **Validação de Dados**
- ✅ Verifica se email já existe ANTES de inserir
- ✅ Mensagem **VERMELHA** se email já cadastrado
- ✅ Mensagem **VERDE** se cadastro realizado com sucesso
- ✅ Feedback visual imediato ao usuário

### 4. **Estrutura do Banco de Dados**

```sql
Tabela: usuarios
├── id (PK)
├── nome
├── email (UNIQUE)
├── senha
├── categoria (0=Admin, 1=Paciente, 2=Profissional)
├── paciente_id (FK para pacientes - se categoria=1)
└── profissional_id (FK para profissionais - se categoria=2)

Tabela: pacientes
├── id (PK)
├── nome
└── contato

Tabela: profissionais
├── id (PK)
├── nome
├── categoria
└── especialidade

Tabela: atendimentos (sem links ainda)
├── id (PK)
├── tipo
├── paciente_id
├── profissional_id
├── datahora
└── status

Tabela: observacoes_atendimento (sem links ainda)
├── id (PK)
├── atendimento_id
├── autor
├── texto
└── datahora
```

---

## 🚀 COMO USAR

### Passo 1: Configurar o Banco de Dados
```bash
# Execute o arquivo SQL no MySQL:
mysql -u admin -p < SQL\ Commands.sql
# Digite a senha: 12345678
```

### Passo 2: Compilar o Projeto
```bash
cd /home/uniedit/eclipse-workspace/agenda-clinica/
mvn clean install
```

### Passo 3: Deploy no Tomcat
- Copie o arquivo `.war` gerado para a pasta `webapps` do Tomcat
- Reinicie o Tomcat

### Passo 4: Acessar a Aplicação
```
http://localhost:8080/agenda-clinica/
```

---

## 📋 DADOS DE TESTE INCLUÍDOS

### Administrador
- **Email**: admin@agenda.com
- **Senha**: admin123
- **Categoria**: Admin (0)

### Pacientes
1. João Silva
   - Email: joao.paciente@email.com
   - Senha: senha123
   - Contato: 11987654321

2. Maria Santos
   - Email: maria.paciente@email.com
   - Senha: senha456
   - Contato: 21987654321

### Profissionais
1. Dr. Carlos Medico
   - Email: carlos.medico@email.com
   - Senha: senha789
   - Especialidade: Cardiologia

2. Dra. Ana Especialista
   - Email: ana.especialista@email.com
   - Senha: senha000
   - Especialidade: Dermatologia

---

## 🔍 COMO FUNCIONA A VALIDAÇÃO

### Cadastro de Paciente
1. Usuário preenche: Nome, Email, Senha, Contato
2. Sistema verifica: `SeedService.emailJaExiste(email)`
3. Se existe: ❌ Mensagem vermelha "Email já cadastrado"
4. Se não existe:
   - Cria registro em `pacientes`
   - Obtém o `id` do paciente
   - Cria usuário em `usuarios` com `paciente_id` preenchido
   - ✓ Mensagem verde "Cadastro realizado"

### Cadastro de Profissional
1. Usuário preenche: Nome, Email, Senha, Especialidade
2. Sistema verifica: `SeedService.emailJaExiste(email)`
3. Se existe: ❌ Mensagem vermelha "Email já cadastrado"
4. Se não existe:
   - Cria registro em `profissionais`
   - Obtém o `id` do profissional
   - Cria usuário em `usuarios` com `profissional_id` preenchido
   - ✓ Mensagem verde "Cadastro realizado"

---

## 🎨 INTERFACE

- **Tema**: Escuro com gradientes azul/roxo
- **Cores de Feedback**:
  - 🟢 Verde: Sucesso, email disponível
  - 🔴 Vermelho: Erro, email duplicado
- **Páginas**:
  - `index.jsp`: Página inicial com 2 botões
  - `cadastro.jsp`: Formulário com campos dinâmicos
  - `login.jsp`: Autenticação de usuários

---

## 📂 ARQUIVOS CRIADOS/MODIFICADOS

### Novo criado:
- `util/SeedService.java` - Serviço de inicialização
- `util/AppInitListener.java` - Listener para auto-executar seed
- `controller/VerificaEmailServlet.java` - Verificação de email
- `model/Paciente.java`
- `model/Profissional.java`
- `model/Atendimento.java`
- `model/ObservacaoAtendimento.java`
- `dao/PacienteDAO.java`
- `dao/ProfissionalDAO.java`
- `dao/AtendimentoDAO.java`
- `dao/ObservacaoAtendimentoDAO.java`
- `webapp/cadastro.jsp`
- `webapp/login.jsp`
- `webapp/css/style.css`

### Modificado:
- `model/Usuario.java` - Adicionados campos paciente_id e profissional_id
- `dao/UsuarioDAO.java` - Adicionado método buscarPorEmailESenha()
- `controller/CadastroServlet.java` - Novo fluxo com validação
- `controller/LoginServlet.java` - Login com redirecionamento por categoria
- `index.jsp` - Interface escura simplificada
- `hibernate.cfg.xml` - Mapeamentos atualizados
- `SQL Commands.sql` - Estrutura e dados de teste

---

## ⚠️ PONTOS IMPORTANTES

1. **Email é único**: A validação impede duplicatas
2. **Categoria determina o link**: Paciente (1) → paciente_id, Profissional (2) → profissional_id
3. **Admin é criado automaticamente**: Primeira execução cria o usuário admin
4. **Sem links para atendimentos**: Conforme solicitado, ficam para depois

---

## 🔧 TROUBLESHOOTING

### Erro: "Erro ao conectar com banco de dados"
- Verifique se MySQL está rodando
- Confirme as credenciais em `hibernate.cfg.xml`
- Verifique se database `agenda_clinica` foi criado

### Erro: "Email já existe"
- Limpe a tabela com: `DELETE FROM usuarios WHERE categoria != 0;`
- Ou use outro email para teste

### Admin não foi criado
- Verifique se `AppInitListener.java` está sendo compilado
- Reinicie o Tomcat
- Veja o console do Tomcat para mensagens de seed

---

## ✨ PRÓXIMAS MELHORIAS (Futuro)

1. Adicionar links para atendimentos
2. Adicionar links para observações
3. Dashboard diferenciado para pacientes e médicos
4. Agendamento de consultas
5. Histórico de atendimentos
6. Sistema de notificações

---

**Implementado por**: GitHub Copilot
**Data**: 07/04/2026
**Status**: ✅ Pronto para uso
