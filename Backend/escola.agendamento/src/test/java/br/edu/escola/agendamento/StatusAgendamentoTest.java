package br.edu.escola.agendamento;

import br.edu.escola.agendamento.enums.StatusAgendamento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusAgendamentoTest {

    @Test
    void devePossuirOsStatusEsperados() {
        assertEquals(3, StatusAgendamento.values().length);
        assertEquals(StatusAgendamento.PENDENTE, StatusAgendamento.valueOf("PENDENTE"));
        assertEquals(StatusAgendamento.APROVADO, StatusAgendamento.valueOf("APROVADO"));
        assertEquals(StatusAgendamento.REJEITADO, StatusAgendamento.valueOf("REJEITADO"));
    }
}