package ru.bdayloop;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.bdayloop.config.AppConfig;
import ru.bdayloop.service.i.GiftService;
import ru.bdayloop.service.i.GroupService;
import ru.bdayloop.service.i.MessageService;
import ru.bdayloop.service.i.UserService;
import ru.bdayloop.web.CorsFilter;
import ru.bdayloop.web.servlet.GiftServlet;
import ru.bdayloop.web.servlet.GroupServlet;
import ru.bdayloop.web.servlet.MessageServlet;
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
        context.addServlet(new ServletHolder(new GroupServlet(ctx.getBean(GroupService.class))), "/groups/*");
        context.addServlet(new ServletHolder(new GiftServlet(ctx.getBean(GiftService.class))), "/gifts/*");
        context.addServlet(new ServletHolder(new MessageServlet(ctx.getBean(MessageService.class))), "/messages/*");
        context.addFilter(CorsFilter.class, "/*", null);
        server.start();
        log.info("Сервер запущен на http://localhost:8080");
        server.join();
    }
}