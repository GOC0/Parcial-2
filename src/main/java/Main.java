
import config.Rutas;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

import static Database.Conexion.startDb;

public class Main {

    public static void main(String[] args) {

        startDb();

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

