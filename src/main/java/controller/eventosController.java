package controller;


import io.javalin.http.Context;
import logic.Eventos;
import java.time.LocalDate;


import static Database.eventosDB.guardarEv;

public class eventosController {

    public static void CrearEventos(Context ctx){
        String nombre = ctx.formParam("nombre");
        String descripcion = ctx.formParam("descripcion");
        String lugar = ctx.formParam("lugar");
        LocalDate fecha = LocalDate.parse(ctx.formParam("fecha"));
        int cupos=  Integer.parseInt(ctx.formParam("cupos"));

        if(nombre.isEmpty() || descripcion.isEmpty() || lugar.isEmpty()){
            ctx.status(400);
            ctx.result("El registro no puede estar vacio");
        }else{
            Eventos eventos = new Eventos(nombre,descripcion,lugar,fecha,cupos);
            guardarEv(eventos);
        }

    }

    public static void EliminarEventos(Context ctx){
        String nombre = ctx.formParam("nombre");

    }
}
