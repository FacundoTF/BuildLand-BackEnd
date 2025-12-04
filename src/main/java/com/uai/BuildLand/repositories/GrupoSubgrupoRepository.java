package com.uai.BuildLand.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.uai.BuildLand.models.access.GrupoSubgrupo;

public interface GrupoSubgrupoRepository extends CrudRepository<GrupoSubgrupo, Long>, PagingAndSortingRepository<GrupoSubgrupo, Long> {
    
    /**
     * Verifica si existe una relación inversa (grupoHijo contiene a grupoPadre)
     * Previene referencias circulares
     */
    boolean existsByGrupoPadreIdAndGrupoHijoId(@Param("grupoPadreId") Long grupoPadreId, @Param("grupoHijoId") Long grupoHijoId);
}
