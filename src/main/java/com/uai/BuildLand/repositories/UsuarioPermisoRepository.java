package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.auth.UsuarioPermiso;
import com.uai.BuildLand.models.access.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioPermisoRepository extends JpaRepository<UsuarioPermiso, UsuarioPermiso.UsuarioPermisoId> {
    
    List<UsuarioPermiso> findByCuit(Usuario usuario);
    
    List<UsuarioPermiso> findByPermiso(Permiso permiso);
    
    Optional<UsuarioPermiso> findByCuitAndPermiso(Usuario usuario, Permiso permiso);
    
    boolean existsByCuitAndPermiso(Usuario usuario, Permiso permiso);
    
    void deleteByCuitAndPermiso(Usuario usuario, Permiso permiso);
}
