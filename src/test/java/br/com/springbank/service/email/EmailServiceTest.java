package br.com.springbank.service.email;

import br.com.springbank.service.exceptions.email.EmailMessageRequiredException;
import br.com.springbank.service.exceptions.email.EmailRecipientRequiredException;
import br.com.springbank.service.exceptions.email.EmailSendingException;
import br.com.springbank.service.exceptions.email.EmailTitleRequiredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "email", "no-reply@springbank.com");
    }

    @Test
    void deveEnviarEmailComSucesso() {
        String recipient = "destinatario@email.com";
        String title = "Assunto";
        String message = "Conteúdo do e-mail.";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(recipient, title, message);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void deveRetornarUmExcecaoAoFalharOEnvioDoEmail() {
        doThrow(new MailException("Erro no envio") {}).when(javaMailSender).send(any(SimpleMailMessage.class));

        var ex = Assertions.assertThrows(EmailSendingException.class,
                () -> this.emailService.sendEmail("destinatario@gmail.com", "Assunto", "Mensagem")
        );

        assertTrue(ex.getMessage().contains("Falha ao enviar e-mail"));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void deveLancarExcecaoSeDestinatarioForNulo() {
        var ex = assertThrows(EmailRecipientRequiredException.class, () ->
                emailService.sendEmail(null, "Assunto", "Mensagem")
        );
        assertEquals("Destinatário do e-mail não pode ser nulo ou vazio.", ex.getMessage());
        verifyNoInteractions(javaMailSender);
    }

    @Test
    void deveLancarExcecaoSeDestinatarioForVazio() {
        var ex = assertThrows(EmailRecipientRequiredException.class, () ->
                emailService.sendEmail("", "Assunto", "Mensagem")
        );
        assertEquals("Destinatário do e-mail não pode ser nulo ou vazio.", ex.getMessage());
        verifyNoInteractions(javaMailSender);
    }

    @Test
    void deveLancarExcecaoSeTituloForVazio() {
        var ex = assertThrows(EmailTitleRequiredException.class, () ->
                emailService.sendEmail("destinatario@gmail.com", "", "Mensagem")
        );
        assertEquals("Título do e-mail não pode ser nulo ou vazio.", ex.getMessage());
        verifyNoInteractions(javaMailSender);
    }

    @Test
    void deveLancarExcecaoSeTituloForNulo() {
        var ex = assertThrows(EmailTitleRequiredException.class, () ->
                emailService.sendEmail("destinatario@gmail.com", null, "Mensagem")
        );
        assertEquals("Título do e-mail não pode ser nulo ou vazio.", ex.getMessage());
        verifyNoInteractions(javaMailSender);
    }

    @Test
    void deveLancarExcecaoSeMensagemForVazia() {
        var ex = assertThrows(EmailMessageRequiredException.class, () ->
                emailService.sendEmail("destinatario@gmail.com", "Assunto", "")
        );
        assertEquals("Mensagem do e-mail não pode ser nula ou vazia.", ex.getMessage());
        verifyNoInteractions(javaMailSender);
    }
    @Test
    void deveLancarExcecaoSeMensagemFornula() {
        var ex = assertThrows(EmailMessageRequiredException.class, () ->
                emailService.sendEmail("destinatario@gmail.com", "Assunto", null)
        );
        assertEquals("Mensagem do e-mail não pode ser nula ou vazia.", ex.getMessage());
        verifyNoInteractions(javaMailSender);
    }
}