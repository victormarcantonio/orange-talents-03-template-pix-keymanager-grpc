package br.com.zup.chave

import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Entity
class Chave(@field: NotNull val clienteId: UUID,
            @field: NotNull val tipoChave: TipoChave,
            @field: NotBlank @field: Size(max=77) @field: Id var chaveId: String,
            @field: NotNull val tipoConta: TipoConta,
            @field:Embedded val conta: Conta
) {
}
