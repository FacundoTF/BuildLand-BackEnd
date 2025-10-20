package com.uai.BuildLand.repositories;

import org.springframework.data.repository.CrudRepository;

import com.uai.BuildLand.models.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
    boolean existsByEmailAndPassword(String email, String password);
    Optional<Usuario> findByEmail(String email);
}
