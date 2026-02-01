package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    List<Marca> findByCategoriaNombre(String nombreCategoria);
}
