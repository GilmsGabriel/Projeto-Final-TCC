package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.dto.RegistroRequestDTO;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.PerfilUsuario;
import br.edu.escola.agendamento.exception.RegraNegocioException;
import br.edu.escola.agendamento.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO registrar(RegistroRequestDTO dto) {

        if (dto == null) {
            throw new RegraNegocioException(
                    "Dados de registro inválidos.",
                    "DADOS_REGISTRO_INVALIDOS",
                    "Os dados do usuário devem ser informados."
            );
        }

        if (dto.getNomeCompleto() == null
                || dto.getNomeCompleto().isBlank()
                || dto.getEmail() == null
                || dto.getEmail().isBlank()
                || dto.getSenha() == null
                || dto.getSenha().isBlank()
                || dto.getConfirmarSenha() == null
                || dto.getConfirmarSenha().isBlank()) {

            throw new RegraNegocioException(
                    "Dados de registro incompletos.",
                    "DADOS_REGISTRO_INVALIDOS",
                    "Nome, e-mail, senha e confirmação de senha são obrigatórios."
            );
        }

        if (!dto.getSenha().equals(dto.getConfirmarSenha())) {
            throw new RegraNegocioException(
                    "As senhas informadas não coincidem.",
                    "SENHAS_DIVERGENTES",
                    "A senha e a confirmação de senha devem ser iguais."
            );
        }

        String email = dto.getEmail().trim();

        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException(
                    "E-mail já cadastrado.",
                    "EMAIL_JA_CADASTRADO",
                    "Já existe um usuário registrado com esse e-mail."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNomeCompleto(dto.getNomeCompleto().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(PerfilUsuario.COMUM);
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        UsuarioResponseDTO response = new UsuarioResponseDTO();

        response.setId(usuarioSalvo.getId());
        response.setNomeCompleto(usuarioSalvo.getNomeCompleto());
        response.setEmail(usuarioSalvo.getEmail());
        response.setPerfil(usuarioSalvo.getPerfil());
        response.setDataCriacao(usuarioSalvo.getDataCriacao());

        return response;
    }
}