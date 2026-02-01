package com.uai.BuildLand.models.reporte.productosSolicitados;

import com.uai.BuildLand.models.Categoria;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class ReporteProductosSolicitados {
    private String categoria;
    private Date fechaInicio;
    private Date fechaFin;

    public String getCategoria() {
        return categoria;
    }
    public Date getFechaInicio() {
        return fechaInicio;
    }
    public Date getFechaFin() {
        return fechaFin;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
}
