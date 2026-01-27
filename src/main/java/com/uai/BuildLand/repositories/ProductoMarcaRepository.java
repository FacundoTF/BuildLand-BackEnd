package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.ProductoMarca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoMarcaRepository extends JpaRepository<ProductoMarca, Long> {
    Optional<ProductoMarca> findAllByMarca_NombreAndProducto_Nombre(String marcaNombre, String productoNombre);
}
