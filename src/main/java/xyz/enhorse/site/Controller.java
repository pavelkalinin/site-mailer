package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

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

    private final Mailer mailer;


    public Controller(final Configuration configuration) {
        mailer = new Mailer(Validate.notNull("configuration", configuration));
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            MailMessage message = new MailMessage.Builder()
                    .addSender(request.getParameter("from"))
                    .addSubject(request.getParameter("subj"))
                    .addMessage(request.getParameter("msg"))
                    .build();
            mailer.sendMessage(message);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (Exception ex) {
            response.getWriter().append(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

}
