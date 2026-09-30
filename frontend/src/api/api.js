import axios, { AxiosError } from "axios";
import { registrarFake } from "../mocks/registroMock";

const api = axios.create({
  baseURL: "http://localhost:8080"
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  if (config.method === "post" && config.url === "/api/auth/registrar") {
    config.adapter = async (mockConfig) => {
      await new Promise((resolve) => setTimeout(resolve, 300));

      try {
        const data = registrarFake(JSON.parse(mockConfig.data));

        return {
          data,
          status: 201,
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
