package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.dto.AgendamentoRequestDTO;
import br.edu.escola.agendamento.dto.AgendamentoResponseDTO;
import br.edu.escola.agendamento.dto.UsuarioResumoDTO;
import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import br.edu.escola.agendamento.exception.RecursoNaoEncontradoException;
import br.edu.escola.agendamento.exception.RegraNegocioException;
import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            UsuarioRepository usuarioRepository,
            FileStorageService fileStorageService
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public synchronized AgendamentoResponseDTO solicitarAgendamento(
            Long usuarioId,
            AgendamentoRequestDTO dto
    ) {
        validarDiaPermitido(dto.getDataEvento());

        validarDisponibilidadeData(dto.getDataEvento());

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado.",
                        "USUARIO_NAO_ENCONTRADO"
                ));

        Agendamento agendamento = new Agendamento();

        agendamento.setUsuario(usuario);
        agendamento.setDataEvento(dto.getDataEvento());
        agendamento.setDescricaoEvento(dto.getDescricaoEvento());
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        Agendamento salvo =
                agendamentoRepository.save(agendamento);

        return converterParaResponseDTO(salvo);
    }

    @Transactional
    public AgendamentoResponseDTO anexarArquivo(
            Long agendamentoId,
            MultipartFile arquivo
    ) {
        Agendamento agendamento =
                agendamentoRepository.findById(agendamentoId)
                        .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Agendamento não encontrado.",
                                "AGENDAMENTO_NAO_ENCONTRADO"
                        ));

        fileStorageService.validarArquivo(arquivo);

        String anexoUrl =
                fileStorageService.armazenarArquivo(
                        arquivo,
                        agendamentoId
                );

        agendamento.setAnexoUrl(anexoUrl);

        Agendamento atualizado =
                agendamentoRepository.save(agendamento);

        return converterParaResponseDTO(atualizado);
    }

    private void validarDiaPermitido(LocalDate data) {

        DayOfWeek diaSemana = data.getDayOfWeek();

        if (diaSemana != DayOfWeek.SATURDAY
                && diaSemana != DayOfWeek.SUNDAY) {

            throw new RegraNegocioException(
                    "Não é possível agendar para este dia da semana.",
                    "REGRA_NEGOCIO_DIA_INVALIDO",
                    "Agendamentos são permitidos apenas aos sábados e domingos."
            );
        }
    }

    private void validarDisponibilidadeData(LocalDate data) {

        boolean existeAgendamento =
                agendamentoRepository.existsByDataEventoAndStatusNot(
                        data,
                        StatusAgendamento.REJEITADO
                );

        if (existeAgendamento) {
            throw new RegraNegocioException(
                    "Não é possível agendar para esta data.",
                    "REGRA_NEGOCIO_DATA_INDISPONIVEL",
                    "Já existe um agendamento para esta data."
            );
        }
    }

    private AgendamentoResponseDTO converterParaResponseDTO(
            Agendamento agendamento
    ) {
        AgendamentoResponseDTO response =
                new AgendamentoResponseDTO();

        response.setId(agendamento.getId());

        Usuario usuario = agendamento.getUsuario();

        response.setUsuario(
                new UsuarioResumoDTO(
                        usuario.getId(),
                        usuario.getNomeCompleto(),
                        usuario.getEmail()
                )
        );

        response.setDataEvento(
                agendamento.getDataEvento()
        );

        if (agendamento.getDataEvento() != null) {
            response.setDiaSemana(
                    agendamento.getDataEvento()
                            .getDayOfWeek()
                            .toString()
            );
        }

        response.setDescricaoEvento(
                agendamento.getDescricaoEvento()
        );

        response.setAnexoUrl(
                agendamento.getAnexoUrl()
        );

        response.setStatus(
                agendamento.getStatus()
        );

        response.setJustificativaRejeicao(
                agendamento.getJustificativaRejeicao()
        );

        response.setDataCriacao(
                agendamento.getDataCriacao()
        );

        response.setDataAtualizacao(
                agendamento.getDataAtualizacao()
        );

        return response;
    }
}