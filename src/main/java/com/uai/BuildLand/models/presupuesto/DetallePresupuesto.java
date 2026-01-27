package com.uai.BuildLand.models.presupuesto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.uai.BuildLand.models.ProductoMarca;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "presupuestos_detalles")
@Setter @Getter
public class DetallePresupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_presupuesto", nullable = false, updatable = false)
    private Long idDetallePresupuesto;

    @OneToOne
    @JoinColumn(name = "id_producto_marca", nullable = false)
    private ProductoMarca productoMarca;

    @Column(name = "cantidad", nullable = false)
    private double cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    @JsonBackReference
    private Presupuesto presupuesto;

    public DetallePresupuesto(ProductoMarca productoMarca, double cantidad, Presupuesto presupuesto) {
        this.productoMarca = productoMarca;
        this.cantidad = cantidad;
        this.presupuesto = presupuesto;
    }

    public DetallePresupuesto(ProductoMarca productoMarca, double cantidad) {
        this.productoMarca = productoMarca;
        this.cantidad = cantidad;
        this.presupuesto = null;
    }

    public DetallePresupuesto() {

    }
}
