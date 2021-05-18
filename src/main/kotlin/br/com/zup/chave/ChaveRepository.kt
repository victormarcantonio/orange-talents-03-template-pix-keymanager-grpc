package br.com.zup.chave

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChaveRepository: JpaRepository<Chave, UUID> {

    fun existsByChaveId(id: String): Boolean
    fun findByChaveId(id: String): Optional<Chave>
}
