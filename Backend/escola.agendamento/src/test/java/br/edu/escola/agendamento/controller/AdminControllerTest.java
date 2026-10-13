package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.config.SecurityConfig;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.LogAuditoria;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.repository.LogAuditoriaRepository;
import br.edu.escola.agendamento.security.CustomUserDetailsService;
import br.edu.escola.agendamento.security.JwtAuthenticationFilter;
import br.edu.escola.agendamento.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.escola.agendamento.service.AgendamentoService;
import br.edu.escola.agendamento.service.UsuarioService;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtTokenProvider.class
})
@TestPropertySource(properties = {
        "spring.jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
        "spring.jwt.expiration-milliseconds=3600000"
})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AgendamentoRepository agendamentoRepository;

    @MockitoBean
    private LogAuditoriaRepository logAuditoriaRepository;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AgendamentoService agendamentoService;

    @MockitoBean
    private UsuarioService usuarioService;


    @Test
    void administradorDeveReceberAgendamentosPendentes()
            throws Exception {

        String emailAdmin = "admin@escola.edu.br";

        UserDetails administrador = User
                .withUsername(emailAdmin)
                .password("senha")
                .authorities("ROLE_ADMINISTRADOR")
                .build();

        when(customUserDetailsService.loadUserByUsername(emailAdmin))
                .thenReturn(administrador);

        Usuario solicitante = new Usuario();
        solicitante.setId(10L);
        solicitante.setNomeCompleto("Maria Silva");
        solicitante.setEmail("maria@escola.edu.br");

        Agendamento pendente = new Agendamento();
        pendente.setId(1L);
        pendente.setUsuario(solicitante);
        pendente.setDataEvento(LocalDate.of(2026, 10, 20));
        pendente.setDescricaoEvento("Reunião pedagógica");
        pendente.setStatus(StatusAgendamento.PENDENTE);

        when(agendamentoRepository.findByStatus(
                StatusAgendamento.PENDENTE
        )).thenReturn(List.of(pendente));

        String token = gerarToken(administrador);

        mockMvc.perform(get("/api/admin/agendamentos/pendentes")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + token
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.dados.length()").value(1))
                .andExpect(jsonPath("$.dados[0].id").value(1))
                .andExpect(jsonPath("$.dados[0].status")
                        .value("PENDENTE"));

        verify(agendamentoRepository)
                .findByStatus(StatusAgendamento.PENDENTE);
    }

    @Test
    void usuarioComumDeveReceber403AoConsultarPendentes()
            throws Exception {

        String emailUsuario = "maria@escola.edu.br";

        UserDetails usuarioComum = User
                .withUsername(emailUsuario)
                .password("senha")
                .authorities("ROLE_COMUM")
                .build();

        when(customUserDetailsService.loadUserByUsername(emailUsuario))
                .thenReturn(usuarioComum);

        String token = gerarToken(usuarioComum);

        mockMvc.perform(get("/api/admin/agendamentos/pendentes")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + token
                        ))
                .andExpect(status().isForbidden());

        verify(agendamentoRepository, never())
                .findByStatus(StatusAgendamento.PENDENTE);
    }

    @Test
    void administradorDeveReceberLogsEmOrdemDecrescente()
            throws Exception {

        String emailAdmin = "admin@escola.edu.br";

        UserDetails administrador = User
                .withUsername(emailAdmin)
                .password("senha")
                .authorities("ROLE_ADMINISTRADOR")
                .build();

        when(customUserDetailsService.loadUserByUsername(emailAdmin))
                .thenReturn(administrador);

        Usuario usuarioAuditado = new Usuario();
        usuarioAuditado.setId(2L);
        usuarioAuditado.setNomeCompleto("Admin Geral");
        usuarioAuditado.setEmail(emailAdmin);

        LogAuditoria logRecente = new LogAuditoria();
        logRecente.setId(55L);
        logRecente.setUsuario(usuarioAuditado);
        logRecente.setAcao("APROVACAO_AGENDAMENTO");
        logRecente.setEntidadeAfetada("Agendamento#10");
        logRecente.setDetalhes(
                "Aprovado o agendamento de 2026-09-26."
        );
        logRecente.setDataHora(
                LocalDateTime.of(2026, 9, 19, 12, 0)
        );

        LogAuditoria logAntigo = new LogAuditoria();
        logAntigo.setId(54L);
        logAntigo.setUsuario(usuarioAuditado);
        logAntigo.setAcao("CRIACAO_AGENDAMENTO");
        logAntigo.setEntidadeAfetada("Agendamento#9");
        logAntigo.setDetalhes("Agendamento criado.");
        logAntigo.setDataHora(
                LocalDateTime.of(2026, 9, 18, 10, 30)
        );

        when(logAuditoriaRepository.findAllByOrderByDataHoraDesc())
                .thenReturn(List.of(logRecente, logAntigo));

        String token = gerarToken(administrador);

        mockMvc.perform(get("/api/admin/logs")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + token
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.dados.length()").value(2))

                // O log mais recente deve aparecer primeiro.
                .andExpect(jsonPath("$.dados[0].id").value(55))
                .andExpect(jsonPath("$.dados[0].acao")
                        .value("APROVACAO_AGENDAMENTO"))
                .andExpect(jsonPath("$.dados[0].entidadeAfetada")
                        .value("Agendamento#10"))
                .andExpect(jsonPath("$.dados[0].dataHora")
                        .value("2026-09-19T12:00:00"))
                .andExpect(jsonPath("$.dados[0].usuario.nomeCompleto")
                        .value("Admin Geral"))

                // O log mais antigo deve aparecer depois.
                .andExpect(jsonPath("$.dados[1].id").value(54))
                .andExpect(jsonPath("$.dados[1].dataHora")
                        .value("2026-09-18T10:30:00"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(logAuditoriaRepository)
                .findAllByOrderByDataHoraDesc();
    }

    @Test
    void usuarioComumDeveReceber403AoConsultarLogs()
            throws Exception {

        String emailUsuario = "maria@escola.edu.br";

        UserDetails usuarioComum = User
                .withUsername(emailUsuario)
                .password("senha")
                .authorities("ROLE_COMUM")
                .build();

        when(customUserDetailsService.loadUserByUsername(emailUsuario))
                .thenReturn(usuarioComum);

        String token = gerarToken(usuarioComum);

        mockMvc.perform(get("/api/admin/logs")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + token
                        ))
                .andExpect(status().isForbidden());

        verify(logAuditoriaRepository, never())
                .findAllByOrderByDataHoraDesc();
    }

    private String gerarToken(UserDetails usuario) {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        usuario.getAuthorities()
                );

        return jwtTokenProvider.gerarToken(authentication);
    }
}