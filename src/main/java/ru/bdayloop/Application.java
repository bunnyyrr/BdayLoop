package ru.bdayloop;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.bdayloop.config.AppConfig;
import ru.bdayloop.service.i.UserService;
import ru.bdayloop.web.CorsFilter;
import ru.bdayloop.web.WebConfig;
import ru.bdayloop.web.servlet.UserServlet;


public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        ctx.registerShutdownHook();

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.getSessionHandler().setHttpOnly(true);
        server.setHandler(context);

        context.addServlet(new ServletHolder(new UserServlet(ctx.getBean(UserService.class))), "/users/*");
        context.addFilter(CorsFilter.class, "/*", null);

        AnnotationConfigWebApplicationContext webCtx = new AnnotationConfigWebApplicationContext();
        webCtx.register(WebConfig.class);
        webCtx.setParent(ctx);
        context.addServlet(new ServletHolder("dispatcher", new DispatcherServlet(webCtx)), "/");

        server.start();
        log.info("Сервер запущен на http://localhost:8080");
        server.join();
    }
}