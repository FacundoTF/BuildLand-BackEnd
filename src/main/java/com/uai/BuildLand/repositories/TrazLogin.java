package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.dto.TrazabilidadLoginDto;
import com.uai.BuildLand.models.dto.TrazabilidadPersonaFisicaDto;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TrazLogin extends CrudRepository<TrazabilidadLoginDto, String>, PagingAndSortingRepository<TrazabilidadLoginDto, String>, JpaSpecificationExecutor<TrazabilidadLoginDto> {
}
