package controller;


import io.javalin.http.Context;
import logic.Usuario;

import static Database.UsuarioDB.buscarUsuario;

public class login {


    public static void loginController(Context ctx) {

        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        Usuario usuario = buscarUsuario(username);
        if (usuario != null) {
            if (usuario.getPassword().equals(password)) {

                ctx.sessionAttribute("usuario", usuario);
                ctx.redirect("/dashboard");
            }else{
                ctx.status(400);
                ctx.redirect("/login");
                return;
            }
        }else{
            ctx.status(400);
            ctx.redirect("/login");
            return;
        }
    }

    public static void CerrarSession(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/login");
    }



}
