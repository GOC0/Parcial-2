package Database;

import jakarta.persistence.EntityManager;
import logic.Inscripciones;

import java.util.List;

public class inscripcionesDB {

    public static void guardarInscripcion(Inscripciones inscripciones){
        EntityManager em = Conexion.getEntityManager();
        try{
        em.getTransaction().begin();
        em.persist(inscripciones);
        em.getTransaction().commit();
        }catch(Exception ex){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        }finally{
            em.close();
        }
    }
    public static void eliminarInscripcion(int id){
        EntityManager em = Conexion.getEntityManager();
        try {
            em.getTransaction().begin();
            Inscripciones inscripcion = em.find(Inscripciones.class, id);
            if (inscripcion != null) {
                em.remove(inscripcion);
            }
            em.getTransaction().commit();
        }catch (Exception ex){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        }finally{
            em.close();
        }
    }

    public static Inscripciones obtenerInscripcion(int id) {
        EntityManager em = Conexion.getEntityManager();
        try {
            return em.find(Inscripciones.class, id);
        } finally {
            em.close();
        }
    }

    public static List<Inscripciones> obtenerInscripciones() {
        EntityManager em = Conexion.getEntityManager();

        try {
            return em.createNamedQuery(
                    "inscripciones.findAll",
                    Inscripciones.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}
