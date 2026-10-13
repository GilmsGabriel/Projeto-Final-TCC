
package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
                javaMailSender,
                "nao-responder@escola.edu.br"
        );
    }

    @Test
    void deveEnviarEmailConfirmacaoSolicitacao() {
        Usuario usuario = criarUsuario();
        Agendamento agendamento = criarAgendamento(usuario);

        notificationService.enviarEmailConfirmacaoSolicitacao(
                usuario,
                agendamento
        );

        SimpleMailMessage mensagem = capturarMensagem();

        assertEquals(
                "maria@escola.edu.br",
                mensagem.getTo()[0]
        );
        assertEquals(
                "nao-responder@escola.edu.br",
                mensagem.getFrom()
        );
        assertEquals(
                "Solicitação de agendamento recebida",
                mensagem.getSubject()
        );

        assertNotNull(mensagem.getText());
        assertTrue(mensagem.getText().contains("Maria Silva"));
        assertTrue(mensagem.getText().contains("26/09/2026"));
        assertTrue(mensagem.getText().contains("PENDENTE"));
    }

    @Test
    void deveEnviarEmailAprovacao() {
        Usuario usuario = criarUsuario();
        Agendamento agendamento = criarAgendamento(usuario);

        notificationService.enviarEmailAprovacao(
                usuario,
                agendamento
        );

        SimpleMailMessage mensagem = capturarMensagem();

        assertEquals(
                "maria@escola.edu.br",
                mensagem.getTo()[0]
        );
        assertEquals(
                "Agendamento aprovado",
                mensagem.getSubject()
        );

        assertNotNull(mensagem.getText());
        assertTrue(mensagem.getText().contains("Maria Silva"));
        assertTrue(mensagem.getText().contains("26/09/2026"));
        assertTrue(mensagem.getText().contains("APROVADO"));
    }

    @Test
    void deveEnviarEmailRejeicaoComMotivo() {
        Usuario usuario = criarUsuario();
        Agendamento agendamento = criarAgendamento(usuario);

        String motivo = "Conflito com manutenção do espaço.";

        notificationService.enviarEmailRejeicao(
                usuario,
                agendamento,
                motivo
        );

        SimpleMailMessage mensagem = capturarMensagem();

        assertEquals(
                "maria@escola.edu.br",
                mensagem.getTo()[0]
        );
        assertEquals(
                "Agendamento rejeitado",
                mensagem.getSubject()
        );

        assertNotNull(mensagem.getText());
        assertTrue(mensagem.getText().contains("Maria Silva"));
        assertTrue(mensagem.getText().contains("REJEITADO"));
        assertTrue(mensagem.getText().contains(motivo));
    }

    @Test
    void naoDevePropagarFalhaNoEnvioDeEmail() {
        Usuario usuario = criarUsuario();
        Agendamento agendamento = criarAgendamento(usuario);

        doThrow(new MailSendException("SMTP indisponível"))
                .when(javaMailSender)
                .send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                notificationService.enviarEmailConfirmacaoSolicitacao(
                        usuario,
                        agendamento
                )
        );

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    private SimpleMailMessage capturarMensagem() {
        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(javaMailSender).send(captor.capture());

        return captor.getValue();
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();

        usuario.setId(2L);
        usuario.setNomeCompleto("Maria Silva");
        usuario.setEmail("maria@escola.edu.br");

        return usuario;
    }

    private Agendamento criarAgendamento(Usuario usuario) {
        Agendamento agendamento = new Agendamento();

        agendamento.setId(10L);
        agendamento.setUsuario(usuario);
        agendamento.setDataEvento(LocalDate.of(2026, 9, 26));
        agendamento.setDescricaoEvento("Festa comunitária");

        return agendamento;
    }
}
