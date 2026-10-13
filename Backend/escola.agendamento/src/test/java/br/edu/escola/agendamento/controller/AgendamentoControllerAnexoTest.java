
package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.config.SecurityConfig;
import br.edu.escola.agendamento.dto.AgendamentoResponseDTO;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.PerfilUsuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.exception.GlobalExceptionHandler;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.security.CustomUserDetailsService;
import br.edu.escola.agendamento.security.JwtTokenProvider;
import br.edu.escola.agendamento.service.AgendamentoService;
import br.edu.escola.agendamento.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgendamentoController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class AgendamentoControllerAnexoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgendamentoRepository agendamentoRepository;

    @MockitoBean
    private AgendamentoService agendamentoService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "maria@escola.edu.br")
    void devePermitirUploadPeloDonoDoAgendamento() throws Exception {

        Long agendamentoId = 10L;
        String email = "maria@escola.edu.br";

        Usuario dono = new Usuario();
        dono.setId(1L);
        dono.setNomeCompleto("Maria Silva");
        dono.setEmail(email);

        Agendamento agendamento = new Agendamento();
        agendamento.setId(agendamentoId);
        agendamento.setUsuario(dono);
        agendamento.setDataEvento(LocalDate.of(2026, 10, 17));
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        UsuarioResponseDTO usuarioLogado = new UsuarioResponseDTO();
        usuarioLogado.setId(1L);
        usuarioLogado.setNomeCompleto("Maria Silva");
        usuarioLogado.setEmail(email);
        usuarioLogado.setPerfil(PerfilUsuario.COMUM);

        AgendamentoResponseDTO resultado = new AgendamentoResponseDTO();
        resultado.setId(agendamentoId);
        resultado.setAnexoUrl(
                "/arquivos/agendamentos/10/planta.pdf"
        );
        resultado.setStatus(StatusAgendamento.PENDENTE);

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "planta.pdf",
                "application/pdf",
                "conteudo de teste do pdf".getBytes()
        );

        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamento));

        when(usuarioService.buscarPerfilLogado(email))
                .thenReturn(usuarioLogado);

        when(agendamentoService.anexarArquivo(
                eq(agendamentoId),
                any(MultipartFile.class)
        )).thenReturn(resultado);

        mockMvc.perform(
                        multipart(
                                "/api/agendamentos/{id}/anexo",
                                agendamentoId
                        )
                                .file(arquivo)
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem")
                        .value("Arquivo anexado com sucesso."))
                .andExpect(jsonPath("$.dados.id").value(10))
                .andExpect(jsonPath("$.dados.anexoUrl")
                        .value("/arquivos/agendamentos/10/planta.pdf"))
                .andExpect(jsonPath("$.dados.status").value("PENDENTE"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(agendamentoRepository).findById(agendamentoId);
        verify(usuarioService).buscarPerfilLogado(email);
        verify(agendamentoService).anexarArquivo(
                eq(agendamentoId),
                any(MultipartFile.class)
        );
    }

    @Test
    @WithMockUser(username = "joao@escola.edu.br")
    void deveBloquearUploadPorOutroUsuarioComum() throws Exception {

        Long agendamentoId = 10L;
        String emailOutroUsuario = "joao@escola.edu.br";

        Usuario dono = new Usuario();
        dono.setId(1L);
        dono.setNomeCompleto("Maria Silva");
        dono.setEmail("maria@escola.edu.br");

        Agendamento agendamento = new Agendamento();
        agendamento.setId(agendamentoId);
        agendamento.setUsuario(dono);
        agendamento.setDataEvento(LocalDate.of(2026, 10, 17));
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        UsuarioResponseDTO outroUsuario = new UsuarioResponseDTO();
        outroUsuario.setId(2L);
        outroUsuario.setNomeCompleto("João Santos");
        outroUsuario.setEmail(emailOutroUsuario);
        outroUsuario.setPerfil(PerfilUsuario.COMUM);

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "planta.pdf",
                "application/pdf",
                "conteudo de teste do pdf".getBytes()
        );

        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamento));

        when(usuarioService.buscarPerfilLogado(emailOutroUsuario))
                .thenReturn(outroUsuario);

        mockMvc.perform(
                        multipart(
                                "/api/agendamentos/{id}/anexo",
                                agendamentoId
                        )
                                .file(arquivo)
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.codigoErro")
                        .value("ACESSO_NEGADO"));

        verify(agendamentoRepository).findById(agendamentoId);
        verify(usuarioService).buscarPerfilLogado(emailOutroUsuario);

        verify(agendamentoService, never()).anexarArquivo(
                eq(agendamentoId),
                any(MultipartFile.class)
        );
    }
}
