package com.uai.BuildLand.models.reporte.productosSolicitados;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Setter @Getter
public class ReporteProductosSolicitadosResponse {
    private List<ReporteProductosSolicitadosDatos> datos;
}
