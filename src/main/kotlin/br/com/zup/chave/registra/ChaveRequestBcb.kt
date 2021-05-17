package br.com.zup.chave.registra

import br.com.zup.chave.TipoChave
import br.com.zup.chave.TipoConta
import br.com.zup.chave.TipoPessoa
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class ChaveRequestBcb(
    @field: NotNull val keyType: PixKeyType,
    @field: NotNull val key: String?,
    @field: NotNull val bankAccount: BankAccount,
    @field: NotNull val owner: Owner
) {
}
