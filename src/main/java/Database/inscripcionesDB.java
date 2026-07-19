package Database;

import jakarta.persistence.EntityManager;
import logic.Inscripciones;
import logic.Usuario;

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

    public static Inscripciones obtenerInscripcion(int idUsuario, int idEvento) {
        EntityManager em = Conexion.getEntityManager();

        try {
            return em.createQuery(
                            """
                            SELECT i
                            FROM Inscripciones i
                            WHERE i.usuario.id = :idUsuario
                            AND i.evento.id = :idEvento
                            """,
                            Inscripciones.class
                    )
                    .setParameter("idUsuario", idUsuario)
                    .setParameter("idEvento", idEvento)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

        } finally {
            em.close();
        }
    }
    //para obterner la lista de inscripciones de un usuario
    public static List<Inscripciones> obtenerInscripcionesParaUsuario(int idUsuario) {

        EntityManager em = Conexion.getEntityManager();

        try {

            return em.createQuery(
                            "SELECT i FROM Inscripciones i " +
                                    "JOIN FETCH i.evento " +
                                    "WHERE i.usuario.id = :idUsuario",
                            Inscripciones.class
                    )
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();

        } finally {
            em.close();
        }
    }



    // busca el numero total de inscrito en un evento tomando el id del evento
    public static long totalInscritos(int idEvento) {
        EntityManager em = Conexion.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT COUNT(i) FROM Inscripciones i WHERE i.evento.id = :idEvento",
                            Long.class
                    )
                    .setParameter("idEvento", idEvento)
                    .getSingleResult();

        } finally {
            em.close();
        }
    }

    //busca el total de asistente de para un evento tomando el id del evento
    public static long totalAsistentes(int idEvento) {
        EntityManager em = Conexion.getEntityManager();

        try {
            return em.createQuery(
                            """
                            SELECT COUNT(i)
                            FROM Inscripciones i
                            WHERE i.evento.id = :idEvento
                            AND i.asistencia = true
                            """,
                            Long.class
                    )
                    .setParameter("idEvento", idEvento)
                    .getSingleResult();

        } finally {
            em.close();
        }
    }

    //para buscar el porcentaje de asistencia
    public static double porcentajeAsistencia(int idEvento) {

        long inscritos = totalInscritos(idEvento);
        long asistentes = totalAsistentes(idEvento);

        if (inscritos == 0) {
            return 0;
        }

        double porcentaje = (double) asistentes * 100 / inscritos;

        return Math.round(porcentaje * 100.0) / 100.0;
    }
}
