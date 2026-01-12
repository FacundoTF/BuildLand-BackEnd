package com.uai.BuildLand.models.cotizacion;

import com.uai.BuildLand.models.Producto;
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
    private double importe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    public DetalleCotizacion(Producto producto, double importe, Cotizacion cotizacion) {
        this.producto = producto;
        this.importe = importe;
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

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }
}
