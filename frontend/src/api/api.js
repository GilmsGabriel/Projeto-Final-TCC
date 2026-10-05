import axios, { AxiosError } from "axios";
import { registrarFake } from "../mocks/registroMock";
import { meusAgendamentosFake, criarAgendamentoFake, enviarAnexoFake } from "../mocks/agendamentosMock";
import { calendarioFake } from "../mocks/calendarioMock";
import { pendentesFake, aprovarFake, rejeitarFake } from "../mocks/adminMock";
import { logsAuditoriaFake } from "../mocks/logsAuditoriaMock";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

const REGEX_ANEXO = /^\/api\/agendamentos\/(\d+)\/anexo$/;
const REGEX_APROVAR = /^\/api\/admin\/agendamentos\/(\d+)\/aprovar$/;
const REGEX_REJEITAR = /^\/api\/admin\/agendamentos\/(\d+)\/rejeitar$/;

function rotaMockada(config) {
  if (config.method === "post" && config.url === "/api/auth/registrar") return "registrar";
  if (config.method === "post" && config.url === "/api/agendamentos") return "criarAgendamento";
  if (config.method === "post" && REGEX_ANEXO.test(config.url)) return "enviarAnexo";
  if (config.method === "get" && config.url === "/api/agendamentos/meus") return "meus";
  if (config.method === "get" && config.url === "/api/agendamentos/calendario") return "calendario";
  if (config.method === "get" && config.url === "/api/admin/agendamentos/pendentes") return "pendentes";
  if (config.method === "get" && config.url === "/api/admin/logs") return "logsAuditoria";
  if (config.method === "put" && REGEX_APROVAR.test(config.url)) return "aprovar";
  if (config.method === "put" && REGEX_REJEITAR.test(config.url)) return "rejeitar";
  return null;
}

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  const rota = rotaMockada(config);

  if (rota) {
    config.adapter = async (mockConfig) => {
      await new Promise((resolve) => setTimeout(resolve, 300));

      try {
        let data;
        let status = 200;

        switch (rota) {
          case "registrar":
            data = registrarFake(JSON.parse(mockConfig.data));
            status = 201;
            break;
          case "criarAgendamento":
            data = criarAgendamentoFake(JSON.parse(mockConfig.data), JSON.parse(localStorage.getItem("usuario")));
            status = 201;
            break;
          case "enviarAnexo": {
            const [, id] = mockConfig.url.match(REGEX_ANEXO);
            data = enviarAnexoFake(id, mockConfig.data);
            break;
          }
          case "meus":
            data = meusAgendamentosFake(JSON.parse(localStorage.getItem("usuario"))?.id);
            break;
          case "calendario": {
            const { mes, ano } = mockConfig.params || {};
            data = calendarioFake(Number(mes), Number(ano));
            break;
          }
          case "pendentes":
            data = pendentesFake();
            break;
          case "logsAuditoria":
            data = logsAuditoriaFake();
            break;
          case "aprovar": {
            const [, id] = mockConfig.url.match(REGEX_APROVAR);
            data = aprovarFake(id);
            break;
          }
          case "rejeitar": {
            const [, id] = mockConfig.url.match(REGEX_REJEITAR);
            const { justificativa } = JSON.parse(mockConfig.data);
            data = rejeitarFake(id, justificativa);
            break;
          }
          default:
            throw new Error("Rota mockada não implementada");
        }

        return {
          data,
          status,
          statusText: "OK",
          headers: {},
          config: mockConfig,
        };
      } catch (error) {
        throw new AxiosError(error.message, "ERR_BAD_REQUEST", mockConfig, null, {
          data: { message: error.message },
          status: 409,
          statusText: "Conflict",
          headers: {},
          config: mockConfig,
        });
      }
    };
  }

  return config;
});

export default api;
