package br.com.zup.chave

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {
   CPF {
      override fun valida(chave: String?): Boolean {
         if(chave.isNullOrBlank()){
            return false
         }

         return CPFValidator().run {
            initialize(null)
            isValid(chave,null)
         }

      }
   }, CELULAR {
      override fun valida(chave: String?): Boolean {
         if(chave.isNullOrBlank()){
            return false
         }

         return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())

      }
   }, EMAIL {
      override fun valida(chave: String?): Boolean {
         if(chave.isNullOrBlank()){
            return false
         }

         return EmailValidator().run{
            initialize(null)
            isValid(chave,null)
         }
      }
   }, ALEATORIA {
      override fun valida(chave: String?): Boolean = chave.isNullOrBlank()
   };


   abstract fun valida(chave: String?): Boolean
}
