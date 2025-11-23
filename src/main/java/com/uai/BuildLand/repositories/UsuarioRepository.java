package com.uai.BuildLand.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.auth.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long>, PagingAndSortingRepository<Usuario, Long> {
    boolean existsByEmailAndPassword(String email, String password);
    Optional<Usuario> findByEmail(String coreeo);
}
