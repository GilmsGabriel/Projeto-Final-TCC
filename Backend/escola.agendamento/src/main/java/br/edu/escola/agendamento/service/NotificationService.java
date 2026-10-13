package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationService.class);

    private static final DateTimeFormatter FORMATADOR_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender javaMailSender;
    private final String remetente;

    public NotificationService(
            JavaMailSender javaMailSender,
            @Value("${spring.mail.username:nao-responder@escola.edu.br}")
            String remetente
    ) {
        this.javaMailSender = javaMailSender;
        this.remetente = remetente;
    }

    @Async
    public void enviarEmailConfirmacaoSolicitacao(
            Usuario usuario,
            Agendamento agendamento
    ) {
        try {
            String assunto = "Solicitação de agendamento recebida";

            String corpo = """
                    Olá, %s!

                    Sua solicitação de agendamento foi recebida com sucesso.

                    Número da solicitação: %d
                    Data do evento: %s
                    Descrição: %s
                    Status: PENDENTE

                    Você receberá outra notificação quando sua solicitação
                    for aprovada ou rejeitada.

                    Atenciosamente,
                    Sistema de Agendamento Escolar
                    """.formatted(
                    usuario.getNomeCompleto(),
                    agendamento.getId(),
                    formatarData(agendamento),
                    obterDescricao(agendamento)
            );

            enviarEmailSeguro(
                    usuario.getEmail(),
                    assunto,
                    corpo,
                    agendamento.getId()
            );

        } catch (Exception exception) {
            logger.error(
                    "Falha ao preparar o e-mail de confirmação do agendamento {}.",
                    obterIdAgendamento(agendamento),
                    exception
            );
        }
    }

    @Async
    public void enviarEmailAprovacao(
            Usuario usuario,
            Agendamento agendamento
    ) {
        try {
            String assunto = "Agendamento aprovado";

            String corpo = """
                    Olá, %s!

                    Seu agendamento foi aprovado.

                    Número da solicitação: %d
                    Data do evento: %s
                    Descrição: %s
                    Status: APROVADO

                    Atenciosamente,
                    Sistema de Agendamento Escolar
                    """.formatted(
                    usuario.getNomeCompleto(),
                    agendamento.getId(),
                    formatarData(agendamento),
                    obterDescricao(agendamento)
            );

            enviarEmailSeguro(
                    usuario.getEmail(),
                    assunto,
                    corpo,
                    agendamento.getId()
            );

        } catch (Exception exception) {
            logger.error(
                    "Falha ao preparar o e-mail de aprovação do agendamento {}.",
                    obterIdAgendamento(agendamento),
                    exception
            );
        }
    }

    @Async
    public void enviarEmailRejeicao(
            Usuario usuario,
            Agendamento agendamento,
            String motivo
    ) {
        try {
            String assunto = "Agendamento rejeitado";

            String corpo = """
                    Olá, %s!

                    Infelizmente, seu agendamento foi rejeitado.

                    Número da solicitação: %d
                    Data do evento: %s
                    Descrição: %s
                    Status: REJEITADO

                    Motivo da rejeição:
                    %s

                    Atenciosamente,
                    Sistema de Agendamento Escolar
                    """.formatted(
                    usuario.getNomeCompleto(),
                    agendamento.getId(),
                    formatarData(agendamento),
                    obterDescricao(agendamento),
                    motivo
            );

            enviarEmailSeguro(
                    usuario.getEmail(),
                    assunto,
                    corpo,
                    agendamento.getId()
            );

        } catch (Exception exception) {
            logger.error(
                    "Falha ao preparar o e-mail de rejeição do agendamento {}.",
                    obterIdAgendamento(agendamento),
                    exception
            );
        }
    }

    private void enviarEmailSeguro(
            String destinatario,
            String assunto,
            String corpo,
            Long agendamentoId
    ) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();

            mensagem.setFrom(remetente);
            mensagem.setTo(destinatario);
            mensagem.setSubject(assunto);
            mensagem.setText(corpo);

            javaMailSender.send(mensagem);

        } catch (Exception exception) {
            logger.error(
                    "Não foi possível enviar o e-mail do agendamento {}.",
                    agendamentoId,
                    exception
            );
        }
    }

    private String formatarData(Agendamento agendamento) {
        if (agendamento.getDataEvento() == null) {
            return "Não informada";
        }

        return agendamento.getDataEvento().format(FORMATADOR_DATA);
    }

    private String obterDescricao(Agendamento agendamento) {
        if (agendamento.getDescricaoEvento() == null
                || agendamento.getDescricaoEvento().isBlank()) {
            return "Não informada";
        }

        return agendamento.getDescricaoEvento();
    }

    private Long obterIdAgendamento(Agendamento agendamento) {
        return agendamento == null ? null : agendamento.getId();
    }
}