package com.uai.BuildLand.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.auth.Usuario;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, String>, PagingAndSortingRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String coreeo);

    @Modifying
    @Transactional
    @Query(value = "DELETE usuarios_conjunto WHERE usuario_cuit = :cuit", nativeQuery = true)
    void eliminarConjuntoGrupoPermiso(@Param("cuit") String cuit);

    Optional<Usuario> findByCuit(String cuit);
}
