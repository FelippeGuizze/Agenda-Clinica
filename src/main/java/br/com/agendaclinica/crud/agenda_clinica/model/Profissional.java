package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profissionais")
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String especialidade;

    @Column(name = "crm_numero", length = 6, nullable = true)
    private String crmNumero;

    @Column(name = "crm_uf", length = 2, nullable = true)
    private String crmUf;

    @Column(name = "tipo_nicho", length = 20, nullable = true)
    private String tipoNicho; // "Consulta" ou "Exame" — definido pelo admin ao autorizar o CRM

    // Construtores
    public Profissional() {}

    public Profissional(String nome, String categoria, String especialidade) {
        this.nome = nome;
        this.categoria = categoria;
        this.especialidade = especialidade;
    }

    /**
     * Retorna o CRM formatado: CRM/UF XXXXXX
     * Retorna null se não houver CRM cadastrado
     */
    public String getCrmFormatado() {
        if (crmNumero == null || crmUf == null) return null;
        return "CRM/" + crmUf + " " + crmNumero;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public String getCrmNumero() { return crmNumero; }
    public void setCrmNumero(String crmNumero) { this.crmNumero = crmNumero; }
    public String getCrmUf() { return crmUf; }
    public void setCrmUf(String crmUf) { this.crmUf = crmUf; }
    public String getTipoNicho() { return tipoNicho; }
    public void setTipoNicho(String tipoNicho) { this.tipoNicho = tipoNicho; }
}

