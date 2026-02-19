package com.uai.BuildLand.models.cotizacion;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cotizaciones_accionadas", schema = "dbo")
public class CotizacionAccionada {
    @Id
    @ManyToOne
    private Cotizacion cotizacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "accion", nullable = false)
    private Acciones accion;

    @ManyToOne
    private CotizacionMensaje mensaje;

    public enum Acciones {
        ACEPTADA,
        RECHAZADA
    }
}
