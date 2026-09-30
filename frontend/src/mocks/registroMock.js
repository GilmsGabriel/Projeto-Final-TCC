import { loginMock } from "./loginMock";

const usuarios = [{ ...loginMock.usuario, dataCriacao: "2026-09-19T10:15:30Z" }];

export function registrarFake({ nomeCompleto, email }) {
  const emailNormalizado = email.trim().toLowerCase();

  if (usuarios.some((usuario) => usuario.email === emailNormalizado)) {
    throw new Error("Este e-mail já está cadastrado. Use outro e-mail ou entre na sua conta.");
  }

  const usuario = {
    id: usuarios.length + 1,
    nomeCompleto,
    email: emailNormalizado,
    perfil: "COMUM",
    dataCriacao: new Date().toISOString(),
  };
  usuarios.push(usuario);

  return usuario;
}
