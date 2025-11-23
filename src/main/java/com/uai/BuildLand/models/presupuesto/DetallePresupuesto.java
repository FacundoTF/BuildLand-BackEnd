package com.uai.BuildLand.models.presupuesto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.uai.BuildLand.models.Producto;
import jakarta.persistence.*;

@Entity
@Table(name = "presupuestos_detalles")
public class DetallePresupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_presupuesto", nullable = false, updatable = false)
    private Long idDetallePresupuesto;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private double cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    @JsonBackReference
    private Presupuesto presupuesto;

    public DetallePresupuesto(Producto producto, double cantidad, Presupuesto presupuesto) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.presupuesto = presupuesto;
    }

    public DetallePresupuesto(Producto producto, double cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.presupuesto = null;
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
