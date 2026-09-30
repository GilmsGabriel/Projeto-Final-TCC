package br.edu.escola.agendamento.repository;

import br.edu.escola.agendamento.entity.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogAuditoriaRepository
        extends JpaRepository<LogAuditoria, Long> {

    List<LogAuditoria> findAllByOrderByDataHoraDesc();
}
