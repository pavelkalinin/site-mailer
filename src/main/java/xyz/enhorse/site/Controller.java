package xyz.enhorse.site;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.07.2016
 */
public class Controller extends HttpServlet {

    private final Configuration cfg;


    public Controller(final Configuration configuration) {
        cfg = configuration;
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String from = request.getParameter("from");
        String subject = request.getParameter("subj");
        String message = request.getParameter("msg");

        response.getWriter().print("from: \'" + from
                + "\' with subject \'" + subject
                + "\'\n" + message);
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

}
