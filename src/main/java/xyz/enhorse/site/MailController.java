package xyz.enhorse.site;

import org.apache.log4j.Logger;
import xyz.enhorse.commons.Email;
import xyz.enhorse.commons.Pretty;
import xyz.enhorse.commons.Validate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
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

    private final Configuration config;
    private final Logger logger;
    private final MailService service;


    public MailController(final Configuration configuration) {
        config = configuration;
        logger = configuration.logger();
        service = new MailService(configuration.smtpServer(), configuration.emailFrom());
    }


    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException {
        logger.debug(String.format("%s to send a mail has been received", request));

        try {
            sendMail(generateMail(request));
            response.sendRedirect(checkRedirect(request.getParameter(FORM_SUCCESS)));
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (Exception ex) {
            sendErrorReport(ex);
            try {
                response.getWriter().append(ex.getMessage());
                response.sendRedirect(checkRedirect(request.getParameter(FORM_FAIL)));
                response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            } catch (IOException iex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        logger.debug(String.format("%s has been processed: %d", request, response.getStatus()));
    }


    private MailMessage generateMail(final HttpServletRequest request) {
        String charset = Validate.defaultIfNull(request.getCharacterEncoding(), StandardCharsets.UTF_8.name());
        String name = request.getParameter(FORM_NAME);
        String email = request.getParameter(FORM_EMAIL);
        String subject = request.getParameter(FORM_SUBJECT);
        String content = request.getParameter(FORM_CONTENT);
        MailMessage mail = new MailMessage.Builder()
                .setName(name)
                .setAddress(email)
                .setSubject(subject)
                .setContent(content)
                .addContent(String.format("%n" + "mailto:%s", email))
                .setEncoding(charset)
                .build();

        logger.debug("Generated mail:" + System.lineSeparator() + mail);
        return mail;
    }


    private MailMessage generateErrorReport(final Exception ex) {
        Date now = new Date();
        MailMessage report = new MailMessage.Builder()
                .setSubject("Error report: " + now)
                .setContent(String.format("timestamp:%n%s%n", now))
                .addContent(String.format("stacktrace:%n%s%n", Pretty.format(ex)))
                .addContent(String.format("configuration:%n%s%n", config))
                .setEncoding(Charset.defaultCharset().name())
                .build();

        logger.warn("Generated error report:" + report);
        return report;
    }


    private void sendMail(final MailMessage mail) {
        Email address = config.emailTo();

        service.sendMail(address, mail);
        logger.info(String.format("Message from \'%s <%s>\' has been sent to \'%s\'",
                mail.name(), mail.address(), address));
    }


    private void sendErrorReport(final Exception exception) {
        Email address = config.emailAdmin();

        try {
            MailMessage report = generateErrorReport(exception);
            service.sendMail(address, report);
            logger.warn("Error report has been sent to \'" + address + "\'");
        } catch (Exception ex) {
            logger.error("Couldn't send email to \'" + address + "\'", ex);
        }
    }


    private String checkRedirect(final String parameter) {
        try {
            logger.debug("Redirecting to \'" + new URI(parameter) + "\'");
            return parameter;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return DEFAULT_REDIRECT;
        }
    }
}
