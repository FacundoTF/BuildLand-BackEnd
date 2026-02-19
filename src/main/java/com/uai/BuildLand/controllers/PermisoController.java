package com.uai.BuildLand.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.uai.BuildLand.models.access.Permiso;
import com.uai.BuildLand.repositories.PermisoRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Controlador REST para manejar las operaciones CRUD de permisos
 */
@RestController
@RequestMapping("api/permisos")
@CrossOrigin(origins = "*")
public class PermisoController {
    private final PermisoRepository permisoRepository;

    public PermisoController(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    /**
     * Obtiene un permiso por ID
     * @param id ID del permiso
     * @return Permiso encontrado o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Permiso> obtenerPorId(@PathVariable Long id) {
        Long nonNullId = Objects.requireNonNull(id, "id must not be null");
        Optional<Permiso> permiso = permisoRepository.findById(nonNullId);
        if (permiso.isPresent()) {
            return ResponseEntity.ok(permiso.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo permiso
     * @param permiso Permiso a crear
     * @param ucb UriComponentsBuilder para construir la URI de la respuesta
     * @return Permiso creado con status 201
     */
    @PostMapping
    public ResponseEntity<Permiso> crear(@RequestBody Permiso permiso, UriComponentsBuilder ucb) {
        
        Optional<Permiso> perm = Optional.ofNullable(permiso);

        if (perm.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (permiso.getNombre() == null || permiso.getNombre().trim().isEmpty() || permiso.getNombre().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        Permiso permisoGuardado = permisoRepository.save(permiso);

        URI uri = ucb.path("/permisos/{id}").buildAndExpand(permisoGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(permisoGuardado);
    }

    /**
     * Actualiza un permiso existente
     * @param permiso Datos actualizados
     * @return Permiso actualizado o 404 si no existe
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Permiso> actualizar(@RequestBody Permiso permiso) {
        Long nonNullId = Objects.requireNonNull(permiso.getId(), "id must not be null");
        Optional<Permiso> permisoDesactualizado = permisoRepository.findById(nonNullId);
        if (permisoDesactualizado.isPresent()) {
            permisoRepository.save(permiso);
            return ResponseEntity.ok(permiso);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un permiso por ID
     * @param id ID del permiso a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existía
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long nonNullId = Objects.requireNonNull(id, "id must not be null");

        boolean permisoExiste = permisoRepository.existsById(nonNullId);

        if (permisoExiste) {
            permisoRepository.deleteById(nonNullId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos los permisos con paginación
     * @param pageable Parámetros de paginación y ordenamiento
     * @return Lista de permisos
     */
    @GetMapping("/enlistar")
    public ResponseEntity<List<Permiso>> enlistar(Pageable pageable) {
        Page<Permiso> permisos = permisoRepository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "nombre"))));
        return ResponseEntity.ok(permisos.getContent());
    }
}
