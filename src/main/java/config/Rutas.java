package config;


import controller.eventosController;
import controller.login;
import io.javalin.config.RoutesConfig;

public class Rutas {

    public static void registrar (RoutesConfig routes) {

        routes.get("/", ctx -> {ctx.redirect("/login.html");});
        routes.get("/login", ctx -> {ctx.redirect("/login.html");});


        //rutas post del login
        routes.post("/login", login::loginController);
        routes.post("/cerrarSession",login::CerrarSession);



        // rutas post para eventos
        routes.post("/crearEventos", eventosController::crearEventos);
        routes.post("/actualizarEventos", eventosController::actualizarEventos);
        routes.post("eliminarEventos", eventosController::EliminarEventos);


    }


}
