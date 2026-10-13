
package br.edu.escola.agendamento.controller;

import br.edu.escola.agendamento.dto.ApiResponse;
import br.edu.escola.agendamento.dto.UsuarioResponseDTO;
import br.edu.escola.agendamento.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> buscarPerfilLogado(
            Authentication auth
    ) {
        String email = auth.getName();

        UsuarioResponseDTO perfil =
                usuarioService.buscarPerfilLogado(email);

        ApiResponse<UsuarioResponseDTO> response = ApiResponse.ok(
                perfil,
                "Perfil do usuário recuperado com sucesso."
        );

        return ResponseEntity.ok(response);
    }
}
