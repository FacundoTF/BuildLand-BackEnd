package com.uai.BuildLand.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.uai.BuildLand.models.access.Grupo;
import com.uai.BuildLand.models.access.GrupoSubgrupo;
import com.uai.BuildLand.repositories.GrupoRepository;
import com.uai.BuildLand.services.GrupoSubgrupoService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Controlador REST para manejar las operaciones CRUD de grupos
 */
@RestController
@RequestMapping("/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {
    private final GrupoRepository grupoRepository;
    private final GrupoSubgrupoService grupoSubgrupoService;

    public GrupoController(GrupoRepository grupoRepository, GrupoSubgrupoService grupoSubgrupoService) {
        this.grupoRepository = grupoRepository;
        this.grupoSubgrupoService = grupoSubgrupoService;
    }

    /**
     * Obtiene un grupo por ID
     * @param id ID del grupo
     * @return Grupo encontrado o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Grupo> obtenerPorId(@PathVariable Long id) {
        Long nonNullId = Objects.requireNonNull(id, "id must not be null");
        Optional<Grupo> grupo = grupoRepository.findById(nonNullId);
        if (grupo.isPresent()) {
            return ResponseEntity.ok(grupo.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo grupo
     * @param grupo Grupo a crear
     * @param ucb UriComponentsBuilder para construir la URI de la respuesta
     * @return Grupo creado con status 201
     */
    @PostMapping
    public ResponseEntity<Grupo> crear(@RequestBody Grupo grupo, UriComponentsBuilder ucb) {
        
        Optional<Grupo> grp = Optional.ofNullable(grupo);

        if (grp.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (grupo.getNombre() == null || grupo.getNombre().trim().isEmpty() || grupo.getNombre().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (grupo.getDescripcion() != null && grupo.getDescripcion().length() > 255) {
            return ResponseEntity.badRequest().build();
        }

        Grupo grupoGuardado = grupoRepository.save(grupo);

        URI uri = ucb.path("/grupos/{id}").buildAndExpand(grupoGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(grupoGuardado);
    }

    /**
     * Actualiza un grupo existente
     * @param grupo Datos actualizados
     * @return Grupo actualizado o 404 si no existe
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Grupo> actualizar(@RequestBody Grupo grupo) {
        Long nonNullId = Objects.requireNonNull(grupo.getId(), "id must not be null");
        Optional<Grupo> grupoDesactualizado = grupoRepository.findById(nonNullId);
        if (grupoDesactualizado.isPresent()) {
            grupoRepository.save(grupo);
            return ResponseEntity.ok(grupo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un grupo por ID
     * @param id ID del grupo a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existía
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long nonNullId = Objects.requireNonNull(id, "id must not be null");

        boolean grupoExiste = grupoRepository.existsById(nonNullId);

        if (grupoExiste) {
            grupoRepository.deleteById(nonNullId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos los grupos con paginación
     * @param pageable Parámetros de paginación y ordenamiento
     * @return Lista de grupos
     */
    @GetMapping("/enlistar")
    public ResponseEntity<List<Grupo>> enlistar(Pageable pageable) {
        Page<Grupo> grupos = grupoRepository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "nombre"))));
        return ResponseEntity.ok(grupos.getContent());
    }

    /**
     * Agrega un subgrupo a un grupo padre
     * Previene referencias circulares validando que el grupo hijo no contenga al grupo padre
     * 
     * @param grupoPadreId ID del grupo padre
     * @param grupoHijoId ID del grupo que será subgrupo
     * @return GrupoSubgrupo creado o error si la operación es inválida
     */
    @PostMapping("/{grupoPadreId}/subgrupos/{grupoHijoId}")
    public ResponseEntity<?> agregarSubgrupo(@PathVariable Long grupoPadreId, 
                                              @PathVariable Long grupoHijoId) {
        try {
            GrupoSubgrupo relacion = grupoSubgrupoService.agregarSubgrupo(grupoPadreId, grupoHijoId);
            return ResponseEntity.status(201).body(relacion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Elimina la relación de subgrupo
     * 
     * @param relacionId ID de la relación a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existe
     */
    @DeleteMapping("/subgrupos/{relacionId}")
    public ResponseEntity<Void> eliminarSubgrupo(@PathVariable Long relacionId) {
        try {
            grupoSubgrupoService.eliminarSubgrupo(relacionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
