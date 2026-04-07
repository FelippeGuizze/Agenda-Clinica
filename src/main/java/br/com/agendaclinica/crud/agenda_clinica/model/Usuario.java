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

    // Construtores padrões p/ hibernate
    public Usuario() {}

    public Usuario(String nome) {
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}