package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.cotizacion.Cotizacion;
import com.uai.BuildLand.models.cotizacion.CotizacionMensaje;
import com.uai.BuildLand.models.cotizacion.CotizacionResumen;
import com.uai.BuildLand.models.cotizacion.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    @Query(value = "WITH CotizacionesConMonto AS (\n" +
            "    SELECT \n" +
            "        c.id_cotizacion,\n" +
            "        d.cuit,\n" +
            "        d.nombre,\n" +
            "        SUM(cd.precio_unitario) AS monto_total,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY d.cuit ORDER BY SUM(cd.precio_unitario) ASC) AS rn\n" +
            "    FROM dbo.cotizaciones c\n" +
            "    INNER JOIN dbo.distribuidoras d ON c.distribuidora_cuit = d.cuit\n" +
            "    INNER JOIN dbo.cotizaciones_detalles cd ON c.id_cotizacion = cd.cotizacion_id\n" +
            "    WHERE c.presupuesto_id_presupuesto = :idPresupuesto " +
            "    GROUP BY c.id_cotizacion, d.cuit, d.nombre\n" +
            ")\n" +
            "SELECT TOP 3\n" +
            "    id_cotizacion AS idCotizacion,\n" +
            "    cuit,\n" +
            "    nombre,\n" +
            "    monto_total AS montoTotal\n" +
            "FROM CotizacionesConMonto\n" +
            "WHERE rn = 1\n" +
            "ORDER BY monto_total ASC", nativeQuery = true)
    List<CotizacionResumen> findTop3ByPresupuestoIdOrderByImporteTotalDesc(@Param("idPresupuesto") Long idPresupuesto);

    @Query("SELECT dc FROM DetalleCotizacion dc WHERE dc.cotizacion.idCotizacion IN :cotizacionIds")
    List<DetalleCotizacion> findDetallesByCotizacionIds(@Param("cotizacionIds") List<Long> cotizacionIds);

    @Procedure
    @Async
    void sp_NotificarDesplazamiento(Long IdPresupuesto);

    Optional<List<Object>> findByPresupuesto_IdPresupuestoAndDistribuidora_Cuit(Long id, String cuit);

    @Modifying
    @Query("INSERT INTO CotizacionAccionada(cotizacion,accion,mensaje) VALUES(:idCotizacion, 'RECHAZADA', :idMensaje)")
    void rechazarCotizacion(@Param("idCotizacion") Long idCotizacion, @Param("idMensaje") CotizacionMensaje idMensaje);

    @Modifying
    @Query("INSERT INTO CotizacionAccionada(cotizacion,accion,mensaje) VALUES(:idCotizacion, 'ACEPTADA', :mensaje)")
    void aceptarCotizacion(@Param("idCotizacion") Long idCotizacion, @Param("mensaje") CotizacionMensaje mensaje);

    @Query("SELECT ca FROM CotizacionAccionada ca WHERE ca.cotizacion.idCotizacion = :idCotizacion AND ca.accion = 'ACEPTADA'")
    Optional<Object> encontrarCotizacionAceptada(@Param("idCotizacion") Long idCotizacion);

    @Query("SELECT ca FROM CotizacionAccionada ca WHERE ca.cotizacion.idCotizacion = :idCotizacion AND ca.accion = 'RECHAZADA'")
    Optional<Object> encontrarCotizacionRechazada(@Param("idCotizacion") Long idCotizacion);

    @Query("SELECT idMensaje, mensaje FROM CotizacionMensaje")
    List<Object> obtenerMensajesCotizacion();

    @Procedure
    List<Object> sp_ReporteCotizacionesPerdidasGanadas(String DistribuidoraCuit, Date FechaInicio, Date FechaFin);

    @Procedure
    List<Object> sp_ReporteMensajeCotizacionesRechazadasGral(String DistribuidoraCuit, Date FechaInicio, Date FechaFin);

    @Procedure
    List<Object> sp_ReporteMensajeCotizacionesRechazadas(String DistribuidoraCuit, Date FechaInicio, Date FechaFin);

    List<Cotizacion> findByDistribuidora(Distribuidora distribuidora);

    List<Cotizacion> findByDistribuidora_Cuit(String distribuidoraCuit);
}
