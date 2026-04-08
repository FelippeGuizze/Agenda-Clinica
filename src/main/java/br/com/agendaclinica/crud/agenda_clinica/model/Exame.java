package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("EXAME")
public class Exame extends Atendimento {

    public Exame() {
        super();
    }

    public Exame(String tipo, Paciente paciente, Profissional profissional, LocalDateTime datahora, String status) {
        super(tipo, paciente, profissional, datahora, status);
    }

    public Exame(String tipo, Paciente paciente, Profissional profissional, LocalDateTime datahora, String status, BigDecimal preco, Long disponibilidadeId) {
        super(tipo, paciente, profissional, datahora, status, preco, disponibilidadeId);
    }

    @Override
    public BigDecimal calcularCusto() {
        // Exames podem ter uma taxa base adicional ou cálculo específico
        BigDecimal base = getPreco() != null ? getPreco() : new BigDecimal("100.00");
        return base.add(new BigDecimal("35.00")); // Taxa laboratorial de Exame
    }

    @Override
    public String gerarOrientacoes() {
        return "Jejum obrigatório de 8 horas. Não ingerir álcool 24h antes.";
    }
}
