package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.entity.LogAuditoria;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.repository.LogAuditoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogAuditoriaServiceTest {

    @Mock
    private LogAuditoriaRepository logAuditoriaRepository;

    @InjectMocks
    private LogAuditoriaService logAuditoriaService;

    @Test
    void deveRegistrarEventoComTodosOsDados() {
        Usuario usuario = mock(Usuario.class);

        String acao = "APROVACAO_AGENDAMENTO";
        String entidadeAfetada = "Agendamento#10";
        String detalhes = "Aprovado o agendamento de 2026-09-26.";

        logAuditoriaService.registrarEvento(
                usuario,
                acao,
                entidadeAfetada,
                detalhes
        );

        ArgumentCaptor<LogAuditoria> captor =
                ArgumentCaptor.forClass(LogAuditoria.class);

        verify(logAuditoriaRepository, times(1)).save(captor.capture());

        LogAuditoria logSalvo = captor.getValue();

        assertEquals(usuario, logSalvo.getUsuario());
        assertEquals(acao, logSalvo.getAcao());
        assertEquals(entidadeAfetada, logSalvo.getEntidadeAfetada());
        assertEquals(detalhes, logSalvo.getDetalhes());
    }

    @Test
    void devePermitirRegistrarEventoSemUsuario() {
        String acao = "EXECUCAO_SCHEDULER";
        String entidadeAfetada = "Agendamento";
        String detalhes = "Agendamento expirado automaticamente.";

        logAuditoriaService.registrarEvento(
                null,
                acao,
                entidadeAfetada,
                detalhes
        );

        ArgumentCaptor<LogAuditoria> captor =
                ArgumentCaptor.forClass(LogAuditoria.class);

        verify(logAuditoriaRepository, times(1)).save(captor.capture());

        LogAuditoria logSalvo = captor.getValue();

        assertNull(logSalvo.getUsuario());
        assertEquals(acao, logSalvo.getAcao());
        assertEquals(entidadeAfetada, logSalvo.getEntidadeAfetada());
        assertEquals(detalhes, logSalvo.getDetalhes());
    }
}