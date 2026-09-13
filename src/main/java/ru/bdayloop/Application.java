package ru.bdayloop;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.bdayloop.dao.GiftDao;
import ru.bdayloop.dao.GroupDao;
import ru.bdayloop.dao.MessageDao;
import ru.bdayloop.dao.UserDao;
import ru.bdayloop.service.GiftService;
import ru.bdayloop.service.GroupService;
import ru.bdayloop.service.MessageService;
import ru.bdayloop.service.UserService;
import ru.bdayloop.service.impl.GiftServiceImpl;
import ru.bdayloop.service.impl.GroupServiceImpl;
import ru.bdayloop.service.impl.MessageServiceImpl;
import ru.bdayloop.service.impl.UserServiceImpl;
import ru.bdayloop.web.servlet.*;

public class Application {
    public static void main(String[] args) throws Exception {
        UserDao userDao = new UserDao();
        GroupDao groupDao = new GroupDao();
        GiftDao giftDao = new GiftDao();
        MessageDao messageDao = new MessageDao();

        UserService userService = new UserServiceImpl(userDao);
        GroupService groupService= new GroupServiceImpl(groupDao);
        GiftService giftService = new GiftServiceImpl(giftDao);
        MessageService messageService = new MessageServiceImpl(messageDao, userDao);

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new HelloServlet()), "/hello");
        context.addServlet(new ServletHolder(new UserServlet(userService)), "/users/*");
        context.addServlet(new ServletHolder(new GroupServlet(groupService)), "/groups/*");
        context.addServlet(new ServletHolder(new GiftServlet(giftService)), "/gifts/*");
        context.addServlet(new ServletHolder(new MessageServlet(messageService)), "/messages/*");

        server.start();
        System.out.println("Сервер запущен на http://localhost:8080");
        server.join();
    }
}