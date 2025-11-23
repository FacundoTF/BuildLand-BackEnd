package com.uai.BuildLand.models.cotizacion;

import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.presupuesto.Presupuesto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cotizaciones")
public class Cotizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion")
    private Long idCotizacion;

    @OneToMany(mappedBy = "idDetalleCotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCotizacion> detalles;

    @ManyToOne
    private Distribuidora distribuidora;

    @Column(name = "fecha")
    private Date fecha;

    @ManyToOne
    private Presupuesto presupuesto;

    public Cotizacion(List<DetalleCotizacion> detalles, Distribuidora distribuidora, Date fecha,
            Presupuesto presupuesto) {
        this.detalles = detalles;
        this.distribuidora = distribuidora;
        this.fecha = fecha;
        this.presupuesto = presupuesto;
    }

    public Cotizacion() {

    }

    public Long getId() {
        return idCotizacion;
    }

    public void setId(Long id) {
        this.idCotizacion = id;
    }

    public Presupuesto getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
    }

    public List<DetalleCotizacion> getDetalles() {
        return detalles;
    }

    public Distribuidora getDistribuidora() {
        return distribuidora;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setDetalles(List<DetalleCotizacion> detalles) {
        this.detalles = detalles;
    }

    public void setDistribuidora(Distribuidora distribuidora) {
        this.distribuidora = distribuidora;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
