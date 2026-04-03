package com.wms.infrastructure.persistence.repository

import com.wms.domain.location.model.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationJpaRepository : JpaRepository<Location, Long> {
    fun findByLocationCode(locationCode: String): Location?
    fun findAllByZoneIdAndIsDeletedFalse(zoneId: Long): List<Location>
    fun findAllByZoneId(zoneId: Long): List<Location>
}
