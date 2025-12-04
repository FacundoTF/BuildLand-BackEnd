package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,String> {
}
