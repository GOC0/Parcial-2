package Database;

import jakarta.persistence.EntityManager;
import logic.Usuario;

import java.util.List;

public class UsuarioDB {

    public static void crearU (Usuario usuario){
        EntityManager em = Conexion.getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        }catch(Exception ex){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }finally {
            em.close();
        }

    }

    public static void eliminarU (Usuario usuario){
        EntityManager em = Conexion.getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario merged = em.merge(usuario);
            em.remove(merged);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public static Usuario buscarUsuario(String usuario) {
        EntityManager em = Conexion.getEntityManager();
        try {
            List<Usuario> resultados = em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.usuario = :usuario",
                            Usuario.class)
                    .setParameter("usuario", usuario)
                    .setMaxResults(1)
                    .getResultList();

            return resultados.isEmpty() ? null : resultados.get(0);

        } finally {
            em.close();
        }
    }
    public static void actualizarU(Usuario usuario) {
        EntityManager em = Conexion.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}
