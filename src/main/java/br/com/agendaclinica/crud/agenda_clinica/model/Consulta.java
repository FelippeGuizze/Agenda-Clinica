package br.com.agendaclinica.crud.agenda_clinica.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("CONSULTA")
public class Consulta extends Atendimento {

    public Consulta() {
        super();
    }

    public Consulta(String tipo, Paciente paciente, Profissional profissional, LocalDateTime datahora, String status) {
        super(tipo, paciente, profissional, datahora, status);
    }

    public Consulta(String tipo, Paciente paciente, Profissional profissional, LocalDateTime datahora, String status, BigDecimal preco, Long disponibilidadeId) {
        super(tipo, paciente, profissional, datahora, status, preco, disponibilidadeId);
    }

    @Override
    public BigDecimal calcularCusto() {
        // Se a consulta possui um preço cadastrado inicialmente, retorna ele. Senão o preço fixo.
        if (getPreco() != null) {
            return getPreco();
        }
        return new BigDecimal("150.00");
    }

    @Override
    public String gerarOrientacoes() {
        return "Chegar com 15 minutos de antecedência. Trazer documento original com foto.";
    }
}
