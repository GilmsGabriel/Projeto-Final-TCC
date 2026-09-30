import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./LoginPage.css";

export default function RegistroPage() {
  const [nomeCompleto, setNomeCompleto] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState("");
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    if (loading) return;

    setErro("");
    if (senha !== confirmarSenha) {
      setErro("As senhas não coincidem. Confira a confirmação de senha.");
      return;
    }

    setLoading(true);
    try {
      await api.post("/api/auth/registrar", { nomeCompleto, email, senha, confirmarSenha });
      navigate("/login", {
        state: { mensagemSucesso: "Cadastro realizado com sucesso. Entre na sua conta." },
      });
    } catch (error) {
      setErro(error.response?.data?.message || "Não foi possível realizar o cadastro. Tente novamente.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page">
      <form className="login-form" onSubmit={handleSubmit} aria-busy={loading}>
        <h1>Criar conta</h1>
        <label htmlFor="registro-nome">Nome completo</label>
        <input
          id="registro-nome"
          type="text"
          autoComplete="name"
          required
          value={nomeCompleto}
          onChange={(event) => setNomeCompleto(event.target.value)}
        />
        <label htmlFor="registro-email">E-mail</label>
        <input
          id="registro-email"
          type="email"
          autoComplete="email"
          required
          value={email}
          onChange={(event) => setEmail(event.target.value)}
        />
        <label htmlFor="registro-senha">Senha</label>
        <input
          id="registro-senha"
          type="password"
          autoComplete="new-password"
          required
          value={senha}
          onChange={(event) => setSenha(event.target.value)}
        />
        <label htmlFor="registro-confirmar-senha">Confirmar senha</label>
        <input
          id="registro-confirmar-senha"
          type="password"
          autoComplete="new-password"
          required
          value={confirmarSenha}
          onChange={(event) => setConfirmarSenha(event.target.value)}
        />
        {erro && <p className="login-error" role="alert">{erro}</p>}
        <button type="submit" disabled={loading}>
          {loading ? "Cadastrando…" : "Criar conta"}
        </button>
        <Link to="/login">Já tenho uma conta</Link>
      </form>
    </main>
  );
}
