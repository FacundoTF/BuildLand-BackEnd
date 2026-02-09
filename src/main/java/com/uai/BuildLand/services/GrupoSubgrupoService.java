//package com.uai.BuildLand.services;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.uai.BuildLand.models.access.Grupo;
//import com.uai.BuildLand.models.access.GrupoSubgrupo;
//import com.uai.BuildLand.repositories.GrupoRepository;
//import com.uai.BuildLand.repositories.GrupoSubgrupoRepository;
//
///**
// * Servicio para manejar la lógica de relaciones entre grupos
// * Previene referencias circulares mediante validaciones
// */
//@Service
//public class GrupoSubgrupoService {
//
//    @Autowired
//    private GrupoSubgrupoRepository grupoSubgrupoRepository;
//
//    @Autowired
//    private GrupoRepository grupoRepository;
//
//    /**
//     * Agrega un subgrupo a un grupo padre con validación de circularidad
//     *
//     * @param grupoPadreId ID del grupo padre
//     * @param grupoHijoId ID del grupo que será subgrupo
//     * @return La relación creada
//     * @throws IllegalArgumentException si la relación sería circular
//     */
//    @Transactional
//    public GrupoSubgrupo agregarSubgrupo(Long grupoPadreId, Long grupoHijoId) {
//        if (grupoPadreId.equals(grupoHijoId)) {
//            throw new IllegalArgumentException("Un grupo no puede ser subgrupo de sí mismo");
//        }
//
//        // Verificar si ya existe una relación inversa (grupoHijo contiene a grupoPadre)
//        if (grupoSubgrupoRepository.existsByGrupoPadreIdAndGrupoHijoId(grupoHijoId, grupoPadreId)) {
//            throw new IllegalArgumentException(
//                "No se puede crear esta relación: el grupo hijo ya contiene al grupo padre. " +
//                "Esto crearía una referencia circular."
//            );
//        }
//
//        Grupo grupoPadre = grupoRepository.findById(grupoPadreId)
//            .orElseThrow(() -> new IllegalArgumentException("Grupo padre no encontrado"));
//        Grupo grupoHijo = grupoRepository.findById(grupoHijoId)
//            .orElseThrow(() -> new IllegalArgumentException("Grupo hijo no encontrado"));
//
//        GrupoSubgrupo relacion = new GrupoSubgrupo(grupoPadre, grupoHijo);
//        return grupoSubgrupoRepository.save(relacion);
//    }
//
//    /**
//     * Elimina una relación subgrupo
//     *
//     * @param relacionId ID de la relación a eliminar
//     */
//    @Transactional
//    public void eliminarSubgrupo(Long relacionId) {
//        grupoSubgrupoRepository.deleteById(relacionId);
//    }
//}
