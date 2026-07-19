package controller;

import io.javalin.http.Context;
import logic.Eventos;
import logic.Inscripciones;
import logic.Usuario;
import logic.codigoQR;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;

import static Database.eventosDB.*;
import static Database.inscripcionesDB.*;

public class inscripcionesController {

    //para inscribirse. le falta la redirecion
    public static void crearInscripcion(Context ctx) {

        System.out.println("Entró al controlador");
        System.out.println("ID Evento: " + ctx.pathParam("idEvento"));

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        int idEvento = Integer.parseInt(ctx.pathParam("idEvento"));

        Eventos evento = buscarEvento(idEvento);

        if (evento == null) {
            ctx.status(404).result("Evento no encontrado");
            return;
        }

        // Verificar inscripción existente
        if (obtenerInscripcion(usuario.getId(), idEvento) != null) {
            ctx.status(409).result("Ya estás inscrito en este evento");
            return;
        }

        if (evento.getCupo() <= 0) {
            ctx.status(400).result("No hay cupos disponibles");
            return;
        }

        try {

            evento.setCupo(evento.getCupo() - 1);
            actualizarEv(evento);

            String token = UUID.randomUUID().toString();

            String contenidoQR = String.format(
                    "{\"evento\":%d,\"usuario\":%d,\"token\":\"%s\"}",
                    evento.getId(),
                    usuario.getId(),
                    token
            );

            byte[] qr = codigoQR.generarQR(contenidoQR);

            Inscripciones inscripcion = new Inscripciones();
            inscripcion.setEvento(evento);
            inscripcion.setUsuario(usuario);
            inscripcion.setTokenValidacion(token);
            inscripcion.setQrCode(qr);
            inscripcion.setFechaInscripcion(LocalDateTime.now());

            guardarInscripcion(inscripcion);

            ctx.status(201).result("Inscripción realizada correctamente");

        } catch (Exception ex) {
            ex.printStackTrace();
            ctx.status(500).result("Error al registrar la inscripción");
        }
    }

    //para cancelarInscripcion. le falta la redirecion
    public static void cancelarInscripcion(Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        int idEvento = Integer.parseInt(ctx.pathParam("idEvento"));

        Inscripciones inscripcion = obtenerInscripcion(usuario.getId(), idEvento);

        if (inscripcion == null) {
            ctx.status(404).result("No existe la inscripción");
            return;
        }

        try {


            Eventos evento = buscarEvento(idEvento);

            evento.setCupo(evento.getCupo() + 1);
            actualizarEv(evento);

            eliminarInscripcion(inscripcion.getId());

            ctx.status(200).result("Inscripción cancelada correctamente");

        } catch (Exception ex) {

            ex.printStackTrace();
            ctx.status(500).result("Error al cancelar la inscripción");
        }
    }

    public static void marcarAsistencia(int id) {

    }


    public static void listarInscripciones(@NotNull Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        List<Inscripciones> lista =
                obtenerInscripcionesParaUsuario(usuario.getId());

        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Inscripciones i : lista) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i.getId());
            item.put("eventoId", i.getEvento().getId());
            item.put("titulo", i.getEvento().getTitulo());
            item.put("lugar", i.getEvento().getLugar());
            item.put("fecha", i.getEvento().getFecha());

            String qrBase64 = i.getQrCode() != null
                    ? Base64.getEncoder().encodeToString(i.getQrCode())
                    : "";
            item.put("qr", qrBase64);

            respuesta.add(item);
        }

        ctx.json(respuesta);
    }
}
