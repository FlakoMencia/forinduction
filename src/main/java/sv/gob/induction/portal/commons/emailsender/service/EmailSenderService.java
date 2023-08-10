package sv.gob.induction.portal.commons.emailsender.service;

import java.nio.charset.StandardCharsets;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import sv.gob.induction.portal.commons.emailsender.model.Mail;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender emailSender;

    public void enviarCorreo(Mail mail) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String correoOrigen = "no-reply@forinduction.gob.sv";
            helper.setFrom(correoOrigen);
            helper.setTo(mail.getCorreosDestino().toArray(new String[]{}));

            helper.setSubject(mail.getEncabezado());
            helper.setText(mail.getCuerpo(), true);

            if (mail.getFile() != null) {
                FileSystemResource resource = new FileSystemResource(mail.getFile());
                helper.addInline(mail.getFileName(), resource);
            }

            emailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalArgumentException("Error al intentar enviar el correo electronico... ", ex);
        }
    }

}
