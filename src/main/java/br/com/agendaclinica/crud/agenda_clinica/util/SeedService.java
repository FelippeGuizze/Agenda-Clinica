package br.com.agendaclinica.crud.agenda_clinica.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;
import br.com.agendaclinica.crud.agenda_clinica.model.DisponibilidadeProfissional;

public class SeedService {

    private static SessionFactory factory = HibernateUtil.getSessionFactory();

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
                
                // Inserir usuário administrador (senha será criptografada automaticamente)
                Usuario admin = new Usuario(
                    "Administrador", 
                    "admin@agenda.com", 
                    "admin123", 
                    0  // 0 = Admin, 1 = Paciente, 2 = Profissional
                );
                
                session.persist(admin);
                t.commit();
                
                System.out.println("✓ Usuário administrador criado com sucesso!");
                SecurityUtil.registrarAuditoria("admin@agenda.com", "Criação de admin", true);
            } else {
                System.out.println("✓ Usuário administrador já existe no banco de dados.");
            }

            // Criar disponibilidades padrão (12:00 e 16:00 para todos os dias)
            criarDisponibilidadesDefault(session);

        } catch (Exception e) {
            System.err.println("✗ Erro ao fazer seed do banco de dados: " + e.getMessage());
            e.printStackTrace();
            SecurityUtil.registrarAuditoria("sistema", "Seed do banco de dados", false);
        }
    }

    /**
     * Cria disponibilidades padrão (12:00 e 16:00) para todos os dias
     */
    private static void criarDisponibilidadesDefault(Session session) {
        try {
            // Verificar se já existem disponibilidades
            Query<DisponibilidadeProfissional> query = session.createQuery(
                "FROM DisponibilidadeProfissional WHERE horario IN ('12:00', '16:00')", 
                DisponibilidadeProfissional.class
            );
            
            if (query.list().isEmpty()) {
                Transaction t = session.beginTransaction();
                
                String[] dias = {"SEGUNDA", "TERÇA", "QUARTA", "QUINTA", "SEXTA", "SABADO", "DOMINGO"};
                String[] horarios = {"12:00", "16:00"};
                
                // Criar disponibilidades para todos os profissionais (profissional_id = -1 significa padrão)
                for (String dia : dias) {
                    for (String horario : horarios) {
                        DisponibilidadeProfissional disponibilidade = new DisponibilidadeProfissional(
                            -1L, // -1 = disponibilidade padrão para todos
                            dia,
                            horario
                        );
                        session.persist(disponibilidade);
                    }
                }
                
                t.commit();
                System.out.println("✓ Disponibilidades padrão criadas! (12:00 e 16:00 em todos os dias)");
                SecurityUtil.registrarAuditoria("sistema", "Criação de disponibilidades padrão", true);
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar disponibilidades padrão: " + e.getMessage());
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