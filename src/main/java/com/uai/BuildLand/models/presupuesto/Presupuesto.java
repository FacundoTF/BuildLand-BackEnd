package com.uai.BuildLand.models.presupuesto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.uai.BuildLand.models.Categoria;
import com.uai.BuildLand.models.PersonaFisica;
import com.uai.BuildLand.models.cotizacion.Cotizacion;
import jakarta.persistence.*;

@Entity
@Table(name = "presupuestos")
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presupuesto")
    private Long idPresupuesto;

    @ManyToOne
    private PersonaFisica persona;

    @ManyToOne
    private Categoria categoria;

    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "fechaLimite")
    private Date fechaLimite;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<DetallePresupuesto> detalles;

    @OneToMany(mappedBy = "idCotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cotizacion> cotizaciones;

    public Presupuesto() {
    }

    public Presupuesto(List<DetallePresupuesto> detalles, List<Cotizacion> cotizaciones, Date fecha,
            PersonaFisica persona, Categoria categoria, Long idPresupuesto, Date fechaLimite) {
        this.fechaLimite = fechaLimite;
        this.detalles = detalles;
        this.cotizaciones = cotizaciones;
        this.fecha = fecha;
        this.persona = persona;
        this.categoria = categoria;
        this.idPresupuesto = idPresupuesto;
    }

    public Long getId() {
        return idPresupuesto;
    }

    public void setId(Long idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public PersonaFisica getPersona() {
        return persona;
    }

    public void setPersona(PersonaFisica persona) {
        this.persona = persona;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public List<DetallePresupuesto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePresupuesto> detalles) {
        this.detalles = detalles;
    }

    public List<Cotizacion> getCotizaciones() {
        return cotizaciones;
    }

    public void setCotizaciones(List<Cotizacion> cotizaciones) {
        this.cotizaciones = cotizaciones;
    }
}
