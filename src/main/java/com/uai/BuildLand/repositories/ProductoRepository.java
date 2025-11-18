package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaNombre(String nombre);

    Optional<Producto> findByNombre(String nombre);
}
