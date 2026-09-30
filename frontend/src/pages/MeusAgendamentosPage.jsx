import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import "./MeusAgendamentosPage.css";

export default function MeusAgendamentosPage() {
  const [agendamentos, setAgendamentos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    let ativo = true;

    async function carregarAgendamentos() {
      try {
        const { data } = await api.get("/api/agendamentos/meus");
        if (ativo) setAgendamentos(data);
      } catch {
        if (ativo) setErro("Não foi possível carregar seus agendamentos. Tente novamente mais tarde.");
      } finally {
        if (ativo) setLoading(false);
      }
    }

    carregarAgendamentos();
    return () => {
      ativo = false;
    };
  }, []);

  return (
    <main className="meus-agendamentos-page" aria-busy={loading}>
      <h1>Meus agendamentos</h1>
      <Link to="/agendamentos">Voltar para agendamentos</Link>
      {loading && <p role="status">Carregando seus agendamentos…</p>}
      {erro && <p className="agendamentos-error" role="alert">{erro}</p>}
      {!loading && !erro && agendamentos.length === 0 && (
        <p>Você ainda não tem agendamentos. Suas solicitações aparecerão aqui.</p>
      )}
      {!loading && !erro && agendamentos.length > 0 && (
        <ul className="agendamentos-lista">
          {agendamentos.map((agendamento) => (
            <li key={agendamento.id}>
              <article className="agendamento-card">
                <h2>{agendamento.descricaoEvento}</h2>
                <p>
                  Data do evento: <time dateTime={agendamento.dataEvento}>
                    {agendamento.dataEvento.split("-").reverse().join("/")}
                  </time>
                </p>
                <span className={`agendamento-status agendamento-status-${agendamento.status.toLowerCase()}`}>
                  {agendamento.status}
                </span>
                {agendamento.status === "REJEITADO" && agendamento.justificativaRejeicao && (
                  <p>Justificativa: {agendamento.justificativaRejeicao}</p>
                )}
              </article>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}
