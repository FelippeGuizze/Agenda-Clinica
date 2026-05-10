package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa um CRM autorizado pelo administrador para cadastro de médicos.
 * Formato do CRM brasileiro: CRM/UF XXXXXX (ex: CRM/SP 123456)
 * - crm_numero: até 6 dígitos numéricos
 * - crm_uf: sigla do estado (2 letras)
 */
@Entity
@Table(name = "crm_autorizados")
public class CrmAutorizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crm_numero", nullable = false, length = 6)
    private String crmNumero;

    @Column(name = "crm_uf", nullable = false, length = 2)
    private String crmUf;

    @Column(name = "email_autorizado", nullable = false, unique = true)
    private String emailAutorizado;

    @Column(name = "nome_autorizado", nullable = false)
    private String nomeAutorizado;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean usado = false;

    @Column(name = "tipo_nicho", length = 20, nullable = true)
    private String tipoNicho; // "Consulta" ou "Exame" — definido pelo admin

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    // Construtores
    public CrmAutorizado() {}

    public CrmAutorizado(String crmNumero, String crmUf, String emailAutorizado) {
        this.crmNumero = crmNumero;
        this.crmUf = crmUf.toUpperCase();
        this.emailAutorizado = emailAutorizado;
        this.usado = false;
        this.dataCriacao = LocalDateTime.now();
    }

    public CrmAutorizado(String crmNumero, String crmUf, String emailAutorizado, String nomeAutorizado, String tipoNicho) {
        this.crmNumero = crmNumero;
        this.crmUf = crmUf.toUpperCase();
        this.emailAutorizado = emailAutorizado;
        this.nomeAutorizado = nomeAutorizado;
        this.tipoNicho = tipoNicho;
        this.usado = false;
        this.dataCriacao = LocalDateTime.now();
    }

    /**
     * Retorna o CRM no formato padrão brasileiro: CRM/UF XXXXXX
     */
    public String getCrmFormatado() {
        return "CRM/" + crmUf + " " + crmNumero;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCrmNumero() { return crmNumero; }
    public void setCrmNumero(String crmNumero) { this.crmNumero = crmNumero; }
    public String getCrmUf() { return crmUf; }
    public void setCrmUf(String crmUf) { this.crmUf = crmUf.toUpperCase(); }
    public String getEmailAutorizado() { return emailAutorizado; }
    public void setEmailAutorizado(String emailAutorizado) { this.emailAutorizado = emailAutorizado; }
    public String getNomeAutorizado() { return nomeAutorizado; }
    public void setNomeAutorizado(String nomeAutorizado) { this.nomeAutorizado = nomeAutorizado; }
    public Boolean getUsado() { return usado; }
    public void setUsado(Boolean usado) { this.usado = usado; }
    public String getTipoNicho() { return tipoNicho; }
    public void setTipoNicho(String tipoNicho) { this.tipoNicho = tipoNicho; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
