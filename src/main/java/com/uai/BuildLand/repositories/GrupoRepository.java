package com.uai.BuildLand.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.access.Grupo;

public interface GrupoRepository extends CrudRepository<Grupo, Long>, PagingAndSortingRepository<Grupo, Long> {
}
