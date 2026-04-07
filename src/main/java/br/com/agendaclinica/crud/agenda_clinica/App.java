package br.com.agendaclinica.crud.agenda_clinica;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;

public class App {
    public static void main(String[] args) {
        
    	// String de conexão com o Hibernate
        Configuration cfg = new Configuration().configure();
        
        try (SessionFactory factory = cfg.buildSessionFactory();
             Session session = factory.openSession()) {
            
        	// Msg Teste
            System.out.println("Conexão estabelecida com sucesso!");

            // Teste inserindo dados
            Transaction t = session.beginTransaction();
            
            Usuario novoUsuario = new Usuario("Uniedit Dev", "dev@uniedit.com", "123456", 0);
            session.persist(novoUsuario);
            
            t.commit();
            System.out.println("Usuário salvo com ID: " + novoUsuario.getId());
            
        } catch (Exception e) {
            System.err.println("Erro na conexão: " + e.getMessage());
            e.printStackTrace();
        }
    	    	 	
    }
}