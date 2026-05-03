package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import br.com.agendaclinica.crud.agenda_clinica.util.HibernateUtil;

import java.util.List;

public class AtendimentoDAO {
    
    private static SessionFactory factory = HibernateUtil.getSessionFactory();

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

    public List<Atendimento> buscarConsultasDisponiveis() {
        try (Session session = factory.openSession()) {
            Query<Atendimento> query = session.createQuery(
                "FROM Atendimento WHERE status = 'Disponível' ORDER BY datahora ASC",
                Atendimento.class
            );
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao buscar consultas disponíveis: " + e.getMessage());
            return List.of();
        }
    }

    public List<Atendimento> buscarConsultasDisponiveisFiltradas(String tipo, String especialidade) {
        try (Session session = factory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM Atendimento WHERE status = 'Disponível'");
            
            boolean temTipo = tipo != null && !tipo.trim().isEmpty() && !tipo.equals("Todos");
            boolean temEspecialidade = especialidade != null && !especialidade.trim().isEmpty() && !especialidade.equals("Todas");
            
            if (temTipo) {
                hql.append(" AND tipo = :tipo");
            }
            if (temEspecialidade) {
                hql.append(" AND profissional.especialidade = :especialidade");
            }
            
            hql.append(" ORDER BY datahora ASC");
            
            Query<Atendimento> query = session.createQuery(hql.toString(), Atendimento.class);
            
            if (temTipo) {
                query.setParameter("tipo", tipo);
            }
            if (temEspecialidade) {
                query.setParameter("especialidade", especialidade);
            }
            
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao buscar consultas filtradas: " + e.getMessage());
            return List.of();
        }
    }

    public List<String> listarTiposDisponiveis() {
        try (Session session = factory.openSession()) {
            Query<String> query = session.createQuery(
                "SELECT DISTINCT a.tipo FROM Atendimento a WHERE a.status = 'Disponível' ORDER BY a.tipo",
                String.class
            );
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao listar tipos disponíveis: " + e.getMessage());
            return List.of();
        }
    }

    public List<Atendimento> buscarPorPaciente(Long pacienteId) {
        try (Session session = factory.openSession()) {
            Query<Atendimento> query = session.createQuery(
                "FROM Atendimento WHERE paciente.id = :pacienteId ORDER BY datahora DESC",
                Atendimento.class
            );
            query.setParameter("pacienteId", pacienteId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao buscar atendimentos do paciente: " + e.getMessage());
            return List.of();
        }
    }

    public List<Atendimento> buscarPorProfissional(Long profissionalId) {
        try (Session session = factory.openSession()) {
            Query<Atendimento> query = session.createQuery(
                "FROM Atendimento WHERE profissional.id = :profissionalId ORDER BY datahora DESC",
                Atendimento.class
            );
            query.setParameter("profissionalId", profissionalId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao buscar atendimentos do profissional: " + e.getMessage());
            return List.of();
        }
    }

    public void atualizar(Atendimento atendimento) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            session.merge(atendimento);
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Atendimento> listarTodos() {
        try (Session session = factory.openSession()) {
            Query<Atendimento> query = session.createQuery(
                "FROM Atendimento",
                Atendimento.class
            );
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao listar atendimentos: " + e.getMessage());
            return List.of();
        }
    }
}