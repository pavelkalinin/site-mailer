package xyz.enhorse.site;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         25.07.2016
 */
public class Mailer {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("usage: mailer access.properties.file");
            System.exit(1);
        }

        Configuration configuration = Configuration.loadFromFile(args[0]);

        Controller controller = new Controller(configuration);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(controller), configuration.handler());

        Server server = new Server(configuration.port());
        server.setHandler(context);
        server.start();
        server.join();
    }
}
