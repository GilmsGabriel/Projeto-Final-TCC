import { useState } from "react";
import api from "../api/api";
import CalendarioDisponibilidade from "./CalendarioDisponibilidade";
import "./FormularioAgendamento.css";

export default function FormularioAgendamento() {
  const [mesCalendario, setMesCalendario] = useState(() => {
    const hoje = new Date();
    return `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, "0")}`;
  });
  const [dataEvento, setDataEvento] = useState("");
  const [descricaoEvento, setDescricaoEvento] = useState("");
  const [arquivo, setArquivo] = useState(null);
  const [agendamentoCriado, setAgendamentoCriado] = useState(null);
  const [atualizacaoCalendario, setAtualizacaoCalendario] = useState(0);
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [ano, mes] = mesCalendario.split("-").map(Number);

  async function handleSubmit(event) {
    event.preventDefault();
    if (loading) return;

    const formulario = event.currentTarget;
    setErro("");
    setSucesso("");

    if (!agendamentoCriado) {
      const diaSemana = new Date(`${dataEvento}T00:00:00`).getDay();
      if (!dataEvento || (diaSemana !== 0 && diaSemana !== 6)) {
        setErro("Selecione um sábado ou domingo disponível no calendário.");
        return;
      }
      if (!descricaoEvento.trim()) {
        setErro("A descrição do evento é obrigatória.");
        return;
      }
    }

    setLoading(true);
    let id = agendamentoCriado;

    try {
      if (!id) {
        const { data } = await api.post("/api/agendamentos", {
          dataEvento,
          descricaoEvento: descricaoEvento.trim(),
        });
        id = data.id;
        setAgendamentoCriado(id);
        setAtualizacaoCalendario((atual) => atual + 1);
      }

      if (arquivo) {
        const formData = new FormData();
        formData.append("arquivo", arquivo);
        await api.post(`/api/agendamentos/${id}/anexo`, formData);
      }

      setSucesso(arquivo
        ? "Agendamento solicitado e anexo enviado com sucesso."
        : "Agendamento solicitado com sucesso.");
      setDataEvento("");
      setDescricaoEvento("");
      setArquivo(null);
      setAgendamentoCriado(null);
      formulario.reset();
    } catch (error) {
      const mensagem = error.response?.data?.message || "Não foi possível concluir a solicitação. Tente novamente.";
      setErro(id ? `Agendamento criado, mas o anexo não foi enviado. ${mensagem}` : mensagem);
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="formulario-agendamento" onSubmit={handleSubmit} aria-busy={loading}>
      <h2>Solicitar agendamento</h2>
      <fieldset disabled={loading || Boolean(agendamentoCriado)}>
        <legend>Data do evento</legend>
        <label htmlFor="agendamento-mes">Mês do calendário</label>
        <input
          id="agendamento-mes"
          type="month"
          required
          value={mesCalendario}
          onChange={(event) => {
            setMesCalendario(event.target.value);
            setDataEvento("");
          }}
        />
        {mesCalendario && (
          <CalendarioDisponibilidade
            key={atualizacaoCalendario}
            mes={mes}
            ano={ano}
            dataSelecionada={dataEvento}
            onSelecionarData={setDataEvento}
          />
        )}
        <input type="hidden" name="dataEvento" value={dataEvento} />
        <p role="status">
          {dataEvento ? `Data selecionada: ${dataEvento.split("-").reverse().join("/")}` : "Selecione um sábado ou domingo livre."}
        </p>
        <label htmlFor="agendamento-descricao">Descrição do evento</label>
        <textarea
          id="agendamento-descricao"
          name="descricaoEvento"
          required
          rows={3}
          value={descricaoEvento}
          onChange={(event) => setDescricaoEvento(event.target.value)}
        />
      </fieldset>
      <label htmlFor="agendamento-anexo">Anexo (opcional)</label>
      <input
        id="agendamento-anexo"
        type="file"
        name="arquivo"
        disabled={loading}
        onChange={(event) => setArquivo(event.target.files[0] || null)}
      />
      {erro && <p className="formulario-agendamento-error" role="alert">{erro}</p>}
      {sucesso && <p className="formulario-agendamento-success" role="status">{sucesso}</p>}
      <button type="submit" disabled={loading}>
        {loading ? "Enviando…" : agendamentoCriado ? "Reenviar anexo" : "Solicitar agendamento"}
      </button>
    </form>
  );
}
