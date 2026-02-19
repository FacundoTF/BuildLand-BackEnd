package com.uai.BuildLand.models.reporte.productosSolicitados;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter @Setter
public class ReporteProductosSolicitadosDatos {
    private Double porcentaje;
    private String producto;
    private String marca;
    private Double cantidad;
}
