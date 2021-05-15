package br.com.zup.chave

import br.com.zup.chave.registra.Conta
import br.com.zup.chave.registra.TipoChave
import br.com.zup.chave.registra.TipoConta
import java.util.*
import javax.persistence.*
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



    fun pertenceAoCliente(id: String):Boolean{
        return this.clienteId.equals(id)
    }
}
