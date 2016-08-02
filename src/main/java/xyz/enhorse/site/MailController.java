package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        service = new MailService(configuration.smtpServer(), configuration.recipient());
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
        String name = "";
        String email = "";
        String subject = "";
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("name=")) {
                    name = extractParameterValue(line);
                } else if (line.startsWith("email=")) {
                    email = extractParameterValue(line);
                } else if (line.startsWith("subject=")) {
                    subject = extractParameterValue(line);
                } else if (line.startsWith("content=")) {
                    content.append(extractParameterValue(line)).append(System.lineSeparator());
                } else {
                    content.append(line).append(System.lineSeparator());
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Can't parse a message from the request\'" + request + "\'");
        }

        return new MailMessage.Builder()
                .addName(name)
                .addEmail(email)
                .addSubject(subject)
                .addMessage(content.toString())
                .setCharset(charset)
                .build();
    }


    private String extractParameterValue(final String line) {
        int index = line.indexOf('=');
        return line.substring(index + 1);
    }

}
