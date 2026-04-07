package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
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

    public Usuario buscarPorEmailESenha(String email, String senha) {
        try (Session session = factory.openSession()) {
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE email = :email AND senha = :senha", Usuario.class);
            query.setParameter("email", email);
            query.setParameter("senha", senha);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Usuario buscarPorId(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(Usuario.class, id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}