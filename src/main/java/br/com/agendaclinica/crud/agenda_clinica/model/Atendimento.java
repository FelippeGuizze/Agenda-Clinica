package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "atendimentos")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    @Column(nullable = false)
    private LocalDateTime datahora;

    @Column(nullable = false)
    private String status; // "Disponível", "Agendado", "Em progresso", "Concluído", "Cancelado"

    @Column(precision = 10, scale = 2)
    private BigDecimal preco; // Preço da consulta

    @Column(name = "disponibilidade_id")
    private Long disponibilidadeId; // Link para a disponibilidade usada

    // Construtores
    public Atendimento() {}

    public Atendimento(String tipo, Paciente paciente, Profissional profissional, LocalDateTime datahora, String status) {
        this.tipo = tipo;
        this.paciente = paciente;
        this.profissional = profissional;
        this.datahora = datahora;
        this.status = status;
    }

    public Atendimento(String tipo, Paciente paciente, Profissional profissional, LocalDateTime datahora, String status, BigDecimal preco, Long disponibilidadeId) {
        this.tipo = tipo;
        this.paciente = paciente;
        this.profissional = profissional;
        this.datahora = datahora;
        this.status = status;
        this.preco = preco;
        this.disponibilidadeId = disponibilidadeId;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional profissional) { this.profissional = profissional; }
    public LocalDateTime getDatahora() { return datahora; }
    public void setDatahora(LocalDateTime datahora) { this.datahora = datahora; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Long getDisponibilidadeId() { return disponibilidadeId; }
    public void setDisponibilidadeId(Long disponibilidadeId) { this.disponibilidadeId = disponibilidadeId; }
}