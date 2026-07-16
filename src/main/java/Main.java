
import config.Rutas;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import logic.Rol;
import logic.Usuario;

import static Database.Conexion.startDb;
import static Database.UsuarioDB.buscarUsuario;
import static Database.UsuarioDB.crearU;

public class Main {

    public static void main(String[] args) {

        startDb();
        Usuario u = new Usuario("admin", "1234", Rol.Administrador);
        Usuario u2 = buscarUsuario("admin");
        if(u2 == null){
            crearU(u);
        }

        Javalin app = Javalin.create(
                config -> {
                    config.staticFiles.add(staticFileConfig -> {
                        staticFileConfig.hostedPath = "/";
                        staticFileConfig.directory = "/publico";
                        staticFileConfig.location = Location.CLASSPATH;
                        staticFileConfig.aliasCheck = null;
                    });
                    config.fileRenderer(new JavalinThymeleaf());

                   Rutas.registrar(config.routes);

                }
        ).start(7000);
    }
}

