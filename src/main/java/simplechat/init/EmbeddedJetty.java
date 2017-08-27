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
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Properties;

public class EmbeddedJetty {

    private static Properties properties;

    public static void main(String[] args) throws Exception {
        properties = new Properties();
        InputStream stream = EmbeddedJetty.class.getResourceAsStream("/webapp/WEB-INF/app.properties");
        properties.load(stream);
        stream.close();

        new EmbeddedJetty().startJetty();
    }

    private void startJetty() throws Exception {
        Server server = new Server();
        server.setConnectors(getConnectors(server));
        server.setHandler(getServletContextHandler(getContext()));
        server.start();
        server.join();
    }

    private static ServletContextHandler getServletContextHandler(WebApplicationContext context) throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(properties.getProperty("jetty.contextPath"));
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)),
                properties.getProperty("jetty.mappingURL"));
        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase(new ClassPathResource("webapp").getURI().toString());
        contextHandler.addFilter(new FilterHolder
                (new DelegatingFilterProxy("springSecurityFilterChain")),
                "/*", EnumSet.allOf(DispatcherType.class));
        return contextHandler;
    }

    private static WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(properties.getProperty("jetty.configLocation"));
        return context;
    }

    private static Connector[] getConnectors(Server server){
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(EmbeddedJetty.class
                .getResource(properties.getProperty("jetty.keyStorePath")).toExternalForm());
        sslContextFactory.setKeyStorePassword(properties.getProperty("jetty.keyStorePassword"));
        sslContextFactory.setKeyManagerPassword(properties.getProperty("jetty.keyManagerPassword"));

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(Integer.parseInt(properties.getProperty("jetty.port")));

        return new Connector[] {sslConnector};
    }
}
