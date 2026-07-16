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
    public List<Eventos> buscarTodoslosEventos() {

        EntityManager em = Conexion.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT DISTINCT e FROM Eventos",
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
    public static void eliminarEv(String nombre){
        EntityManager em= Conexion.getEntityManager();
        em.getTransaction().begin();

        try {
            Eventos eventos = em.find(Eventos.class, nombre);
            if (eventos != null) {
                em.remove(eventos);
                em.getTransaction().commit();
            }
        }catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                e.printStackTrace();
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
