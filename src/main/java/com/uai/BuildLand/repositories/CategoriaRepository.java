package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,String> {
    @Query(value = "SELECT nombre_categoria FROM distribuidora_categoria dc WHERE dc.usuario_cuit = :distribuidoraCuit", nativeQuery = true)
    List<Categoria> findByDistribuidoraId(String distribuidoraCuit);
}
