package br.com.zup.chave.registra

import br.com.zup.PixRequest
import br.com.zup.RemovePixRequest
import br.com.zup.chave.TipoChave
import br.com.zup.chave.TipoConta
import br.com.zup.chave.TipoPessoa
import br.com.zup.chave.remove.DeletaChaveRequest
import javax.validation.ConstraintViolationException
import javax.validation.Validator


fun PixRequest.toModel(validator: Validator) : ChaveRequest {
    val chaveDTO = ChaveRequest(
        clienteId = id,
        tipoChave = when(tipoChave){
            br.com.zup.TipoChave.CHAVE_DEFAULT -> null
            else -> TipoChave.valueOf(tipoChave.name)
        },
        chave = chave,
        tipoConta = when(tipoConta){
            br.com.zup.TipoConta.CONTA_DEFAULT -> null
                else -> TipoConta.valueOf(tipoConta.name)
        },
        tipoPessoa = when(tipoPessoa){
            br.com.zup.TipoPessoa.PESSOA_DEFAULT -> null
            else -> TipoPessoa.valueOf(tipoPessoa.name)
        }
    )
    val validFields = validator.validate(chaveDTO)
    if(validFields.isNotEmpty()){
        throw ConstraintViolationException(validFields)
    }
    return chaveDTO
}
