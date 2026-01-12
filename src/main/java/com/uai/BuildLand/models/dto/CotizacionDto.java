package com.uai.BuildLand.models.dto;

import java.util.Date;
import java.util.List;

public class CotizacionDto {
    private Long id;
    private Date fecha;
    private Double importeTotal;
    private Long idDistribuidora;
    private String nombreDistribuidora;
    private String cuitDistribuidora;
    private List<DetalleCotizacionDto> detalles;

    public CotizacionDto() {
    }

    public CotizacionDto(Long id, Date fecha, Double importeTotal, Long idDistribuidora, String nombreDistribuidora, String cuitDistribuidora, List<DetalleCotizacionDto> detalles) {
        this.id = id;
        this.fecha = fecha;
        this.importeTotal = importeTotal;
        this.idDistribuidora = idDistribuidora;
        this.nombreDistribuidora = nombreDistribuidora;
        this.cuitDistribuidora = cuitDistribuidora;
        this.detalles = detalles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(Double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public Long getIdDistribuidora() {
        return idDistribuidora;
    }

    public void setIdDistribuidora(Long idDistribuidora) {
        this.idDistribuidora = idDistribuidora;
    }

    public String getNombreDistribuidora() {
        return nombreDistribuidora;
    }

    public void setNombreDistribuidora(String nombreDistribuidora) {
        this.nombreDistribuidora = nombreDistribuidora;
    }

    public String getCuitDistribuidora() {
        return cuitDistribuidora;
    }

    public void setCuitDistribuidora(String cuitDistribuidora) {
        this.cuitDistribuidora = cuitDistribuidora;
    }

    public List<DetalleCotizacionDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCotizacionDto> detalles) {
        this.detalles = detalles;
    }
}

