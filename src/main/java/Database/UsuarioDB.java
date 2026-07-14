package Database;

import jakarta.persistence.EntityManager;
import logic.Usuario;

import java.util.List;

public class UsuarioDB {

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
}
