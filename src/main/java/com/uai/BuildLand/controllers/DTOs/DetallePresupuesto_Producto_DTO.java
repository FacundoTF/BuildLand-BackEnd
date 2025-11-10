package com.uai.BuildLand.controllers.DTOs;

import com.uai.BuildLand.models.DetallePresupuesto;
import com.uai.BuildLand.models.Producto;

public class DetallePresupuesto_Producto_DTO {
    private DetallePresupuesto detallePresupuesto;
    private Producto producto;

    public void setDetallePresupuesto(DetallePresupuesto detallePresupuesto) {
        this.detallePresupuesto = detallePresupuesto;
    }

    public DetallePresupuesto getDetallePresupuesto() {
        return detallePresupuesto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Producto getProducto() {
        return producto;
    }
}
