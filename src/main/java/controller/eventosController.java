package controller;


import io.javalin.http.Context;
import logic.Eventos;
import java.time.LocalDate;

import static Database.eventosDB.*;

public class eventosController {

    //eso es para crear eventos. me falto poner la redirecion a la pagina
    public static void crearEventos(Context ctx){
        String nombre = ctx.formParam("nombre");
        String descripcion = ctx.formParam("descripcion");
        String lugar = ctx.formParam("lugar");
        String fechaStr = ctx.formParam("fecha");
        String cuposStr = ctx.formParam("cupos");

        if (nombre == null || nombre.isBlank()
                || descripcion == null || descripcion.isBlank()
                || lugar == null || lugar.isBlank()
                || fechaStr == null || fechaStr.isBlank()
                || cuposStr == null || cuposStr.isBlank()) {
            ctx.status(400).result("El registro no puede estar vacío");
            return;
        }
        try {
            LocalDate fecha = LocalDate.parse(fechaStr);
            int cupos = Integer.parseInt(cuposStr);

            Eventos eventos = new Eventos(nombre, descripcion, lugar, fecha, cupos);
            guardarEv(eventos);
            ctx.status(201).result("Evento creado correctamente");
        } catch (Exception e) {
            ctx.status(500).result("Error al crear el evento");
        }

    }
    //eso es para actualizar eventos. me falto poner la redirecion a la pagina
    public static void actualizarEventos(Context ctx){
        String nombre = ctx.formParam("nombre");
        String descripcion = ctx.formParam("descripcion");
        String lugar = ctx.formParam("lugar");
        String fechaStr = ctx.formParam("fecha");
        String cuposStr = ctx.formParam("cupos");

            if (nombre == null || nombre.isBlank()
                    || descripcion == null || descripcion.isBlank()
                    || lugar == null || lugar.isBlank()
                    || fechaStr == null || fechaStr.isBlank()
                    || cuposStr == null || cuposStr.isBlank()) {
                ctx.status(400).result("El registro no puede estar vacío");
                return;
            }
        try {
            LocalDate fecha = LocalDate.parse(fechaStr);
            int cupos = Integer.parseInt(cuposStr);

            Eventos evento = new Eventos(nombre, descripcion, lugar, fecha, cupos);
            actualizarEv(evento);
            ctx.status(201).result("Evento actualizado correctamente");
        } catch (Exception e) {
            ctx.status(500).result("Error al actualizar el evento");
        }

    }


    //eso es para elimar eventos. me falto poner la redirecion a la pagina
    public static void EliminarEventos(Context ctx){
        String nombre = ctx.formParam("nombre");

        Eventos ev = buscarEventos(nombre);

        if(ev != null){
            eliminarEv(nombre);
            ctx.status(200);

        }else{
            ctx.status(404).result("No se encontro el evento");
            return;
        }

    }

    public static void despublicarEventos(Context ctx){
        String nombre= ctx.formParam("nombre");
        Eventos ev = buscarEventos(nombre);
        if(ev != null){
            despublicarEv(ev.getId());
            ctx.status(200);
        }
    }
}
