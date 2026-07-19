package controller;

import io.javalin.http.Context;
import logic.Eventos;
import logic.Inscripciones;
import logic.Usuario;
import logic.codigoQR;

import java.time.LocalDateTime;
import java.util.UUID;

import static Database.UsuarioDB.buscarUsuario;
import static Database.eventosDB.buscarEventos;
import static Database.eventosDB.actualizarEv;
import static Database.inscripcionesDB.*;

public class inscripcionesController {

    public static void crearInscripcion(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if(null == buscarUsuario(usuario.getUsuario())){
            ctx.status(400);
            ctx.redirect("/login");
            return;
        };
        String evento = ctx.formParam("nombre");
        Eventos e = buscarEventos(evento);

        if(e == null || e.getNumeroCupos()== 0){
            ctx.status(400).result("No hay evento disponible o sin cupos");
            ctx.redirect("/dashboard");
            return;
        }else{
            e.setNumeroCupos(e.getNumeroCupos()-1);
        }

        try {

            String token = UUID.randomUUID().toString();

            String contenidoQR = String.format(
                    "{\"evento\":%d,\"usuario\":%d,\"token\":\"%s\"}",
                    e.getId(),
                    usuario.getId(),
                    token
            );
            byte[] qr = codigoQR.generarQR(contenidoQR);

            Inscripciones inscripcion = new Inscripciones();
            inscripcion.setEvento(e);
            inscripcion.setUsuario(usuario);
            inscripcion.setTokenValidacion(token);
            inscripcion.setQrCode(qr);
            inscripcion.setFechaInscripcion(LocalDateTime.now());
            guardarInscripcion(inscripcion);
            actualizarEv(e);

            ctx.status(201).result("Inscripción realizada correctamente");
            ctx.redirect("/dashboard");

        } catch (Exception ex) {
            ctx.status(500).result("Error al inscribirse");
            ex.printStackTrace();
        }

    }

    public static void cancelarInscripcion(Context ctx) {
        int id = Integer.parseInt(ctx.formParam("id"));
        Inscripciones i= obtenerInscripcion(id);
        if(i!=null){
            eliminarInscripcion(i.getId());
            ctx.status(204);
            ctx.redirect("/dashboard");
        }else {
            ctx.status(409);
            ctx.redirect("/dashboard");
            return;
        }

    }
}
