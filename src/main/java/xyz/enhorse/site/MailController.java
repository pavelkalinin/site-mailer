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
public class MailController extends HttpServlet {

    private final MailService service;


    public MailController(final Configuration configuration) {
        Validate.notNull("configuration for mail controller", configuration);
        service = new MailService(configuration.smtpServer(), configuration.recipient());
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            MailMessage mail = new MailMessage.Builder()
                    .addFrom(request.getParameter("name"))
                    .addEmail(request.getParameter("email"))
                    .addSubject(request.getParameter("subject"))
                    .addMessage(request.getParameter("content"))
                    .build();
            service.sendMail(mail);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (Exception ex) {
            response.getWriter().append(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

}
