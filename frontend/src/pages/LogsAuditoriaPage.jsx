import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import "./LogsAuditoriaPage.css";

function formatarDataHora(dataHora) {
  return new Date(dataHora).toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  }).replace(",", "");
}

export default function LogsAuditoriaPage() {
  const [logs, setLogs] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    let cancelado = false;

    async function carregarLogs() {
      try {
        const { data } = await api.get("/api/admin/logs");
        if (!cancelado) {
          setLogs([...data].sort((a, b) => new Date(b.dataHora).getTime() - new Date(a.dataHora).getTime()));
        }
      } catch (error) {
        if (!cancelado) {
          setErro(error.response?.data?.message || "Não foi possível carregar os logs de auditoria. Tente novamente mais tarde.");
        }
      } finally {
        if (!cancelado) setCarregando(false);
      }
    }

    carregarLogs();
    return () => {
      cancelado = true;
    };
  }, []);

  return (
    <main className="logs-auditoria-page" aria-busy={carregando}>
      <h1>Logs de auditoria</h1>
      <Link to="/admin">Voltar para administração</Link>
      {carregando && <p role="status">Carregando logs de auditoria…</p>}
      {erro && <p className="logs-auditoria-error" role="alert">{erro}</p>}
      {!carregando && !erro && logs.length === 0 && (
        <p>Nenhum registro de auditoria no momento. As atividades aparecerão aqui.</p>
      )}
      {!carregando && !erro && logs.length > 0 && (
        <div className="logs-auditoria-tabela-container" role="region" aria-label="Tabela de logs de auditoria" tabIndex={0}>
          <table className="logs-auditoria-tabela">
            <caption>Registros do mais recente para o mais antigo</caption>
            <thead>
              <tr>
                <th scope="col">Data e hora</th>
                <th scope="col">Usuário</th>
                <th scope="col">Ação</th>
                <th scope="col">Entidade afetada</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td><time dateTime={log.dataHora}>{formatarDataHora(log.dataHora)}</time></td>
                  <td>{log.usuario.nomeCompleto}</td>
                  <td>{log.acao}</td>
                  <td>{log.entidadeAfetada}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}
