package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.PerfilUsuario;
import br.edu.escola.agendamento.exception.RecursoNaoEncontradoException;
import br.edu.escola.agendamento.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    void deveBuscarPerfilDoUsuarioLogado() {
        String email = "maria@escola.edu.br";
        LocalDateTime dataCriacao =
                LocalDateTime.of(2026, 9, 19, 10, 15, 30);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Maria Silva");
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.COMUM);
        usuario.setDataCriacao(dataCriacao);

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response =
                usuarioService.buscarPerfilLogado(email);

        verify(usuarioRepository).findByEmail(email);

        assertEquals(1L, response.getId());
        assertEquals("Maria Silva", response.getNomeCompleto());
        assertEquals(email, response.getEmail());
        assertEquals(PerfilUsuario.COMUM, response.getPerfil());
        assertEquals(dataCriacao, response.getDataCriacao());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
        String email = "inexistente@escola.edu.br";

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        RecursoNaoEncontradoException exception =
                assertThrows(
                        RecursoNaoEncontradoException.class,
                        () -> usuarioService.buscarPerfilLogado(email)
                );

        assertEquals(
                "USUARIO_NAO_ENCONTRADO",
                exception.getCodigoErro()
        );

        verify(usuarioRepository).findByEmail(email);
    }
}