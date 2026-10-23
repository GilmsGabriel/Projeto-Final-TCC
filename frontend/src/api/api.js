import axios, { AxiosError } from "axios";
import { logsAuditoriaFake } from "../mocks/logsAuditoriaMock";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

function rotaMockada(config) {
  if (config.method === "get" && config.url === "/api/admin/logs") return "logsAuditoria";
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
          case "logsAuditoria":
            data = logsAuditoriaFake();
            break;
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
