package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.config.SecurityConfig;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.security.CustomUserDetailsService;
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

import java.time.LocalDate;
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
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void administradorDeveReceberSomenteAgendamentosPendentes()
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
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Agendamentos pendentes consultados com sucesso."
                        ))
                .andExpect(jsonPath("$.dados.length()").value(1))
                .andExpect(jsonPath("$.dados[0].id").value(1))
                .andExpect(jsonPath("$.dados[0].usuario.id").value(10))
                .andExpect(jsonPath("$.dados[0].usuario.nomeCompleto")
                        .value("Maria Silva"))
                .andExpect(jsonPath("$.dados[0].status")
                        .value("PENDENTE"))
                .andExpect(jsonPath("$.timestamp").exists());

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