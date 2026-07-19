
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import config.Rutas;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;
import io.javalin.rendering.template.JavalinThymeleaf;
import logic.Rol;
import logic.Usuario;

import static Database.Conexion.startDb;
import static Database.UsuarioDB.buscarUsuario;
import static Database.UsuarioDB.crearU;

public class Main {

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
                    config.jsonMapper(new JavalinJackson(mapper, true));
                   Rutas.registrar(config.routes);

                }
        ).start(7000);
    }
}

