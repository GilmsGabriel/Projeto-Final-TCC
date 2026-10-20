import { useState } from "react";
import api from "../api/api";
import { AuthContext } from "./authContext";

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("token"));
  const [usuario, setUsuario] = useState(() => {
    const usuarioSalvo = localStorage.getItem("usuario");
    return usuarioSalvo ? JSON.parse(usuarioSalvo) : null;
  });

  async function login(email, senha) {
    const response = await api.post("/api/auth/login", { email, senha });
    const { token: novoToken, usuario: novoUsuario } = response.data.dados;

    localStorage.setItem("token", novoToken);
    localStorage.setItem("usuario", JSON.stringify(novoUsuario));
    setToken(novoToken);
    setUsuario(novoUsuario);

    return novoUsuario;
  }

  async function registrar({ nomeCompleto, email, senha, confirmarSenha }) {
    const response = await api.post("/api/auth/registrar", {
      nomeCompleto,
      email,
      senha,
      confirmarSenha,
    });

    return response.data.dados;
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    setToken(null);
    setUsuario(null);
  }

  return (
    <AuthContext.Provider value={{ token, usuario, login, registrar, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
