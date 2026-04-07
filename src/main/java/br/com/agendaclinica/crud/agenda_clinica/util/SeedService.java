package br.com.agendaclinica.crud.agenda_clinica.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;

public class SeedService {

    private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    /**
     * Insere dados iniciais no banco de dados
     * Chamado apenas uma vez ao iniciar a aplicação
     */
    public static void seedDatabase() {
        try (Session session = factory.openSession()) {
            // Verificar se já existe usuário admin
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE email = :email", Usuario.class);
            query.setParameter("email", "admin@agenda.com");
            
            if (query.uniqueResult() == null) {
                Transaction t = session.beginTransaction();
                
                // Inserir usuário administrador
                Usuario admin = new Usuario(
                    "Administrador", 
                    "admin@agenda.com", 
                    "admin123", 
                    0  // 0 = Admin, 1 = Paciente, 2 = Profissional
                );
                
                session.persist(admin);
                t.commit();
                
                System.out.println("✓ Usuário administrador criado com sucesso!");
            } else {
                System.out.println("✓ Usuário administrador já existe no banco de dados.");
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao fazer seed do banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verifica se um email já existe na tabela de usuários
     */
    public static boolean emailJaExiste(String email) {
        try (Session session = factory.openSession()) {
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE email = :email", Usuario.class);
            query.setParameter("email", email);
            return query.uniqueResult() != null;
        } catch (Exception e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }
}
