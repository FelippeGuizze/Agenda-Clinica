package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.*;

/**
 * Disponibilidade de horários do profissional
 * Permite ao profissional definir em quais dias e horários ele está disponível
 */
@Entity
@Table(name = "disponibilidade_profissionais")
public class DisponibilidadeProfissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profissional_id", nullable = false)
    private Long profissionalId;

    @Column(nullable = false)
    private String diaSemana; // "SEGUNDA", "TERÇA", "QUARTA", "QUINTA", "SEXTA", "SABADO", "DOMINGO"

    @Column(nullable = false)
    private String horario; // "08:00", "09:00", "10:00", etc

    @Column(nullable = false)
    private Boolean ativo; // true = disponível, false = indisponível

    // Construtores
    public DisponibilidadeProfissional() {}

    public DisponibilidadeProfissional(Long profissionalId, String diaSemana, String horario) {
        this.profissionalId = profissionalId;
        this.diaSemana = diaSemana;
        this.horario = horario;
        this.ativo = true;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProfissionalId() { return profissionalId; }
    public void setProfissionalId(Long profissionalId) { this.profissionalId = profissionalId; }
    
    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }
    
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
