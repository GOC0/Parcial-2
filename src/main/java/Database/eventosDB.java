package Database;


import jakarta.persistence.EntityManager;
import logic.Eventos;

import java.util.List;

public class eventosDB {

    //para guardar eventos
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


    //para buscar eventos por id
    public static Eventos buscarEvento(int idEvento) {
        EntityManager em = Conexion.getEntityManager();

        try {
            return em.find(Eventos.class, idEvento);
        } finally {
            em.close();
        }
    }

    //para buscar un evento
    public static Eventos  buscarEventos(String nombre){
            EntityManager em = Conexion.getEntityManager();
            try {
                List<Eventos> resultados = em.createQuery(
                                "SELECT e FROM Eventos e WHERE e.nombre = :nombre",
                                Eventos.class)
                        .setParameter("nombre", nombre)
                        .setMaxResults(1)
                        .getResultList();

                return resultados.isEmpty() ? null : resultados.get(0);

            } finally {
                em.close();
            }
    }


    //para buscar la lista de todos los eventos
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

    //para eliminar eventos
    public static void eliminarEv(int id) {
        EntityManager em = Conexion.getEntityManager();

        try {
            em.getTransaction().begin();

            Eventos evento = em.find(Eventos.class, id);

            if (evento != null) {
                em.remove(evento);
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
