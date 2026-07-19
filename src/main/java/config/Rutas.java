package config;


import controller.asistenciaController;
import controller.eventosController;
import controller.inscripcionesController;
import controller.login;
import io.javalin.config.RoutesConfig;

public class Rutas {

    public static void registrar (RoutesConfig routes) {

        routes.get("/", ctx -> {ctx.redirect("/login.html");});
        routes.get("/login", ctx -> {ctx.redirect("/login.html");});
        routes.get("/dashboard", ctx -> {ctx.render("/Templates/dashboard.html");});
        routes.get("/api/eventos", eventosController::listarEventos);
        routes.get("/api/inscripciones", inscripcionesController::listarInscripciones);

        //rutas post del login
        routes.post("/login", login::loginController);
        routes.post("/cerrarSession",login::CerrarSession);




        routes.post("/api/eventos", eventosController::crearEventos);
        routes.put("/api/eventos/{id}", eventosController::actualizarEventos);
        routes.delete("/api/eventos/{id}", eventosController::EliminarEventos);
        routes.patch("/api/eventos/{id}/despublicar", eventosController::despublicarEventos);

        routes.post("/api/inscripcion/{idEvento}", inscripcionesController::crearInscripcion);
        routes.delete("/api/inscripcion/{idEvento}", inscripcionesController::cancelarInscripcion);
        routes.post("/api/asistencia", asistenciaController::marcarAsistencia);
    }


}
