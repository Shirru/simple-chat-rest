package simplechat.init;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.EnumSet;

public class EmbeddedJetty {

    private static final int DEFAULT_PORT = 8080;
    private static final String CONTEXT_PATH = "/";
    private static final String CONFIG_LOCATION = "simplechat.config";
    private static final String MAPPING_URL = "/*";

    public static void main(String[] args) throws Exception {
        new EmbeddedJetty().startJetty(DEFAULT_PORT);
    }

    private void startJetty(int port) throws Exception {
        Server server = new Server();
        server.setConnectors(getConnectors(server));
        server.setHandler(getServletContextHandler(getContext()));
        server.start();
        server.join();
    }

    private static ServletContextHandler getServletContextHandler(WebApplicationContext context) throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(CONTEXT_PATH);
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), MAPPING_URL);
        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase(new ClassPathResource("webapp").getURI().toString());
        contextHandler.addFilter(new FilterHolder(
                new DelegatingFilterProxy("springSecurityFilterChain")), "/*",
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        return contextHandler;
    }

    private static WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        return context;
    }

    private static Connector[] getConnectors(Server server){
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(DEFAULT_PORT);

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(EmbeddedJetty.class
                .getResource("/webapp/WEB-INF/keystore.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("qwertyasdfg");
        sslContextFactory.setKeyManagerPassword("qwertyasdfg");

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(8443);

        return new Connector[] {connector, sslConnector};
    }
}
