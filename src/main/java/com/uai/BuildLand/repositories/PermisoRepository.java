package com.uai.BuildLand.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.access.Permiso;

public interface PermisoRepository extends CrudRepository<Permiso, Long>, PagingAndSortingRepository<Permiso, Long> {
}
