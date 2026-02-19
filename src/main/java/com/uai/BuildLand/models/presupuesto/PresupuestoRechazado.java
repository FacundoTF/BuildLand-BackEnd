package com.uai.BuildLand.models.presupuesto;

import com.uai.BuildLand.models.Distribuidora;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "presupuestos_rechazados", schema = "dbo")
public class PresupuestoRechazado {
    @EmbeddedId
    private PresupuestoRechazadoId id;

    @MapsId("cuit")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_cuit", nullable = false, columnDefinition = "varchar(11)")
    private Distribuidora cuit;

    @MapsId("idPresupuesto")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_presupuesto", nullable = false)
    private Presupuesto idPresupuesto;


}