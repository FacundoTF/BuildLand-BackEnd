package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.auth.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.PersonaFisica;

public interface PersonaFisicaRepository extends CrudRepository<PersonaFisica, String>, PagingAndSortingRepository<PersonaFisica, String> {
    PersonaFisica findByUsuario(Usuario usuario);
}
