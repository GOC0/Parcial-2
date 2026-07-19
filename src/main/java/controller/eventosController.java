package controller;


import io.javalin.http.Context;
import logic.Eventos;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static Database.eventosDB.*;

public class eventosController {

    //eso es para crear eventos. me falto poner la redirecion a la pagina
    public static void crearEventos(Context ctx) {

        try {

            Eventos dto = ctx.bodyAsClass(Eventos.class);

            if (dto.getTitulo() == null || dto.getTitulo().isBlank()
                    || dto.getLugar() == null || dto.getLugar().isBlank()
                    || dto.getFecha() == null
                    || dto.getCupo() <= 0) {

                ctx.status(400).result("Datos inválidos");
                return;
            }

            Eventos evento = new Eventos(
                    dto.getTitulo(),
                    dto.getDescripcion(),
                    dto.getLugar(),
                    dto.getFecha(),
                    dto.getCupo()
            );


            guardarEv(evento);

            ctx.status(201).result("Evento creado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al crear el evento");
        }
    }


    //eso es para actualizar eventos. me falto poner la redirecion a la pagina
    public static void actualizarEventos(Context ctx) {

        try {

            Eventos dto = ctx.bodyAsClass(Eventos.class);

            if (dto.getTitulo() == null || dto.getTitulo().isBlank()
                    || dto.getLugar() == null || dto.getLugar().isBlank()
                    || dto.getFecha() == null
                    || dto.getCupo() <= 0) {

                ctx.status(400).result("Datos inválidos");
                return;
            }

            Eventos evento = new Eventos(
                    dto.getTitulo(),
                    dto.getDescripcion(),
                    dto.getLugar(),
                    dto.getFecha(),
                    dto.getCupo()
            );

            actualizarEv(evento);

            ctx.status(200).result("Evento actualizado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al actualizar el evento");
        }
    }


    //eso es para elimar eventos. me falto poner la redirecion a la pagina
    public static void EliminarEventos(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        Eventos evento = buscarEvento(id);

        if (evento == null) {
            ctx.status(404).result("No se encontró el evento");
            return;
        }

        eliminarEv(id);

        ctx.status(200).result("Evento eliminado correctamente");
    }

    public static void despublicarEventos(Context ctx) {

        int id = Integer.parseInt(ctx.pathParam("id"));

        Eventos evento = buscarEvento(id);

        if (evento == null) {
            ctx.status(404).result("Evento no encontrado");
            return;
        }

        despublicarEv(id);

        ctx.status(200).result("Evento despublicado correctamente");
    }

    public static void listarEventos(Context ctx) {
        List<Eventos> eventos = obtenerEventos();

        ctx.json(eventos);
    }
}
