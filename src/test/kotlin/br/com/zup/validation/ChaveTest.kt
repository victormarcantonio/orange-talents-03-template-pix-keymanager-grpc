package br.com.zup.validation

import br.com.zup.chave.Chave
import br.com.zup.chave.Conta
import br.com.zup.chave.TipoChave
import br.com.zup.chave.TipoConta
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class ChaveTest {

    val conta = Conta(
        "Itaú",
        "12345",
        "Victor Marcatonio",
        "111.111.111-11",
        "1", "123"
    )

    val chave =  Chave(
        UUID.randomUUID(),
        TipoChave.CELULAR,
        "111.111.111-11",
        TipoConta.CONTA_CORRENTE,
        conta
    )

    val chaveAleatoria =  Chave(
        UUID.randomUUID(),
        TipoChave.ALEATORIA,
        "111.111.111-11",
        TipoConta.CONTA_CORRENTE,
        conta
    )


    @Test
    internal fun `nao deve atualizar chave caso chave nao seja aleatoria`() {
        assertFalse(chave.atualiza("cd897e65-c4d9-4b90-9e60-6bc394e52afd"))
    }

    @Test
    internal fun `deve atualizar chave caso seja aleatoria`() {
        assertTrue(chaveAleatoria.atualiza("cd897e65-c4d9-4b90-9e60-6bc394e52afd"))
        assertEquals(chaveAleatoria.chave,"cd897e65-c4d9-4b90-9e60-6bc394e52afd" )
    }

    @Test
    internal fun `deve retornar true caso chave seja aleatoria`() {
       assertTrue (chaveAleatoria.isAleatoria())
    }

    @Test
    internal fun `deve retornar false caso chave nao seja aleatoria`() {
        assertFalse(chave.isAleatoria())
    }
}