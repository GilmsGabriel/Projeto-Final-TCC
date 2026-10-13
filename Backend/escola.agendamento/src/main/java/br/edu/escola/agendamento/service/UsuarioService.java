
package br.edu.escola.agendamento.service;

import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.entity.Usuario;
import br.edu.escola.agendamento.exception.RecursoNaoEncontradoException;
import br.edu.escola.agendamento.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPerfilLogado(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado.",
                        "USUARIO_NAO_ENCONTRADO"
                ));

        UsuarioResponseDTO response = new UsuarioResponseDTO();

        response.setId(usuario.getId());
        response.setNomeCompleto(usuario.getNomeCompleto());
        response.setEmail(usuario.getEmail());
        response.setPerfil(usuario.getPerfil());
        response.setDataCriacao(usuario.getDataCriacao());

        return response;
    }
}
