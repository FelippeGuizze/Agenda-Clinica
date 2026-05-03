package br.com.agendaclinica.crud.agenda_clinica.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import br.com.agendaclinica.crud.agenda_clinica.model.CrmAutorizado;
import br.com.agendaclinica.crud.agenda_clinica.util.HibernateUtil;
import java.util.List;

public class CrmAutorizadoDAO {

    private static SessionFactory factory = HibernateUtil.getSessionFactory();

    public void salvar(CrmAutorizado crm) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            session.persist(crm);
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao salvar CRM autorizado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Busca um CRM autorizado pelo email e número CRM + UF.
     * Usado na validação do cadastro de médico.
     */
    public CrmAutorizado buscarPorEmailECrm(String email, String crmNumero, String crmUf) {
        try (Session session = factory.openSession()) {
            Query<CrmAutorizado> query = session.createQuery(
                "FROM CrmAutorizado WHERE emailAutorizado = :email AND crmNumero = :crm AND crmUf = :uf",
                CrmAutorizado.class
            );
            query.setParameter("email", email);
            query.setParameter("crm", crmNumero);
            query.setParameter("uf", crmUf.toUpperCase());
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Erro ao buscar CRM por email e número: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica se já existe um CRM com o mesmo número e UF cadastrado.
     */
    public boolean crmJaExiste(String crmNumero, String crmUf) {
        try (Session session = factory.openSession()) {
            Query<CrmAutorizado> query = session.createQuery(
                "FROM CrmAutorizado WHERE crmNumero = :crm AND crmUf = :uf",
                CrmAutorizado.class
            );
            query.setParameter("crm", crmNumero);
            query.setParameter("uf", crmUf.toUpperCase());
            return query.uniqueResult() != null;
        } catch (Exception e) {
            System.err.println("Erro ao verificar CRM existente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se já existe uma autorização para o email informado.
     */
    public boolean emailJaAutorizado(String email) {
        try (Session session = factory.openSession()) {
            Query<CrmAutorizado> query = session.createQuery(
                "FROM CrmAutorizado WHERE emailAutorizado = :email",
                CrmAutorizado.class
            );
            query.setParameter("email", email);
            return query.uniqueResult() != null;
        } catch (Exception e) {
            System.err.println("Erro ao verificar email autorizado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marca o CRM como usado após cadastro bem-sucedido do médico.
     */
    public void marcarComoUsado(Long id) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            CrmAutorizado crm = session.get(CrmAutorizado.class, id);
            if (crm != null) {
                crm.setUsado(true);
                session.merge(crm);
            }
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao marcar CRM como usado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lista todos os CRMs autorizados (para o painel admin).
     */
    public List<CrmAutorizado> listarTodos() {
        try (Session session = factory.openSession()) {
            Query<CrmAutorizado> query = session.createQuery(
                "FROM CrmAutorizado ORDER BY dataCriacao DESC",
                CrmAutorizado.class
            );
            return query.list();
        } catch (Exception e) {
            System.err.println("Erro ao listar CRMs autorizados: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Remove uma autorização de CRM.
     */
    public void remover(Long id) {
        try (Session session = factory.openSession()) {
            Transaction t = session.beginTransaction();
            CrmAutorizado crm = session.get(CrmAutorizado.class, id);
            if (crm != null) {
                session.remove(crm);
            }
            t.commit();
        } catch (Exception e) {
            System.err.println("Erro ao remover CRM autorizado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
