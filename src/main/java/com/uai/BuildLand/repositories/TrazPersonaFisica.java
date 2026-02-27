package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.dto.TrazabilidadPersonaFisicaDto;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TrazPersonaFisica extends CrudRepository<TrazabilidadPersonaFisicaDto, String>, PagingAndSortingRepository<TrazabilidadPersonaFisicaDto, String>, JpaSpecificationExecutor<TrazabilidadPersonaFisicaDto> {
}
