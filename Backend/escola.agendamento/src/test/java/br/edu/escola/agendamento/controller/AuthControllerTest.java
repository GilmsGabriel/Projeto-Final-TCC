package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.config.SecurityConfig;
import br.edu.escola.agendamento.dto.LoginRequestDTO;
import br.edu.escola.agendamento.dto.RegistroRequestDTO;
import br.edu.escola.agendamento.dto.TokenResponseDTO;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.enums.PerfilUsuario;
import br.edu.escola.agendamento.exception.GlobalExceptionHandler;
import br.edu.escola.agendamento.security.CustomUserDetailsService;
import br.edu.escola.agendamento.security.JwtTokenProvider;
import br.edu.escola.agendamento.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    // ==========================================
    // TESTES DE REGISTRO
    // ==========================================

    @Test
    void deveRegistrarUsuarioERetornar201() throws Exception {

        UsuarioResponseDTO usuario = new UsuarioResponseDTO();
        usuario.setId(1L);
        usuario.setNomeCompleto("Maria Silva");
        usuario.setEmail("maria@escola.edu.br");
        usuario.setPerfil(PerfilUsuario.COMUM);
        usuario.setDataCriacao(LocalDateTime.of(2026, 9, 19, 10, 15, 30));

        when(authService.registrar(any(RegistroRequestDTO.class)))
                .thenReturn(usuario);

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva",
                                    "email": "maria@escola.edu.br",
                                    "senha": "SenhaForte123",
                                    "confirmarSenha": "SenhaForte123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Usuário registrado com sucesso."))
                .andExpect(jsonPath("$.dados.id").value(1))
                .andExpect(jsonPath("$.dados.nomeCompleto").value("Maria Silva"))
                .andExpect(jsonPath("$.dados.email").value("maria@escola.edu.br"))
                .andExpect(jsonPath("$.dados.perfil").value("COMUM"))
                .andExpect(jsonPath("$.dados.dataCriacao").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(authService).registrar(any(RegistroRequestDTO.class));
    }

    @Test
    void deveRetornar422QuandoEmailForInvalido() throws Exception {

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeCompleto": "Maria Silva",
                                    "email": "email-invalido",
                                    "senha": "SenhaForte123",
                                    "confirmarSenha": "SenhaForte123"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação."))
                .andExpect(jsonPath("$.codigoErro").value("VALIDACAO_ERRO"))
                .andExpect(jsonPath("$.detalhes[0]").value("email: Informe um endereço de e-mail válido."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(authService, never()).registrar(any(RegistroRequestDTO.class));
    }

    @Test
    void deveAutenticarUsuarioERetornar200ComToken() throws Exception {
        TokenResponseDTO tokenResponse = TokenResponseDTO.builder()
                .token("token.jwt.falso")
                .build();

        // Mantemos os mocks originais
        when(authService.autenticar(any(LoginRequestDTO.class))).thenReturn(tokenResponse);
        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(jwtTokenProvider.gerarToken(any())).thenReturn("token.jwt.falso");

        mockMvc.perform(post("/api/auth/login")
                        .with(user("teste").roles("USER")) // Diz ao Spring Security para não bloquear a rota
                        .with(csrf()) // Evita bloqueios de formulário
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "teste@escola.com",
                                    "senha": "senha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Login realizado com sucesso."))
                .andExpect(jsonPath("$.dados").exists());
    }

    @Test
    void deveRetornar401ComJsonDeErroQuandoCredenciaisForemInvalidas() throws Exception {
        when(authService.autenticar(any(LoginRequestDTO.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas."));

        mockMvc.perform(post("/api/auth/login")
                        .with(user("teste").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "teste@escola.com",
                                    "senha": "senhaErrada"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem")
                        .value("Falha na autenticação: E-mail ou senha incorretos."))
                .andExpect(jsonPath("$.codigoErro").value("ERRO_CREDENCIAIS"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornar403ComJsonDeErroQuandoUsuarioEstiverInativo() throws Exception {
        when(authService.autenticar(any(LoginRequestDTO.class)))
                .thenThrow(new org.springframework.security.authentication.DisabledException(
                        "Usuário inativo não pode autenticar."
                ));

        mockMvc.perform(post("/api/auth/login")
                        .with(user("teste").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "inativo@escola.com",
                                    "senha": "SenhaForte123"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem")
                        .value("Usuário inativo não pode autenticar."))
                .andExpect(jsonPath("$.codigoErro").value("USUARIO_INATIVO"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
