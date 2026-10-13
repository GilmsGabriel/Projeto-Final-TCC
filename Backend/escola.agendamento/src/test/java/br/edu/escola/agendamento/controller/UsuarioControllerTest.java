
package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.config.SecurityConfig;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.enums.PerfilUsuario;
import br.edu.escola.agendamento.security.CustomUserDetailsService;
import br.edu.escola.agendamento.security.JwtTokenProvider;
import br.edu.escola.agendamento.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import({
        SecurityConfig.class,
        JwtTokenProvider.class
})
@TestPropertySource(properties = {
        "spring.jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
        "spring.jwt.expiration-milliseconds=3600000"
})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void deveRetornar401QuandoNaoHouverToken() throws Exception {

        mockMvc.perform(get("/api/usuarios/me"))
                .andExpect(status().isUnauthorized());

        verify(usuarioService, never())
                .buscarPerfilLogado(anyString());
    }

    @Test
    void deveRetornarPerfilQuandoTokenForValido() throws Exception {

        String email = "maria@escola.edu.br";

        UserDetails userDetails = User
                .withUsername(email)
                .password("senha-criptografada")
                .authorities("ROLE_COMUM")
                .build();

        when(customUserDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        String token = jwtTokenProvider.gerarToken(authentication);

        UsuarioResponseDTO perfil = new UsuarioResponseDTO();

        perfil.setId(1L);
        perfil.setNomeCompleto("Maria Silva");
        perfil.setEmail(email);
        perfil.setPerfil(PerfilUsuario.COMUM);
        perfil.setDataCriacao(
                LocalDateTime.of(2026, 9, 19, 10, 15, 30)
        );

        when(usuarioService.buscarPerfilLogado(email))
                .thenReturn(perfil);

        mockMvc.perform(get("/api/usuarios/me")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + token
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem")
                        .value("Perfil do usuário recuperado com sucesso."))
                .andExpect(jsonPath("$.dados.id").value(1))
                .andExpect(jsonPath("$.dados.nomeCompleto")
                        .value("Maria Silva"))
                .andExpect(jsonPath("$.dados.email").value(email))
                .andExpect(jsonPath("$.dados.perfil").value("COMUM"))
                .andExpect(jsonPath("$.dados.dataCriacao").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(customUserDetailsService)
                .loadUserByUsername(email);

        verify(usuarioService)
                .buscarPerfilLogado(email);
    }
}
