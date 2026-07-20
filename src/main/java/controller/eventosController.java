package controller;


import io.javalin.http.Context;
import logic.Eventos;
import logic.Rol;
import logic.Usuario;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Database.eventosDB.*;
import static Database.inscripcionesDB.totalAsistencia;
import static Database.inscripcionesDB.totalInscritos;

public class eventosController {

    // esto lo agregue yo: sin esto cualquiera sin login podia crear/editar/borrar eventos
    // (requisito 2 del pdf) devuelve true si hay que cortar, ya deja el status puesto
    private static boolean sinPermiso(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return true;
        }

        if (usuario.getRol() != Rol.Organizador && usuario.getRol() != Rol.Administrador) {
            ctx.status(403).result("No tiene permisos para gestionar eventos");
            return true;
        }

        return false;
    }

    // pa crear eventos
    public static void crearEventos(Context ctx) {

        if (sinPermiso(ctx)) return;

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


    // pa actualizar eventos
    public static void actualizarEventos(Context ctx) {

        if (sinPermiso(ctx)) return;

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));

            // esto lo arregle: antes se creaba un Eventos nuevo sin id y el merge
            // insertaba un duplicado en vez de editar. ahora busco el evento real por el id de la ruta
            Eventos evento = buscarEvento(id);

            if (evento == null) {
                ctx.status(404).result("Evento no encontrado");
                return;
            }

            Eventos dto = ctx.bodyAsClass(Eventos.class);

            if (dto.getTitulo() == null || dto.getTitulo().isBlank()
                    || dto.getLugar() == null || dto.getLugar().isBlank()
                    || dto.getFecha() == null
                    || dto.getCupo() <= 0) {

                ctx.status(400).result("Datos inválidos");
                return;
            }

            evento.setTitulo(dto.getTitulo());
            evento.setDescripcion(dto.getDescripcion());
            evento.setLugar(dto.getLugar());
            evento.setFecha(dto.getFecha());
            evento.setCupo(dto.getCupo());

            actualizarEv(evento);

            ctx.status(200).result("Evento actualizado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al actualizar el evento");
        }
    }


    // pa eliminar eventos
    public static void EliminarEventos(Context ctx) {

        if (sinPermiso(ctx)) return;

        int id = Integer.parseInt(ctx.pathParam("id"));

        Eventos evento = buscarEvento(id);

        if (evento == null) {
            ctx.status(404).result("No se encontró el evento");
            return;
        }

        try {
            eliminarEv(id);
            ctx.status(200).result("Evento eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al eliminar el evento");
        }
    }

    public static void despublicarEventos(Context ctx) {

        if (sinPermiso(ctx)) return;

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

    public static void obtenerResumen(@NotNull Context ctx) {
        try {
            int idEvento = Integer.parseInt(ctx.pathParam("id"));

            Eventos evento = buscarEvento(idEvento);

            if (evento == null) {
                ctx.status(404).result("Evento no encontrado");
                return;
            }

            int totalInscritos = (int) totalInscritos(idEvento);
            int totalAsistencias = (int) totalAsistencia(idEvento);

            Map<String, Object> resumen = new HashMap<>();
            resumen.put("eventoId", evento.getId());
            resumen.put("titulo", evento.getTitulo());
            resumen.put("lugar", evento.getLugar());
            resumen.put("fecha", evento.getFecha());
            resumen.put("cuposTotales", evento.getCupo());
            resumen.put("inscritos", totalInscritos);
            resumen.put("asistencias", totalAsistencias);
            resumen.put("cuposDisponibles", evento.getCupo() - totalInscritos);
            resumen.put("porcentajeAsistencia",
                    totalInscritos == 0 ? 0 : (totalAsistencias * 100.0 / totalInscritos));

            ctx.json(resumen);

        } catch (NumberFormatException e) {
            ctx.status(400).result("ID de evento inválido");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al obtener resumen");
        }

    }

    public static void listarTodosLosEventos(@NotNull Context ctx) {
        Usuario usuarioSesion = ctx.sessionAttribute("usuario");

        if (usuarioSesion == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        if (usuarioSesion.getRol() != Rol.Administrador) {
            ctx.status(403).result("No tiene permisos para ver esta información");
            return;
        }

        try {
            List<Eventos> lista = obtenerTodosLosE();

            List<Map<String, Object>> respuesta = new ArrayList<>();

            for (Eventos e : lista) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", e.getId());
                item.put("titulo", e.getTitulo());
                item.put("descripcion", e.getDescripcion());
                item.put("lugar", e.getLugar());
                item.put("fecha", e.getFecha());
                item.put("cupos", e.getCupo());
                item.put("publicado", e.isPublicado());
                respuesta.add(item);
            }

            ctx.json(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al obtener eventos");
        }
    }

    public static void publicarEvento(@NotNull Context ctx) {
        Usuario usuarioSesion = ctx.sessionAttribute("usuario");

        if (usuarioSesion == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        if (usuarioSesion.getRol() != Rol.Administrador || usuarioSesion.getRol() != Rol.Organizador) {
            ctx.status(403).result("No tiene permisos para esta acción");
            return;
        }

        try {
            int idEvento = Integer.parseInt(ctx.pathParam("id"));

            Eventos evento = buscarEvento(idEvento);

            if (evento == null) {
                ctx.status(404).result("Evento no encontrado");
                return;
            }

            if (evento.isPublicado()) {
                ctx.status(409).result("El evento ya está publicado");
                return;
            }

            publicarEv(idEvento);

            ctx.status(200).result("Evento publicado correctamente");

        } catch (NumberFormatException e) {
            ctx.status(400).result("ID inválido");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al publicar evento");
        }
    }
}
