package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import br.com.agendaclinica.crud.agenda_clinica.model.ObservacaoAtendimento;

public class ObservacaoAtendimentoDAO {
    
    private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public void salvar(ObservacaoAtendimento observacao) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(observacao);
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao salvar observação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ObservacaoAtendimento buscarPorId(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(ObservacaoAtendimento.class, id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar observação: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
