package Database;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import logic.Eventos;

import java.util.List;

public class eventosDB {

    // pa guardar eventos
    public static  void guardarEv(Eventos  eventos){
        EntityManager em = Conexion.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(eventos);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
    }


    // pa buscar eventos por id
    public static Eventos buscarEvento(int idEvento) {
        EntityManager em = Conexion.getEntityManager();

        try {
            return em.find(Eventos.class, idEvento);
        } finally {
            em.close();
        }
    }
    public static List<Eventos> obtenerTodosLosE() {
        EntityManager em = Conexion.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM Eventos e ORDER BY e.fecha DESC",
                    Eventos.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // pa la lista de todos los eventos publicados
    public static List<Eventos> obtenerEventos() {
        EntityManager em = Conexion.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT e FROM Eventos e WHERE e.publicado = true",
                    Eventos.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

public static void actualizarEv(Eventos  eventos){
        EntityManager em = Conexion.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(eventos);
            em.getTransaction().commit();
        }catch(Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }finally {
            em.close();
        }
}

    // pa eliminar eventos
    public static void eliminarEv(int id) {
        EntityManager em = Conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            // esto lo agregue yo: si el evento tenia gente inscrita, la FK evento_id
            // no dejaba borrarlo y quedaba en silencio como que si se habia borrado
            // (el catch se tragaba el error). por eso primero borro sus inscripciones
            em.createQuery("DELETE FROM Inscripciones i WHERE i.evento.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            Eventos evento = em.find(Eventos.class, id);

            if (evento != null) {
                em.remove(evento);
            }

            em.getTransaction().commit();

        } catch (Exception ex) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            // esto tambien lo agregue: relanzo el error pa que el controlador no
            // responda "eliminado correctamente" cuando en verdad fallo
            throw new RuntimeException(ex);

        } finally {
            em.close();
        }
    }

    public static void publicarEv(int idEvento) {

        EntityManager em = Conexion.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            Eventos evento = em.find(Eventos.class, idEvento);

            if (evento == null) {
                return;
            }

            evento.setPublicado(true);

            em.merge(evento);

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw e;

        } finally {

            em.close();

        }
    }

    public static void despublicarEv(int id) {
        EntityManager em = Conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            Eventos evento = em.find(Eventos.class, id);

            if (evento != null) {
                evento.setPublicado(false);
            }

            em.getTransaction().commit();

        } catch (Exception ex) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            ex.printStackTrace();

        } finally {
            em.close();
        }
    }
}
