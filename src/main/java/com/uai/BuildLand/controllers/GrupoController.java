package com.uai.BuildLand.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.uai.BuildLand.models.access.ConjuntoGrupoPermiso;
import com.uai.BuildLand.models.access.Grupo;
import com.uai.BuildLand.models.access.Permiso;
import com.uai.BuildLand.repositories.GrupoRepository;
import com.uai.BuildLand.repositories.PermisoRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Controlador REST para manejar las operaciones CRUD de grupos
 */
@RestController
@RequestMapping("api/grupos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GrupoController {
    private final GrupoRepository grupoRepository;
    private final PermisoRepository permisoRepository;

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

        grupo.setId(null);

        Grupo grupoGuardado = grupoRepository.save(grupo);

        URI uri = ucb.path("api/grupos/{id}").buildAndExpand(grupoGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(grupoGuardado);
    }

    /**
     * Actualiza un grupo existente
     * @param grupoDatos Datos a actualizar
     * @return Grupo actualizado o 404 si no existe
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Grupo> actualizar(@PathVariable Long id, @RequestBody Grupo grupoDatos) {
        Optional<Grupo> grupoExistenteOpt = grupoRepository.findById(id);

        if (grupoExistenteOpt.isPresent()) {
            Grupo grupoExistente = grupoExistenteOpt.get();

            if (grupoDatos.getNombre() != null && !grupoDatos.getNombre().isEmpty()) {
                grupoExistente.setNombre(grupoDatos.getNombre());
            }

            if (grupoDatos.getDescripcion() != null) {
                grupoExistente.setDescripcion(grupoDatos.getDescripcion());
            }

            Grupo grupoGuardado = grupoRepository.save(grupoExistente);
            return ResponseEntity.ok(grupoGuardado);
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
        Specification<Grupo> gruposProhibidosSpec = com.uai.BuildLand.controllers.specifications.GrupoSpecs.filtrarGruposProhibidos();
        Page<Grupo> grupos = grupoRepository.findAll(gruposProhibidosSpec, PageRequest.of(
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
     * @return Grupo actualizado o error
     */
    @PostMapping("/{grupoPadreId}/subgrupos/{grupoHijoId}")
    public ResponseEntity<?> agregarSubgrupo(@PathVariable Long grupoPadreId, 
                                              @PathVariable Long grupoHijoId) {
        if (grupoPadreId.equals(grupoHijoId)) {
            return ResponseEntity.badRequest().body("Un grupo no puede ser subgrupo de sí mismo");
        }

        Optional<Grupo> padreOpt = grupoRepository.findById(grupoPadreId);
        Optional<Grupo> hijoOpt = grupoRepository.findById(grupoHijoId);

        if (padreOpt.isEmpty() || hijoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Grupo padre = padreOpt.get();
        Grupo hijo = hijoOpt.get();

        // Verificar circularidad
        if (esSubgrupo(hijo, padre)) {
            return ResponseEntity.badRequest().body("Referencia circular detectada: el grupo hijo ya contiene al padre.");
        }

        padre.agregarElemento(hijo);
        grupoRepository.save(padre);

        return ResponseEntity.ok(padre);
    }

    // DFS para verificar si 'buscado' es subgrupo (directo o indirecto) de 'inicio'
    private boolean esSubgrupo(Grupo inicio, Grupo buscado) {
        for (ConjuntoGrupoPermiso elemento : inicio.getElementos()) {
            if (elemento.getId().equals(buscado.getId())) {
                return true;
            }
            if (elemento instanceof Grupo) {
                if (esSubgrupo((Grupo) elemento, buscado)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Elimina un subgrupo de un grupo padre
     *
     * @param grupoPadreId ID del grupo padre
     * @param grupoHijoId ID del grupo hijo a remover
     * @return 204 si se eliminó correctamente
     */
    @DeleteMapping("/{grupoPadreId}/subgrupos/{grupoHijoId}")
    public ResponseEntity<Void> eliminarSubgrupo(@PathVariable Long grupoPadreId, @PathVariable Long grupoHijoId) {
        Optional<Grupo> padreOpt = grupoRepository.findById(grupoPadreId);
        Optional<Grupo> hijoOpt = grupoRepository.findById(grupoHijoId);

        if (padreOpt.isEmpty() || hijoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Grupo padre = padreOpt.get();
        Grupo hijo = hijoOpt.get();

        padre.removerElemento(hijo);
        grupoRepository.save(padre);

        return ResponseEntity.noContent().build();
    }

    /**
     * Agrega un permiso a un grupo
     *
     * @param grupoId ID del grupo
     * @param permisoId ID del permiso a agregar
     * @return Grupo actualizado
     */
    @PostMapping("/{grupoId}/permisos/{permisoId}")
    public ResponseEntity<Grupo> agregarPermiso(@PathVariable Long grupoId, @PathVariable Long permisoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        Optional<Permiso> permisoOpt = permisoRepository.findById(permisoId);

        if (grupoOpt.isEmpty() || permisoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Grupo grupo = grupoOpt.get();
        Permiso permiso = permisoOpt.get();

        grupo.agregarElemento(permiso);
        Grupo grupoActualizado = grupoRepository.save(grupo);

        return ResponseEntity.ok(grupoActualizado);
    }

    /**
     * Elimina un permiso de un grupo
     *
     * @param grupoId ID del grupo
     * @param permisoId ID del permiso a remover
     * @return 204 si se eliminó correctamente
     */
    @DeleteMapping("/{grupoId}/permisos/{permisoId}")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long grupoId, @PathVariable Long permisoId) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(grupoId);
        Optional<Permiso> permisoOpt = permisoRepository.findById(permisoId);

        if (grupoOpt.isEmpty() || permisoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Grupo grupo = grupoOpt.get();
        Permiso permiso = permisoOpt.get();

        grupo.removerElemento(permiso);
        grupoRepository.save(grupo);

        return ResponseEntity.noContent().build();
    }
}
