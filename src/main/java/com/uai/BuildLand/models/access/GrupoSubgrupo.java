//package com.uai.BuildLand.models.access;
//
//import jakarta.persistence.*;
//
///**
// * Entidad que representa la relación padre-hijo entre grupos
// * Previene referencias circulares (un grupo no puede ser subgrupo de su subgrupo)
// */
//@Entity
//@Table(name = "grupo_subgrupos")
//public class GrupoSubgrupo {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_grupo_padre", nullable = false)
//    private Grupo grupoPadre;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_grupo_hijo", nullable = false)
//    private Grupo grupoHijo;
//
//    // Constructor por defecto
//    public GrupoSubgrupo() {
//    }
//
//    // Constructor con parámetros
//    public GrupoSubgrupo(Grupo grupoPadre, Grupo grupoHijo) {
//        this.grupoPadre = grupoPadre;
//        this.grupoHijo = grupoHijo;
//    }
//
//    // Getters y Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Grupo getGrupoPadre() {
//        return grupoPadre;
//    }
//
//    public void setGrupoPadre(Grupo grupoPadre) {
//        this.grupoPadre = grupoPadre;
//    }
//
//    public Grupo getGrupoHijo() {
//        return grupoHijo;
//    }
//
//    public void setGrupoHijo(Grupo grupoHijo) {
//        this.grupoHijo = grupoHijo;
//    }
//
//    @Override
//    public String toString() {
//        return "GrupoSubgrupo{" +
//                "id=" + id +
//                ", grupoPadre=" + (grupoPadre != null ? grupoPadre.getId() : null) +
//                ", grupoHijo=" + (grupoHijo != null ? grupoHijo.getId() : null) +
//                '}';
//    }
//}
