package br.com.zup.validation

import br.com.zup.chave.ChaveRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.br.CPF
import javax.inject.Singleton
import javax.validation.Constraint

import javax.validation.Payload
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern
import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidaChavePix::class])
@MustBeDocumented
annotation class ValidaChave(
    val message: String = "Chave pix inválida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)


@Singleton
class ValidaChavePix : ConstraintValidator<ValidaChave, ChaveRequest>{
    override fun isValid(
        value: ChaveRequest?,
        annotationMetadata: AnnotationValue<ValidaChave>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value?.tipoChave== null){
            return false
        }
        println(value.chave)
        return value.tipoChave.valida(value.chave)

    }
}
