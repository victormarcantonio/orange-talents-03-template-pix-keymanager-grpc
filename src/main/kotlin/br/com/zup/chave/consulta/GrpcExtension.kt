package br.com.zup.chave.consulta
import br.com.zup.ConsultaPixRequest.FiltroCase.*
import br.com.zup.ConsultaPixRequest
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun ConsultaPixRequest.toModel(validator: Validator): Filtro{

    val filtro = when(filtroCase){
        PIXID -> pixId.let{
            Filtro.PorPixId(clienteId = it.clienteId, pixId =  it.pixId)
        }
        CHAVE-> Filtro.PorChave(chave)
        FILTRO_NOT_SET -> Filtro.Invalido()
    }

    val violations = validator.validate(filtro)
    if(violations.isNotEmpty()){
        throw javax.validation.ConstraintViolationException(violations)
    }

    return filtro
}