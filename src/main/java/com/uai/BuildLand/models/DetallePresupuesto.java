package com.uai.BuildLand.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "presupuestos_detalles")
public class DetallePresupuesto {
    @Id
    @Column(name = "id_detalle_presupuesto", nullable = false, updatable = false)
    private Long idDetallePresupuesto;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private double cantidad;

    @ManyToOne
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Presupuesto presupuesto;

    public DetallePresupuesto(Producto producto, double cantidad, Presupuesto presupuesto) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.presupuesto = presupuesto;
    }

    public DetallePresupuesto() {

    }

    public Producto getProducto() {
        return producto;
    }

    public Presupuesto getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}
