package br.com.zup.chave

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ChaveRepository: JpaRepository<Chave, String> {

}
