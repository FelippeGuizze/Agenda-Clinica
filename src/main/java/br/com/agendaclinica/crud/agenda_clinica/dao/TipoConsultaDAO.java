package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import br.com.agendaclinica.crud.agenda_clinica.model.TipoConsulta;
import java.util.List;

public class TipoConsultaDAO {

    private static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public void salvar(TipoConsulta tipoConsulta) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(tipoConsulta);
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao salvar tipo de consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<TipoConsulta> buscarPorProfissional(Long profissionalId) {
        try (Session session = factory.openSession()) {
            Query<TipoConsulta> query = session.createQuery(
                "FROM TipoConsulta WHERE profissionalId = :profissionalId AND ativo = true",
                TipoConsulta.class
            );
            query.setParameter("profissionalId", profissionalId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao buscar tipos de consulta: " + e.getMessage());
            return List.of();
        }
    }

    public TipoConsulta buscarPorId(Long id) {
        try (Session session = factory.openSession()) {
            return session.get(TipoConsulta.class, id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar tipo de consulta: " + e.getMessage());
            return null;
        }
    }

    public void deletar(Long id) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            TipoConsulta tipoConsulta = session.get(TipoConsulta.class, id);
            if (tipoConsulta != null) {
                session.remove(tipoConsulta);
            }
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao deletar tipo de consulta: " + e.getMessage());
        }
    }
}