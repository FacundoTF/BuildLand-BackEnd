package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.Marca;
import com.uai.BuildLand.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    List<Marca> findByCategoriaNombre(String nombreCategoria);
    Optional<Marca> findByNombre(String nombre);
}
