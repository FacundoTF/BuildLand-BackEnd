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

    @ManyToOne
    @JoinColumn(name = "tipoMarca", nullable = false)
    private Marca marca;

    public DetallePresupuesto(Producto producto, double cantidad, Marca marca, Presupuesto presupuesto) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.presupuesto = presupuesto;
        this.marca = marca;
    }

    public DetallePresupuesto(Producto producto, double cantidad, Marca marca) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.presupuesto = null;
        this.marca = marca;
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

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }
}
