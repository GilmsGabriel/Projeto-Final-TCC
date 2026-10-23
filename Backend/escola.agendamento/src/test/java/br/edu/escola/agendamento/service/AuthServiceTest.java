package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.dto.RegistroRequestDTO;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.PerfilUsuario;
import br.edu.escola.agendamento.exception.RegraNegocioException;
import br.edu.escola.agendamento.repository.UsuarioRepository;
import br.edu.escola.agendamento.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.edu.escola.agendamento.dto.LoginRequestDTO;
import br.edu.escola.agendamento.dto.TokenResponseDTO;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import br.edu.escola.agendamento.service.LogAuditoriaService;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private LogAuditoriaService logAuditoriaService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        authService = new AuthService(
                usuarioRepository,
                passwordEncoder,
                authenticationManager,
                jwtTokenProvider,
                logAuditoriaService
        );
    }

    @Test
    void deveRegistrarUsuarioComSenhaCriptografada() {
        RegistroRequestDTO dto = criarRegistroDTO(
                "Maria Silva",
                "maria@escola.edu.br",
                "SenhaForte123",
                "SenhaForte123"
        );

        when(usuarioRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    usuario.setId(1L);
                    usuario.setDataCriacao(
                            LocalDateTime.of(2026, 10, 13, 10, 15, 30)
                    );
                    return usuario;
                });

        UsuarioResponseDTO response = authService.registrar(dto);

        verify(logAuditoriaService).registrarEvento(
                org.mockito.ArgumentMatchers.any(Usuario.class),
                org.mockito.ArgumentMatchers.eq("REGISTRO_USUARIO"),
                org.mockito.ArgumentMatchers.eq("Usuario#1"),
                org.mockito.ArgumentMatchers.contains("maria@escola.edu.br")
        );

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();

        assertEquals("Maria Silva", usuarioSalvo.getNomeCompleto());
        assertEquals("maria@escola.edu.br", usuarioSalvo.getEmail());
        assertEquals(PerfilUsuario.COMUM, usuarioSalvo.getPerfil());

        assertNotEquals(
                "SenhaForte123",
                usuarioSalvo.getSenha()
        );

        assertTrue(
                passwordEncoder.matches(
                        "SenhaForte123",
                        usuarioSalvo.getSenha()
                )
        );

        assertEquals(1L, response.getId());
        assertEquals("Maria Silva", response.getNomeCompleto());
        assertEquals("maria@escola.edu.br", response.getEmail());
        assertEquals(PerfilUsuario.COMUM, response.getPerfil());
        assertEquals(
                LocalDateTime.of(2026, 10, 13, 10, 15, 30),
                response.getDataCriacao()
        );
    }

    @Test
    void deveRejeitarRegistroComEmailDuplicado() {
        RegistroRequestDTO dto = criarRegistroDTO(
                "Maria Silva",
                "maria@escola.edu.br",
                "SenhaForte123",
                "SenhaForte123"
        );

        when(usuarioRepository.existsByEmail(dto.getEmail()))
                .thenReturn(true);

        assertThrows(
                RegraNegocioException.class,
                () -> authService.registrar(dto)
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveRejeitarRegistroComSenhasDiferentes() {
        RegistroRequestDTO dto = criarRegistroDTO(
                "Maria Silva",
                "maria@escola.edu.br",
                "SenhaForte123",
                "SenhaDiferente456"
        );

        assertThrows(
                RegraNegocioException.class,
                () -> authService.registrar(dto)
        );

        verify(usuarioRepository, never()).existsByEmail(any(String.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    private RegistroRequestDTO criarRegistroDTO(
            String nomeCompleto,
            String email,
            String senha,
            String confirmarSenha
    ) {
        RegistroRequestDTO dto = new RegistroRequestDTO();

        dto.setNomeCompleto(nomeCompleto);
        dto.setEmail(email);
        dto.setSenha(senha);
        dto.setConfirmarSenha(confirmarSenha);

        return dto;
    }

    @Test
    void deveAutenticarComSucessoERetornarToken() {
        // Arrange
        LoginRequestDTO loginDTO = new LoginRequestDTO("maria@escola.edu.br", "SenhaForte123");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Maria Silva");
        usuario.setEmail("maria@escola.edu.br");
        usuario.setPerfil(PerfilUsuario.COMUM);
        usuario.setAtivo(true);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getSenha()
        );

        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuario));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.gerarToken(authentication)).thenReturn("token.jwt.mockado");

        // Act
        TokenResponseDTO response = authService.autenticar(loginDTO);

        // Assert
        assertEquals("token.jwt.mockado", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(3600L, response.getExpiraEm());
        assertEquals(1L, response.getUsuario().getId());
        assertEquals("Maria Silva", response.getUsuario().getNomeCompleto());
        assertEquals("maria@escola.edu.br", response.getUsuario().getEmail());
        assertEquals(PerfilUsuario.COMUM, response.getUsuario().getPerfil());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        // Arrange
        LoginRequestDTO loginDTO = new LoginRequestDTO("inexistente@escola.edu.br", "senha123");

        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.autenticar(loginDTO));
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtTokenProvider, never()).gerarToken(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioEstiverInativo() {
        // Arrange
        LoginRequestDTO loginDTO = new LoginRequestDTO("maria@escola.edu.br", "SenhaForte123");

        Usuario usuario = new Usuario();
        usuario.setEmail("maria@escola.edu.br");
        usuario.setAtivo(false); // Usuário inativo

        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThrows(DisabledException.class, () -> authService.autenticar(loginDTO));
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtTokenProvider, never()).gerarToken(any());
    }

    @Test
    void deveLancarExcecaoQuandoSenhaEstiverIncorreta() {
        // Arrange
        LoginRequestDTO loginDTO = new LoginRequestDTO("maria@escola.edu.br", "SenhaErrada");

        Usuario usuario = new Usuario();
        usuario.setEmail("maria@escola.edu.br");
        usuario.setAtivo(true);

        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuario));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.autenticar(loginDTO));
        verify(jwtTokenProvider, never()).gerarToken(any());
    }
}