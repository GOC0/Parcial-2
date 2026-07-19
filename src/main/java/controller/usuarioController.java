package controller;

import io.javalin.http.Context;
import logic.Rol;
import logic.Usuario;

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

        if (rolParam == null) {
            ctx.status(400).result("rol es requerido");
            return;
        }
        rolParam = rolParam.trim();

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
        ctx.redirect("/dashboard");
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

}
