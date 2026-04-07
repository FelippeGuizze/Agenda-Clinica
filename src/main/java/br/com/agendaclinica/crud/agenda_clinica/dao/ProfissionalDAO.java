package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import br.com.agendaclinica.crud.agenda_clinica.model.Profissional;

public class ProfissionalDAO {
    
    private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public void salvar(Profissional profissional) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(profissional);
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao salvar profissional: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Profissional buscarPorId(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(Profissional.class, id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar profissional: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
