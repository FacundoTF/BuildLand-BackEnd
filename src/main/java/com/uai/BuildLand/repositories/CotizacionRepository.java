package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.cotizacion.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
}
