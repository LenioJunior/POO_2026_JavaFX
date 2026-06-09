package br.edu.ifsuldeminas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CadastroUsuarioControllerTest {

    @Test
    void nomeVazio_retornaErro() {
        assertEquals("O campo Nome é obrigatório.",
                CadastroUsuarioController.validarCampos("", "a@b.com", "3"));
    }

    @Test
    void emailSemArroba_retornaErro() {
        assertEquals("Informe um e-mail válido.",
                CadastroUsuarioController.validarCampos("João", "semArroba.com", "3"));
    }

    @Test
    void emailVazio_retornaErro() {
        assertEquals("Informe um e-mail válido.",
                CadastroUsuarioController.validarCampos("João", "", "3"));
    }

    @Test
    void limiteNegativo_retornaErro() {
        assertEquals("Limite deve ser um número inteiro não negativo.",
                CadastroUsuarioController.validarCampos("João", "a@b.com", "-1"));
    }

    @Test
    void limiteTexto_retornaErro() {
        assertEquals("Limite deve ser um número inteiro não negativo.",
                CadastroUsuarioController.validarCampos("João", "a@b.com", "abc"));
    }

    @Test
    void camposValidos_retornaNulo() {
        assertNull(CadastroUsuarioController.validarCampos(
                "Maria", "maria@ifsuldeminas.edu.br", "5"));
    }

    @Test
    void limiteZero_valido() {
        assertNull(CadastroUsuarioController.validarCampos("Ana", "ana@x.com", "0"));
    }
}
