package controller;

import io.javalin.http.Context;
import logic.AsistenciaRequest;
import logic.Inscripciones;
import logic.Rol;
import logic.Usuario;
import org.jetbrains.annotations.NotNull;

import static Database.inscripcionesDB.buscarPorToken;
import static Database.inscripcionesDB.marcarComoAsistio;

public class asistenciaController {

    public static void marcarAsistencia(@NotNull Context ctx) {

        Usuario usuario = ctx.sessionAttribute("usuario");

        if (usuario == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        // esto lo arregle: comparabas el rol (que es un enum) contra un String y eso
        // siempre daba false, por eso el escaner tiraba 403 siempre
        if (usuario.getRol() != Rol.Organizador && usuario.getRol() != Rol.Administrador) {
            ctx.status(403).result("No tiene permisos para validar entradas");
            return;
        }

        try {
            AsistenciaRequest body = ctx.bodyAsClass(AsistenciaRequest.class);

            if (body.getToken() == null || body.getToken().isBlank()) {
                ctx.status(400).result("Token inválido");
                return;
            }

            Inscripciones inscripcion = buscarPorToken(body.getToken());

            if (inscripcion == null) {
                ctx.status(404).result("Entrada no encontrada");
                return;
            }

            if (inscripcion.isAsistencia()) {
                ctx.status(409).result("Esta entrada ya fue utilizada");
                return;
            }

            marcarComoAsistio(inscripcion.getId());

            ctx.status(200).result("Asistencia registrada: " + inscripcion.getEvento().getTitulo());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al validar entrada");
        }
    }
}
