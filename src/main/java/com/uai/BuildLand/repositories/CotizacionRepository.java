package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.cotizacion.Cotizacion;
import com.uai.BuildLand.models.cotizacion.CotizacionResumen;
import com.uai.BuildLand.models.cotizacion.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import java.util.List;

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

    @Query(value = "EXEC dbo.sp_NotificarDesplazamiento :IdPresupuesto, :email", nativeQuery = true)
    @Async
    void notificarDesplazamiento(@Param("IdPresupuesto") Long IdPresupuesto, @Param("email") String email);

}
