package com.uai.BuildLand.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.access.Grupo;

public interface GrupoRepository extends CrudRepository<Grupo, Long>, PagingAndSortingRepository<Grupo, Long> {
    Page<Grupo> findAll(Specification<Grupo> gruposProhibidosSpec, Pageable nombre);
}
