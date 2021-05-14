package br.com.zup.validation

import br.com.zup.chave.registra.TipoChave
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidaChaveTest {

    @Test
    internal fun `deve retornar true caso cpf seja valido`() {
        assertTrue(TipoChave.CPF.valida("11111111111"))
    }

    @Test
    internal fun `deve retornar false caso cpf seja invalido`() {
        assertFalse(TipoChave.CPF.valida("11111111"))
    }

    @Test
    internal fun `deve retornar true caso o email seja valido`() {
        assertTrue(TipoChave.EMAIL.valida("victor_marcantonio@hotmail.com"))
    }

    @Test
    internal fun `deve retornar false caso o email seja invalido`() {
        assertFalse(TipoChave.EMAIL.valida("victor_marcantoniohotmail.com"))
    }


    @Test
    internal fun `deve validar celular`() {
        assertTrue(TipoChave.CELULAR.valida("+5585988714077"))
    }

    @Test
    internal fun `deve retornar false caso celular seja invalido`() {
        assertFalse(TipoChave.CELULAR.valida("123"))
    }

    @Test
    internal fun `deve ser valido quando chave aleatoria for nula`() {
        assertTrue(TipoChave.ALEATORIA.valida(""))
    }

}