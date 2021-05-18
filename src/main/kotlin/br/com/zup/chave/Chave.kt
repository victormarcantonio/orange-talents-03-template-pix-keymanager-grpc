package br.com.zup.chave

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Entity
class Chave(@field: NotNull val clienteId: UUID,
            @field: NotNull val tipoChave: TipoChave,
            @field: NotBlank @field: Size(max=77) var chaveId: String,
            @field: NotNull var tipoConta: TipoConta,
            @field:Embedded val conta: Conta
) {


    @Id
    @GeneratedValue
    val id: UUID? = null


    fun pertenceAoCliente(clienteId: UUID):Boolean{
        return this.clienteId.equals(clienteId)
    }

    fun isAleatoria(): Boolean{
        return tipoChave== TipoChave.ALEATORIA
    }

    fun atualiza(chaveAleatoria: String): Boolean{
        if(isAleatoria()){
            this.chaveId = chaveAleatoria
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Chave) return false

        if (clienteId != other.clienteId) return false
        if (tipoChave != other.tipoChave) return false
        if (chaveId != other.chaveId) return false
        if (tipoConta != other.tipoConta) return false
        if (conta != other.conta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clienteId.hashCode()
        result = 31 * result + tipoChave.hashCode()
        result = 31 * result + chaveId.hashCode()
        result = 31 * result + tipoConta.hashCode()
        result = 31 * result + conta.hashCode()
        return result
    }


}
