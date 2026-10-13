
package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.config.SecurityConfig;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.security.CustomUserDetailsService;
import br.edu.escola.agendamento.security.JwtAuthenticationFilter;
import br.edu.escola.agendamento.security.JwtTokenProvider;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgendamentoController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtTokenProvider.class
})
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgendamentoRepository agendamentoRepository;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void deveListarCalendarioSemExibirAgendamentosRejeitados()
            throws Exception {

        Agendamento pendente = new Agendamento();
        pendente.setId(1L);
        pendente.setDataEvento(LocalDate.of(2026, 9, 26));
        pendente.setStatus(StatusAgendamento.PENDENTE);

        Agendamento aprovado = new Agendamento();
        aprovado.setId(2L);
        aprovado.setDataEvento(LocalDate.of(2026, 9, 27));
        aprovado.setStatus(StatusAgendamento.APROVADO);

        when(agendamentoRepository.findByDataEventoBetweenAndStatusNot(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30),
                StatusAgendamento.REJEITADO
        )).thenReturn(List.of(pendente, aprovado));

        mockMvc.perform(get("/api/agendamentos/calendario")
                        .param("mes", "9")
                        .param("ano", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem")
                        .value("Calendário consultado com sucesso."))
                .andExpect(jsonPath("$.dados.length()").value(2))
                .andExpect(jsonPath("$.dados[0].data")
                        .value("2026-09-26"))
                .andExpect(jsonPath("$.dados[0].status")
                        .value("PENDENTE"))
                .andExpect(jsonPath("$.dados[1].data")
                        .value("2026-09-27"))
                .andExpect(jsonPath("$.dados[1].status")
                        .value("APROVADO"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(agendamentoRepository)
                .findByDataEventoBetweenAndStatusNot(
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 30),
                        StatusAgendamento.REJEITADO
                );
    }
}
