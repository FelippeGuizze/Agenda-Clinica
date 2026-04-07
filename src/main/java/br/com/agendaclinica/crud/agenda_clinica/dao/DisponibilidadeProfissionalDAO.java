package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import br.com.agendaclinica.crud.agenda_clinica.model.DisponibilidadeProfissional;
import br.com.agendaclinica.crud.agenda_clinica.util.HibernateUtil;
import java.util.List;

public class DisponibilidadeProfissionalDAO {
    
    private static SessionFactory factory = HibernateUtil.getSessionFactory();

    public void salvar(DisponibilidadeProfissional disponibilidade) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(disponibilidade);
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao salvar disponibilidade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<DisponibilidadeProfissional> buscarPorProfissional(Long profissionalId) {
        try (Session session = factory.openSession()) {
            Query<DisponibilidadeProfissional> query = session.createQuery(
                "FROM DisponibilidadeProfissional WHERE profissionalId = :profissionalId AND ativo = true", 
                DisponibilidadeProfissional.class
            );
            query.setParameter("profissionalId", profissionalId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao buscar disponibilidades: " + e.getMessage());
            return List.of();
        }
    }

    public List<DisponibilidadeProfissional> buscarDisponibilidadesDefault() {
        try (Session session = factory.openSession()) {
            Query<DisponibilidadeProfissional> query = session.createQuery(
                "FROM DisponibilidadeProfissional WHERE profissionalId = -1 AND ativo = true", 
                DisponibilidadeProfissional.class
            );
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao buscar disponibilidades padrão: " + e.getMessage());
            return List.of();
        }
    }

    public List<DisponibilidadeProfissional> buscarPorDiaESemana(String dia, String horario) {
        try (Session session = factory.openSession()) {
            Query<DisponibilidadeProfissional> query = session.createQuery(
                "FROM DisponibilidadeProfissional WHERE diaSemana = :dia AND horario = :horario AND ativo = true", 
                DisponibilidadeProfissional.class
            );
            query.setParameter("dia", dia);
            query.setParameter("horario", horario);
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao buscar disponibilidades: " + e.getMessage());
            return List.of();
        }
    }

    public void deletar(Long id) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            DisponibilidadeProfissional disponibilidade = session.get(DisponibilidadeProfissional.class, id);
            if (disponibilidade != null) {
                session.remove(disponibilidade);
            }
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao deletar disponibilidade: " + e.getMessage());
        }
    }

    public DisponibilidadeProfissional buscarPorId(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(DisponibilidadeProfissional.class, id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar disponibilidade: " + e.getMessage());
            return null;
        }
    }
}