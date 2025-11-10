package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
}