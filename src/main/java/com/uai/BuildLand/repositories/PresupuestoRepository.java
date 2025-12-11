package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.presupuesto.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresupuestoRepository extends
        JpaRepository<Presupuesto, Long>,
        PagingAndSortingRepository<Presupuesto, Long>,
        JpaSpecificationExecutor<Presupuesto> {

}