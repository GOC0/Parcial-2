package config;


import controller.login;
import io.javalin.config.RouterConfig;
import io.javalin.config.RoutesConfig;

public class lo {

    public static void registrar (RoutesConfig routes) {

        routes.get("/", ctx -> {ctx.redirect("/login.html");});
        routes.get("/login", ctx -> {ctx.redirect("/login.html");});
        routes.get("/dashboard", ctx -> {ctx.render("/dashboard.html");});

        routes.post("/login", login::loginController);


    }


}
