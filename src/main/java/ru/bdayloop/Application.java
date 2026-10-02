package ru.bdayloop;

import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bdayloop.dao.i.GiftDao;
import ru.bdayloop.dao.i.GroupDao;
import ru.bdayloop.dao.i.MessageDao;
import ru.bdayloop.dao.i.UserDao;
import ru.bdayloop.dao.impl.GiftDaoImpl;
import ru.bdayloop.dao.impl.GroupDaoImpl;
import ru.bdayloop.dao.impl.MessageDaoImpl;
import ru.bdayloop.dao.impl.UserDaoImpl;
import ru.bdayloop.db.DataSourceFactory;
import ru.bdayloop.service.i.GiftService;
import ru.bdayloop.service.i.GroupService;
import ru.bdayloop.service.i.MessageService;
import ru.bdayloop.service.i.UserService;
import ru.bdayloop.service.impl.GiftServiceImpl;
import ru.bdayloop.service.impl.GroupServiceImpl;
import ru.bdayloop.service.impl.MessageServiceImpl;
import ru.bdayloop.service.impl.UserServiceImpl;
import ru.bdayloop.web.CorsFilter;
import ru.bdayloop.web.servlet.*;


public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        HikariDataSource dataSource = DataSourceFactory.fromEnv();
        Runtime.getRuntime().addShutdownHook(new Thread(dataSource::close));

        UserDao userDao = new UserDaoImpl(dataSource);
        GroupDao groupDao = new GroupDaoImpl(dataSource);
        GiftDao giftDao = new GiftDaoImpl(dataSource);
        MessageDao messageDao = new MessageDaoImpl(dataSource);

        UserService userService = new UserServiceImpl(userDao);
        GroupService groupService= new GroupServiceImpl(groupDao);
        GiftService giftService = new GiftServiceImpl(giftDao);
        MessageService messageService = new MessageServiceImpl(messageDao, userDao);

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.getSessionHandler().setHttpOnly(true);
        server.setHandler(context);

        context.addServlet(new ServletHolder(new UserServlet(userService)), "/users/*");
        context.addServlet(new ServletHolder(new GroupServlet(groupService)), "/groups/*");
        context.addServlet(new ServletHolder(new GiftServlet(giftService)), "/gifts/*");
        context.addServlet(new ServletHolder(new MessageServlet(messageService)), "/messages/*");
        context.addFilter(CorsFilter.class, "/*", null);

        server.start();
        log.info("Сервер запущен на http://localhost:8080");
        server.join();
    }
}