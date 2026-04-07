package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;
import br.com.agendaclinica.crud.agenda_clinica.util.HibernateUtil;

public class UsuarioDAO {
    
    private static SessionFactory factory = HibernateUtil.getSessionFactory();

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

    /**
     * Busca usuário por email (sem validar senha aqui)
     * A validação de senha é feita no objeto Usuario
     * @param email Email do usuário
     * @return Usuário encontrado ou null
     */
    public Usuario buscarPorEmail(String email) {
        try (Session session = factory.openSession()) {
            // Usando Prepared Statements automaticamente (Hibernate previne SQL Injection)
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE email = :email", Usuario.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca usuário por email e valida senha (OBSOLETO - USE buscarPorEmail)
     * @deprecated Use buscarPorEmail() e depois usuario.validarSenha()
     */
    @Deprecated
    public Usuario buscarPorEmailESenha(String email, String senha) {
        try (Session session = factory.openSession()) {
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE email = :email", Usuario.class);
            query.setParameter("email", email);
            Usuario usuario = query.uniqueResult();
            
            if (usuario != null && usuario.validarSenha(senha)) {
                return usuario;
            }
            return null;
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