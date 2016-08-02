package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.07.2016
 */
public class MailController extends HttpServlet {

    private final MailService service;
    private final String redirectToSuccess;
    private final String redirectToFail;


    public MailController(final Configuration configuration) {
        Validate.notNull("configuration for mail controller", configuration);
        service = new MailService(configuration);
        redirectToSuccess = Validate.notNull("redirect to if success URL", configuration.redirectToSuccess());
        redirectToFail = Validate.notNull("redirect to if fail URL", configuration.redirectToFail());
    }


    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            MailMessage mail = generateMail(request);
            service.sendMail(mail);
            response.sendRedirect(redirectToSuccess);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (Exception ex) {
            response.sendRedirect(redirectToFail);
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }


    private MailMessage generateMail(final HttpServletRequest request) {
        Validate.notNull("request", request);

        String charset = Validate.defaultIfNull(request.getCharacterEncoding(), StandardCharsets.UTF_8.name());
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");

        return new MailMessage.Builder()
                .addName(name)
                .addEmail(email)
                .addSubject(subject)
                .addMessage(content)
                .setCharset(charset)
                .build();
    }
}
