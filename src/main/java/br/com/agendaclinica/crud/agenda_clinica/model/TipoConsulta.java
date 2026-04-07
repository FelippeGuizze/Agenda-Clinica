package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Tipos de consulta oferecidos pelos profissionais com preços
 */
@Entity
@Table(name = "tipos_consulta")
public class TipoConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profissional_id", nullable = false)
    private Long profissionalId;

    @Column(nullable = false)
    private String nome; // Ex: "Consulta Geral", "Consulta Especializada", etc.

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo; // true = disponível, false = indisponível

    // Construtores
    public TipoConsulta() {}

    public TipoConsulta(Long profissionalId, String nome, BigDecimal preco, String descricao) {
        this.profissionalId = profissionalId;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.ativo = true;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProfissionalId() { return profissionalId; }
    public void setProfissionalId(Long profissionalId) { this.profissionalId = profissionalId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}