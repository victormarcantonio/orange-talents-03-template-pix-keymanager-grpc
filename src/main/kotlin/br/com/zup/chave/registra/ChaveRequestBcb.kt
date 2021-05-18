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
    @field: NotNull val bankAccount: BankAccountRequest,
    @field: NotNull val owner: OwnerRequest
) {
}


@Introspected
data class OwnerRequest(@field: NotNull val type: TipoPessoa?,
                 @field: NotNull val name: String,
                 @field: NotNull val taxIdNumber: String)


@Introspected
data class BankAccountRequest(@field: NotNull val participant: String,
                       @field: NotBlank val branch: String,
                       @field: NotBlank val accountNumber: String,
                       @field: NotNull val accountType: AccountType)

