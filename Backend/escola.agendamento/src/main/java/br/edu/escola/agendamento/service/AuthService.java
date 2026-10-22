package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.dto.LoginRequestDTO;
import br.edu.escola.agendamento.dto.RegistroRequestDTO;
import br.edu.escola.agendamento.dto.TokenResponseDTO;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.enums.PerfilUsuario;
import br.edu.escola.agendamento.exception.RegraNegocioException;
import br.edu.escola.agendamento.repository.UsuarioRepository;
import br.edu.escola.agendamento.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.escola.agendamento.service.LogAuditoriaService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogAuditoriaService logAuditoriaService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            LogAuditoriaService logAuditoriaService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.logAuditoriaService = logAuditoriaService;
    }

    public TokenResponseDTO autenticar(LoginRequestDTO dto) {
        // Busca o usuário para validar status e montar o DTO de retorno
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));

        // Valida que usuários inativos não autenticam
        if (!usuario.isAtivo()) {
            throw new DisabledException("Usuário inativo não pode autenticar.");
        }

        // Valida as credenciais com o Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        // Gera o token JWT
        String token = jwtTokenProvider.gerarToken(authentication);

        // Mapeia o UsuarioResponseDTO usando os setters padrão da classe já existente
        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();
        usuarioResponseDTO.setId(usuario.getId());
        usuarioResponseDTO.setNomeCompleto(usuario.getNomeCompleto());
        usuarioResponseDTO.setEmail(usuario.getEmail());
        usuarioResponseDTO.setPerfil(usuario.getPerfil());
        usuarioResponseDTO.setDataCriacao(usuario.getDataCriacao());

        // Retorna o response do token de acordo com o contrato
        return TokenResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .expiraEm(3600L)
                .usuario(usuarioResponseDTO)
                .build();
    }

    @Transactional
    public UsuarioResponseDTO registrar(RegistroRequestDTO dto) {
        // ... (resto do método registrar mantido exatamente como você enviou) ...

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

        logAuditoriaService.registrarEvento(
                usuarioSalvo,
                "REGISTRO_USUARIO",
                "Usuario#" + usuarioSalvo.getId(),
                "Novo usuário cadastrado: " + usuarioSalvo.getEmail()
        );

        UsuarioResponseDTO response = new UsuarioResponseDTO();

        response.setId(usuarioSalvo.getId());
        response.setNomeCompleto(usuarioSalvo.getNomeCompleto());
        response.setEmail(usuarioSalvo.getEmail());
        response.setPerfil(usuarioSalvo.getPerfil());
        response.setDataCriacao(usuarioSalvo.getDataCriacao());

        return response;
    }
}