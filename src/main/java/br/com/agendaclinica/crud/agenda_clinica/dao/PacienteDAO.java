package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import br.com.agendaclinica.crud.agenda_clinica.model.Paciente;

public class PacienteDAO {
    
    private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public void salvar(Paciente paciente) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(paciente);
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao salvar paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Paciente buscarPorId(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(Paciente.class, id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
