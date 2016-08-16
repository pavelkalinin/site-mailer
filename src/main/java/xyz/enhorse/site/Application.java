package xyz.enhorse.site;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         25.07.2016
 */
public class Application {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("usage: java -jar mailer.jar file");
            System.exit(0);
        }

        Configuration configuration = null;
        try {
            configuration = Configuration.loadFromFile(args[0]);
        } catch (Exception ex) {
            System.out.println(String.format("Can't load configuration from the file \'%s\': %s",
                    args[0], ex.getMessage()));
            System.exit(-1);
        }


        MailController mailController = new MailController(configuration);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(mailController), configuration.serviceHandler());

        Server server = new Server(configuration.servicePort());
        server.setHandler(context);
        server.start();
        server.join();
    }
}
