package com.uai.BuildLand.models.reporte.productosSolicitados;

import com.uai.BuildLand.models.Categoria;
import com.uai.BuildLand.models.reporte.Reporte;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class ReporteProductosSolicitados extends Reporte {
    private String categoria;
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
