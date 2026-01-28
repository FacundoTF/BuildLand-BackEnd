package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.presupuesto.PresupuestoRechazado;
import com.uai.BuildLand.models.presupuesto.PresupuestoRechazadoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresupuestoRechazadoRepository extends JpaRepository<PresupuestoRechazado, PresupuestoRechazadoId> {
//    List<PresupuestoRechazado> findByCuit(String cuit);
}
