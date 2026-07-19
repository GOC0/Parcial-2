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
import static Database.inscripcionesDB.*;

public class inscripcionesController {

    //para inscribirse. le falta la redirecion
    public static void crearInscripcion(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if(null == buscarUsuario(usuario.getUsuario())){
            ctx.status(400);
            ctx.redirect("/login");
            return;
        };
        String evento = ctx.formParam("nombre");
        Eventos e = buscarEventos(evento);

        if(e.getNumeroCupos()== 0){
            ctx.status(400).result("no hay cupos");
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

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    //para cancelarInscripcion. le falta la redirecion
    public static void cancelarInscripcion(Context ctx) {
        int id = Integer.parseInt(ctx.formParam("id"));
        Inscripciones i= obtenerInscripcion(id);
        if(i!=null){
            eliminarInscripcion(i.getId());
        }else {
            ctx.status(409);
            return;
        }

    }

    public static void marcarAsistencia(int id) {

    }



    }
