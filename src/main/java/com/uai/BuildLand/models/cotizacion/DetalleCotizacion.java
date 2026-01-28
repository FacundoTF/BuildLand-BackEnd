package com.uai.BuildLand.models.cotizacion;

import com.uai.BuildLand.models.Producto;
import com.uai.BuildLand.models.ProductoMarca;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cotizaciones_detalles")
@Setter @Getter
public class DetalleCotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_cotizacion", nullable = false, updatable = false)
    private Long idDetalleCotizacion;

//    @OneToOne
    @ManyToOne
    @JoinColumn(name = "id_producto_marca", nullable = false)
    private ProductoMarca productoMarca;

    @Column(name = "precio_unitario", nullable = false)
    private double importe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    public DetalleCotizacion(ProductoMarca productoMarca, double importe, Cotizacion cotizacion) {
        this.productoMarca = productoMarca;
        this.importe = importe;
        this.cotizacion = cotizacion;
    }

    public DetalleCotizacion() {

    }
}
