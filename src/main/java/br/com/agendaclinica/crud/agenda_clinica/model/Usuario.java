package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios") // Nome da tabela que vai ser criada na DB -> Usuarios
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Adicionando o AUTO-INCREMENTO
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private Integer categoria; // 0 = Admin, 1 = Paciente, 2 = Profissional/Médico

    @Column(name = "paciente_id", nullable = true)
    private Long pacienteId; // Link para tabela pacientes (se categoria = 1)

    @Column(name = "profissional_id", nullable = true)
    private Long profissionalId; // Link para tabela profissionais (se categoria = 2)

    // Construtores padrões p/ hibernate
    public Usuario() {}

    public Usuario(String nome, String email, String senha, Integer categoria) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Integer getCategoria() { return categoria; }
    public void setCategoria(Integer categoria) { this.categoria = categoria; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getProfissionalId() { return profissionalId; }
    public void setProfissionalId(Long profissionalId) { this.profissionalId = profissionalId; }
}