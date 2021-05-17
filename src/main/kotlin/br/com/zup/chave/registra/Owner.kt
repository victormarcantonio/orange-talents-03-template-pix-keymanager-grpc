package br.com.zup.chave.registra

import br.com.zup.chave.TipoPessoa
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotNull

@Introspected
data class Owner(@field: NotNull val type: TipoPessoa?,
                 @field: NotNull val name: String,
                 @field: NotNull val taxIdNumber: String)
