package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.presupuesto.Presupuesto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PresupuestoRepository extends
        JpaRepository<Presupuesto, Long>,
        PagingAndSortingRepository<Presupuesto, Long>,
        JpaSpecificationExecutor<Presupuesto> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO presupuestos_rechazados (cuit, id_presupuesto) VALUES (:cuit, :idPresupuesto)", nativeQuery = true)
    void rechazarPresupuesto(@Param("idPresupuesto") Long idPresupuesto, @Param("cuit") String cuit);

    @Procedure
    List<Object> sp_ReporteProductosSolicitadosGral(String Categoria, Date FechaInicio, Date FechaFin);
    @Procedure
    List<Object> sp_ReporteProductosSolicitadosMarca(String Categoria, String Marca, Date FechaInicio, Date FechaFin);
    @Procedure
    List<Object> sp_ReporteProductosSolicitadosProducto(String Categoria, String Producto, Date FechaInicio, Date FechaFin);
}