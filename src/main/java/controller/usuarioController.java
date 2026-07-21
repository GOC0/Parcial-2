package controller;

import io.javalin.http.Context;
import logic.Rol;
import logic.Usuario;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Database.UsuarioDB.*;

public class usuarioController {


    public static void crearUsuario(Context ctx){
        String usuario = ctx.formParam("usuario");
        String password = ctx.formParam("password");

        Usuario u = buscarUsuario(usuario);
        if(u == null){
            Usuario u1 = new Usuario(usuario, password);
            crearU(u1);
            ctx.status(201).result("Usuario creado correctamente");
            ctx.redirect("/dashboard");

        }else {
            ctx.status(409).result("Usuario ya existente");
            ctx.redirect("/login");
        }
    }

    public static void actualizarUsuario(Context ctx) {
        String usuario = ctx.formParam("usuario");
        String rolParam = ctx.formParam("rol");
        System.out.println("rolParam = '" + rolParam + "'");

        if (rolParam == null) {
            ctx.status(400).result("rol es requerido");
            return;
        }

        if (rolParam.equals(Rol.Administrador.name())) {
            ctx.status(409).result("no se puede editar");
            return;
        }

        Usuario u = buscarUsuario(usuario);
        if (u == null) {
            ctx.status(404).result("usuario no encontrado");
            return;
        }

        switch (rolParam) {
            case "Organizador":
                u.setRol(Rol.Organizador);
                actualizarU(u);
                break;
            case "Participante":
                u.setRol(Rol.Participante);
                actualizarU(u);
                break;
            default:
                ctx.status(400).result("rol invalido");
                return;


        }

        ctx.status(200).result("usuario actualizado");
    }

    public static void eliminarUsuario(Context ctx){
        String usuario = ctx.formParam("usuario");
        Usuario u = buscarUsuario(usuario);
        if (usuario.equals("admin")){
            ctx.status(409).result("no se puede eliminar");
            ctx.redirect("/dashboard");
            return;
        }

        if(u != null){
            eliminarU(u);
            ctx.status(204);
            ctx.redirect("/dashboard");
        }else  {
            ctx.status(409).result("Usuario no existente");
            ctx.redirect("/dashboard");
            return;
        }
    }

    public static void listarUsuarios(@NotNull Context ctx) {
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
            List<Usuario> lista = obtenerTodosLosUsuarios();

            List<Map<String, Object>> respuesta = new ArrayList<>();

            for (Usuario u : lista) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", u.getId());
                item.put("usuario", u.getUsuario());
                item.put("rol", u.getRol());
                item.put("bloqueado", u.isBloqueado());
                respuesta.add(item);
            }

            ctx.json(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al obtener usuarios");
        }
    }


    public static void cambiarRol(Context ctx) {

        Usuario usuarioSesion = ctx.sessionAttribute("usuario");

        if (usuarioSesion == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        if (usuarioSesion.getRol() != Rol.Administrador) {
            ctx.status(403).result("No tiene permisos para esta acción");
            return;
        }

        try {

            int idUsuario = Integer.parseInt(ctx.pathParam("id"));

            if (idUsuario == usuarioSesion.getId()) {
                ctx.status(400).result("No puede cambiar su propio rol");
                return;
            }

            Usuario usuario = buscarUsuarioPorId(idUsuario);

            if (usuario == null) {
                ctx.status(404).result("Usuario no encontrado");
                return;
            }

            Map<String, String> datos = ctx.bodyAsClass(Map.class);

            Rol nuevoRol = Rol.valueOf(datos.get("rol"));

            usuario.setRol(nuevoRol);

            actualizarU(usuario);

            ctx.result("Rol actualizado correctamente");

        } catch (IllegalArgumentException e) {

            ctx.status(400).result("Rol inválido");

        } catch (Exception e) {

            e.printStackTrace();
            ctx.status(500).result("Error al cambiar el rol");

        }
    }

    public static void bloquearUsuario(@NotNull Context ctx) {
        Usuario usuarioSesion = ctx.sessionAttribute("usuario");

        if (usuarioSesion == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        if (usuarioSesion.getRol() != Rol.Administrador) {
            ctx.status(403).result("No tiene permisos para esta acción");
            return;
        }

        try {
            int idUsuario = Integer.parseInt(ctx.pathParam("id"));

            Usuario usuarioObjetivo = buscarUsuarioPorId(idUsuario);

            if (usuarioObjetivo == null) {
                ctx.status(404).result("Usuario no encontrado");
                return;
            }

            if (usuarioObjetivo.getRol() == Rol.Administrador) {
                ctx.status(403).result("No se puede bloquear a un usuario administrador");
                return;
            }

            if (usuarioObjetivo.isBloqueado()) {
                ctx.status(409).result("El usuario ya está bloqueado");
                return;
            }

            bloquearU(idUsuario);

            ctx.status(200).result("Usuario bloqueado correctamente");

        } catch (NumberFormatException e) {
            ctx.status(400).result("ID inválido");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al bloquear usuario");
        }
    }

    public static void desbloquearUsuario(@NotNull Context ctx) {
        Usuario usuarioSesion = ctx.sessionAttribute("usuario");

        if (usuarioSesion == null) {
            ctx.status(401).result("Debe iniciar sesión");
            return;
        }

        if (usuarioSesion.getRol() != Rol.Administrador) {
            ctx.status(403).result("No tiene permisos para esta acción");
            return;
        }

        try {
            int idUsuario = Integer.parseInt(ctx.pathParam("id"));

            Usuario usuarioObjetivo = buscarUsuarioPorId(idUsuario);

            if (usuarioObjetivo == null) {
                ctx.status(404).result("Usuario no encontrado");
                return;
            }

            if (!usuarioObjetivo.isBloqueado()) {
                ctx.status(409).result("El usuario ya está desbloqueado");
                return;
            }

            desbloquearU(idUsuario);

            ctx.status(200).result("Usuario desbloqueado correctamente");

        } catch (NumberFormatException e) {
            ctx.status(400).result("ID inválido");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al desbloquear usuario");
        }
    }
}
