package net.instantcom.mm7;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;

import java.io.IOException;

/**
 * <p>
 * starting up Jetty with {@link MM7Servlet} wired up with Spring
 * </p>
 * Created by bardug on 6/26/2016.
 */
public class MM7ServletInJettyWithSpring {

    private Server webServer;

    public static void main(String[] args) throws Exception {
        new MM7ServletInJettyWithSpring().start();
    }

    private void start() throws Exception {
        initJetty();
        addServletContext();
        webServer.start();
    }

    private void initJetty() {
        webServer = new Server();
//        ServerConnector httpConnector = new ServerConnector(webServer); // java 8
        SelectChannelConnector httpConnector = new SelectChannelConnector(); // java 6
        httpConnector.setPort(8080);
        httpConnector.setHost("127.0.0.1");

        webServer.addConnector(httpConnector);
    }

    private void addServletContext() throws IOException {
        ServletContextHandler context = new ServletContextHandler(webServer, "/", ServletContextHandler.SESSIONS);
        context.setSessionHandler(new SessionHandler());

        // Setup Spring context
        context.addEventListener(new ContextLoaderListener());
        context.setInitParameter("contextConfigLocation", "classpath:net/instantcom/mm7/spring-context.xml");

        // MM7 servlet
        ServletHolder mm7Servlet = new ServletHolder(MM7Servlet.class);
        context.setAttribute(MM7Servlet.VASP_BEAN_ATTRIBUTE, "mm7Vasp"); // bean id as configured in spring xml context
        context.addServlet(mm7Servlet, "/mm7");
    }
}
