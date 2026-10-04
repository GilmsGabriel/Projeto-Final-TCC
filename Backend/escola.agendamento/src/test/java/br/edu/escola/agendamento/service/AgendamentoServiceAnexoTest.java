package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.dto.AgendamentoResponseDTO;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceAnexoTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Test
    void deveAnexarArquivoEAtualizarAnexoUrl() {

        Usuario usuario = mock(Usuario.class);

        when(usuario.getId()).thenReturn(2L);
        when(usuario.getNomeCompleto())
                .thenReturn("Admin Geral");
        when(usuario.getEmail())
                .thenReturn("admin@escola.edu.br");

        Agendamento agendamento =
                new Agendamento();

        agendamento.setId(10L);
        agendamento.setUsuario(usuario);
        agendamento.setDataEvento(
                LocalDate.of(2026, 9, 26)
        );
        agendamento.setDescricaoEvento(
                "Evento escolar"
        );
        agendamento.setStatus(
                StatusAgendamento.PENDENTE
        );

        MockMultipartFile arquivo =
                new MockMultipartFile(
                        "arquivo",
                        "planta.pdf",
                        "application/pdf",
                        "conteudo".getBytes()
                );

        when(agendamentoRepository.findById(10L))
                .thenReturn(Optional.of(agendamento));

        when(fileStorageService.armazenarArquivo(
                arquivo,
                10L
        )).thenReturn(
                "/arquivos/agendamentos/10/planta.pdf"
        );

        when(agendamentoRepository.save(agendamento))
                .thenReturn(agendamento);

        AgendamentoResponseDTO response =
                agendamentoService.anexarArquivo(
                        10L,
                        arquivo
                );

        verify(fileStorageService)
                .validarArquivo(arquivo);

        verify(fileStorageService)
                .armazenarArquivo(
                        arquivo,
                        10L
                );

        ArgumentCaptor<Agendamento> captor =
                ArgumentCaptor.forClass(Agendamento.class);

        verify(agendamentoRepository)
                .save(captor.capture());

        assertEquals(
                "/arquivos/agendamentos/10/planta.pdf",
                captor.getValue().getAnexoUrl()
        );

        assertEquals(
                "/arquivos/agendamentos/10/planta.pdf",
                response.getAnexoUrl()
        );
    }
}