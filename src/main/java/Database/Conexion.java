package Database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.h2.tools.Server;

import java.sql.SQLException;

public class Conexion {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("MiPARCIAL2");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


    // para abrir h2 en modo servidor
    public static void startDb() {
        try {
            //Modo servidor H2.
            Server.createTcpServer("-tcpPort",
                    "9092",
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            //Abriendo el cliente web. El valor 0 representa puerto aleatorio.
            String status = Server.createWebServer("-trace", "-webPort", "0").start().getStatus();
            //
            System.out.println("Status Web: "+status);
        }catch (SQLException ex){
            System.out.println("Problema con la base de datos: "+ex.getMessage());
        }
    }

    public void stopDb(){
        emf.close();
    }
}
