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
    
    // Desestrutura o envelope ApiResponse retornado pelo Spring Boot
    const { token: novoToken, usuario: novoUsuario } = response.data.dados;

    localStorage.setItem("token", novoToken);
    localStorage.setItem("usuario", JSON.stringify(novoUsuario));
    setToken(novoToken);
    setUsuario(novoUsuario);

    return novoUsuario;
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("usuario");
    setToken(null);
    setUsuario(null);
  }

  return (
    <AuthContext.Provider value={{ token, usuario, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
