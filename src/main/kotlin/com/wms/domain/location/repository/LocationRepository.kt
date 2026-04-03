package com.wms.domain.location.repository

import com.wms.domain.location.model.Location
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LocationRepository {
    fun findById(id: Long): Location?
    fun findByCode(code: String): Location?
    fun findByZoneId(zoneId: Long): List<Location>
    fun findByZoneId(zoneId: Long, pageable: Pageable): Page<Location>
    fun findAll(pageable: Pageable): Page<Location>
    fun save(location: Location): Location
    fun delete(location: Location)
    fun existsByCode(code: String): Boolean
}
