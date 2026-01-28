package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.presupuesto.Presupuesto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PresupuestoRepository extends
        JpaRepository<Presupuesto, Long>,
        PagingAndSortingRepository<Presupuesto, Long>,
        JpaSpecificationExecutor<Presupuesto> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO presupuestos_rechazados (cuit, id_presupuesto) VALUES (:cuit, :idPresupuesto)", nativeQuery = true)
    void rechazarPresupuesto(@Param("idPresupuesto") Long idPresupuesto, @Param("cuit") String cuit);
}