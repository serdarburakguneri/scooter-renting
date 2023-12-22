package service;

import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EmailService {

    private static final Logger logger = Logger.getLogger(EmailService.class);

    private final Mailer mailer;

    @Inject
    public EmailService(Mailer mailer) {
        this.mailer = mailer;
    }

    public void sendEmail(String to, String subject, String body) {
        //mailer.send(Mail.withText(to, subject, body));
        logger.info("bra jobbat!");
    }
}
