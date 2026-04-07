package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;

public class AtendimentoDAO {
    
    private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public void salvar(Atendimento atendimento) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(atendimento);
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao salvar atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Atendimento buscarPorId(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(Atendimento.class, id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar atendimento: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
