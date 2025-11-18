package com.uai.BuildLand.models;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "cotizaciones_detalles")
public class DetalleCotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_cotizacion", nullable = false, updatable = false)
    private Long idDetalleCotizacion;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;

    @ManyToOne
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    public DetalleCotizacion(Producto producto, double precioUnitario, Cotizacion cotizacion) {
        this.producto = producto;
        this.precioUnitario = precioUnitario;
        this.cotizacion = cotizacion;
    }

    public DetalleCotizacion() {

    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }
}
