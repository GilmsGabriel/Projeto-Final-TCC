package br.edu.escola.agendamento;

import br.edu.escola.agendamento.repository.AgendamentoRepository;
import br.edu.escola.agendamento.repository.LogAuditoriaRepository;
import br.edu.escola.agendamento.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
/* TODO: [SPRINT 1 - TASK 1] A auto-configuração do DataSource foi excluída temporariamente
 pois o banco PostgreSQL ainda não está provisionado/conectado nesta etapa inicial.
 TODO: REMOVER ESTA EXCLUSÃO assim que a task de configuração do banco for iniciada. */
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class ApplicationTests {

	@MockitoBean
	private AgendamentoRepository agendamentoRepository;

	@MockitoBean
	private UsuarioRepository usuarioRepository;

	@MockitoBean
	private LogAuditoriaRepository logAuditoriaRepository;

	@Test
	void contextLoads() {
	}

}
