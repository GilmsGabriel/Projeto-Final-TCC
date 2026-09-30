package br.edu.escola.agendamento.repository;

import br.edu.escola.agendamento.entity.Agendamento;
import br.edu.escola.agendamento.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByUsuarioId(Long usuarioId);

    List<Agendamento> findByStatus(StatusAgendamento status);

    boolean existsByDataEventoAndStatusNot(
            LocalDate dataEvento,
            StatusAgendamento status
    );

    List<Agendamento> findByDataEventoBetweenAndStatusNot(
            LocalDate inicio,
            LocalDate fim,
            StatusAgendamento status
    );

    List<Agendamento> findByStatusAndDataEventoBefore(
            StatusAgendamento status,
            LocalDate data
    );
}
