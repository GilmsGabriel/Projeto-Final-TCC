package br.edu.escola.agendamento;

import br.edu.escola.agendamento.enums.PerfilUsuario;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PerfilUsuarioTest {

    @Enumerated(EnumType.STRING)
    private PerfilUsuario perfilUsuario;

    @Test
    void devePossuirOsPerfisEsperados() {
        assertEquals(2, PerfilUsuario.values().length);
        assertEquals(PerfilUsuario.COMUM, PerfilUsuario.valueOf("COMUM"));
        assertEquals(PerfilUsuario.ADMINISTRADOR, PerfilUsuario.valueOf("ADMINISTRADOR"));
    }
}