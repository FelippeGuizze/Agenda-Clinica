package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "observacoes_atendimento")
public class ObservacaoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(nullable = false)
    private LocalDateTime datahora;

    // Construtores
    public ObservacaoAtendimento() {}

    public ObservacaoAtendimento(Atendimento atendimento, String autor, String texto, LocalDateTime datahora) {
        this.atendimento = atendimento;
        this.autor = autor;
        this.texto = texto;
        this.datahora = datahora;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Atendimento getAtendimento() { return atendimento; }
    public void setAtendimento(Atendimento atendimento) { this.atendimento = atendimento; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDatahora() { return datahora; }
    public void setDatahora(LocalDateTime datahora) { this.datahora = datahora; }
}
