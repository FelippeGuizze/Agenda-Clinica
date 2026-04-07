package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;

public class UsuarioDAO {
    
    private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public void salvar(Usuario usuario) {
        
        try (Session session = factory.openSession()) {
            
            Transaction t = session.beginTransaction();
            
            session.persist(usuario);
            
            
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
}