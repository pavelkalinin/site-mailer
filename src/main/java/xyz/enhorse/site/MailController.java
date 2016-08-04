package xyz.enhorse.site;

import xyz.enhorse.commons.Validate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author <a href="mailto:pavel13kalinin@gmail.com">Pavel Kalinin</a>
 *         26.07.2016
 */
public class MailController extends HttpServlet {
    private static final String FORM_NAME = "name";
    private static final String FORM_EMAIL = "email";
    private static final String FORM_SUBJECT = "subject";
    private static final String FORM_CONTENT = "content";
    private static final String FORM_SUCCESS = "success";
    private static final String FORM_FAIL = "fail";

    private static final String DEFAULT_REDIRECT = "/";

    private final MailService service;
    private final Configuration config;
    private final String admin;


    public MailController(final Configuration configuration) {
        config = Validate.notNull("configuration for mail controller", configuration);
        service = new MailService(configuration);
        admin = Validate.defaultIfNull(configuration.emailAdmin(), "admin's email address");
    }


    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            MailMessage mail = generateMail(request);
            service.sendMail(mail);
            response.sendRedirect(checkRedirect(request.getParameter(FORM_SUCCESS)));
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (Exception ex) {
            service.sendMail(generateMailToAdmin(ex));
            response.sendRedirect(checkRedirect(request.getParameter(FORM_FAIL)));
            response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }


    private MailMessage generateMail(final HttpServletRequest request) {
        String charset = Validate.defaultIfNull(request.getCharacterEncoding(), StandardCharsets.UTF_8.name());
        String name = request.getParameter(FORM_NAME);
        String email = request.getParameter(FORM_EMAIL);
        String subject = request.getParameter(FORM_SUBJECT);
        String content = request.getParameter(FORM_CONTENT);
        return new MailMessage.Builder()
                .setName(name)
                .setAddress(email)
                .setSubject(subject)
                .setContent(content)
                .addContent(String.format("%n%naddress to replay: %s <%s>", name, email))
                .setEncoding(charset)
                .build();
    }


    private MailMessage generateMailToAdmin(final Exception ex) {
        Date now = new Date();
        return new MailMessage.Builder()
                .setName("Mailer")
                .setAddress(admin)
                .setSubject("error: " + now)
                .setContent(String.format("stacktrace:%n%s%n%n", stackTraceToString(ex)))
                .addContent(String.format("configuration:%n%s%n%n", config))
                .addContent(String.format("timestamp:%n%s", now))
                .setEncoding(Charset.defaultCharset().name())
                .build();
    }


    private String checkRedirect(final String parameter) {
        //TODO check parameter is valid URL
        return Validate.defaultIfNull(parameter, DEFAULT_REDIRECT);
    }


    private static String stackTraceToString(final Exception exception) {
        String stacktrace;

        try (StringWriter writer = new StringWriter();
             PrintWriter printer = new PrintWriter(writer, true)) {
            exception.printStackTrace(printer);
            stacktrace = writer.toString();
        } catch (Exception ex) {
            stacktrace = "Can't print stacktrace:\'" + ex.getMessage() + "\'";
        }

        return stacktrace;
    }
}
