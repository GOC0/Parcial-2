package Database;


import jakarta.persistence.EntityManager;
import logic.Eventos;

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
    public static Eventos  buscarEventps(Eventos  eventos){

    }


    //para buscar la lista de todos los eventos




    //para eliminar eventios
    public static void EliminarEv(String nombre){
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
}
