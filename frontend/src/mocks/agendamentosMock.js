import { loginMock } from "./loginMock";

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
