import { loginMock } from "./loginMock";
import { calendarioFake, reservarDataFake } from "./calendarioMock";

export const agendamentosMock = [
  {
    id: 10,
    usuario: {
      id: loginMock.usuario.id,
      nomeCompleto: loginMock.usuario.nomeCompleto,
      email: loginMock.usuario.email,
    },
    dataEvento: "2026-09-26",
    diaSemana: "SATURDAY",
    descricaoEvento: "Festa junina da turma 5A",
    anexoUrl: null,
    status: "PENDENTE",
    justificativaRejeicao: null,
    dataCriacao: "2026-09-19T10:15:30Z",
    dataAtualizacao: "2026-09-19T10:15:30Z",
  },
  {
    id: 11,
    usuario: {
      id: loginMock.usuario.id,
      nomeCompleto: loginMock.usuario.nomeCompleto,
      email: loginMock.usuario.email,
    },
    dataEvento: "2026-10-03",
    diaSemana: "SATURDAY",
    descricaoEvento: "Encontro de famílias",
    anexoUrl: null,
    status: "APROVADO",
    justificativaRejeicao: null,
    dataCriacao: "2026-09-19T11:00:00Z",
    dataAtualizacao: "2026-09-20T10:00:00Z",
  },
  {
    id: 12,
    usuario: {
      id: loginMock.usuario.id,
      nomeCompleto: loginMock.usuario.nomeCompleto,
      email: loginMock.usuario.email,
    },
    dataEvento: "2026-10-10",
    diaSemana: "SATURDAY",
    descricaoEvento: "Feira de ciências",
    anexoUrl: null,
    status: "REJEITADO",
    justificativaRejeicao: "O espaço não estará disponível na data solicitada.",
    dataCriacao: "2026-09-19T12:00:00Z",
    dataAtualizacao: "2026-09-20T11:00:00Z",
  },
];

export function meusAgendamentosFake(usuarioId) {
  return agendamentosMock.filter((agendamento) => agendamento.usuario.id === usuarioId);
}

// POST /api/agendamentos
export function criarAgendamentoFake({ dataEvento, descricaoEvento }, usuario) {
  const [ano, mes, dia] = (dataEvento || "").split("-").map(Number);
  const data = new Date(ano, mes - 1, dia);
  const diaSemana = data.getDay();

  if (diaSemana !== 0 && diaSemana !== 6) {
    throw new Error("Agendamentos são permitidos apenas aos sábados e domingos.");
  }
  if (!descricaoEvento?.trim()) {
    throw new Error("A descrição do evento é obrigatória.");
  }
  if (!usuario) {
    throw new Error("Usuário não encontrado.");
  }

  const dataOcupada = calendarioFake(mes, ano).some((registro) => registro.data === dataEvento)
    || agendamentosMock.some((agendamento) => agendamento.dataEvento === dataEvento && agendamento.status !== "REJEITADO");

  if (dataOcupada) {
    throw new Error("Já existe um agendamento para esta data. Escolha outro sábado ou domingo.");
  }

  const agora = new Date().toISOString();
  const agendamento = {
    id: Math.max(...agendamentosMock.map((item) => item.id)) + 1,
    usuario: { id: usuario.id, nomeCompleto: usuario.nomeCompleto, email: usuario.email },
    dataEvento,
    diaSemana: diaSemana === 6 ? "SATURDAY" : "SUNDAY",
    descricaoEvento: descricaoEvento.trim(),
    anexoUrl: null,
    status: "PENDENTE",
    justificativaRejeicao: null,
    dataCriacao: agora,
    dataAtualizacao: agora,
  };

  agendamentosMock.push(agendamento);
  reservarDataFake(dataEvento);
  return { ...agendamento };
}

// POST /api/agendamentos/{id}/anexo
export function enviarAnexoFake(id, formData) {
  const agendamento = agendamentosMock.find((item) => item.id === Number(id));
  if (!agendamento) throw new Error("Agendamento não encontrado.");

  const arquivo = formData.get("arquivo");
  if (!(arquivo instanceof File) || arquivo.size === 0) {
    throw new Error("Selecione um arquivo não vazio para enviar o anexo.");
  }

  agendamento.anexoUrl = `/anexos/${id}/${encodeURIComponent(arquivo.name)}`;
  agendamento.dataAtualizacao = new Date().toISOString();
  return { ...agendamento };
}
