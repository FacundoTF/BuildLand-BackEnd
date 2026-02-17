package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.uai.BuildLand.models.Empleado;
import com.uai.BuildLand.repositories.EmpleadoRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Controlador REST para manejar las operaciones CRUD de empleados
 */
@RestController
@RequestMapping("/empleados")
@CrossOrigin(origins = "*")
public class EmpleadoController {
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpleadoController(EmpleadoRepository empleadoRepository, UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Obtiene un empleado por ID (CUIT)
     * @param cuit CUIT del empleado
     * @return Empleado encontrado o 404 si no existe
     */
    @GetMapping("/{cuit}")
    public ResponseEntity<Empleado> obtenerPorId(@PathVariable String cuit) {
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");
        Optional<Empleado> empleado = empleadoRepository.findById(nonNullCuit);
        if (empleado.isPresent()) {
            return ResponseEntity.ok(empleado.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo empleado
     * @param empleado Empleado a crear
     * @param ucb UriComponentsBuilder para construir la URI de la respuesta
     * @return Empleado creado con status 201
     */
    @PostMapping
    public ResponseEntity<Empleado> crear(@RequestBody Empleado empleado, UriComponentsBuilder ucb) {
        
        Optional<Empleado> emp = Optional.ofNullable(empleado);

        if (emp.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (empleado.getCuit() == null || empleado.getCuit().trim().isEmpty() || empleado.getCuit().length() != 11) {
            return ResponseEntity.badRequest().build();
        }

        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty() || empleado.getNombre().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (empleado.getCiudad() == null || empleado.getCiudad().trim().isEmpty() || empleado.getCiudad().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (empleado.getDireccion() == null || empleado.getDireccion().trim().isEmpty() || empleado.getDireccion().length() > 200) {
            return ResponseEntity.badRequest().build();
        }

        if (empleado.getCodigoPostal() == null || empleado.getCodigoPostal().trim().isEmpty() || empleado.getCodigoPostal().length() > 10) {
            return ResponseEntity.badRequest().build();
        }

        if (empleado.getNumeroTelefono() == null || empleado.getNumeroTelefono().trim().isEmpty() || empleado.getNumeroTelefono().length() > 20) {
            return ResponseEntity.badRequest().build();
        }

        Empleado empleadoGuardado = empleadoRepository.save(empleado);

        URI uri = ucb.path("/empleados/{cuit}").buildAndExpand(empleadoGuardado.getUsuario().getCuit()).toUri();
        return ResponseEntity.created(uri).body(empleadoGuardado);
    }

    /**
     * Actualiza un empleado existente
     * @param empleado Datos actualizados
     * @return Empleado actualizado o 404 si no existe
     */
    @PatchMapping("/{cuit}")
    public ResponseEntity<Empleado> actualizar(@RequestBody Empleado empleado) {
        String nonNullCuit = Objects.requireNonNull(empleado.getUsuario().getCuit(), "cuit must not be null");
        Optional<Empleado> empleadoDesactualizado = empleadoRepository.findById(nonNullCuit);
        if (empleadoDesactualizado.isPresent()) {
            empleadoRepository.save(empleado);
            return ResponseEntity.ok(empleado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un empleado por CUIT
     * @param cuit CUIT del empleado a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existía
     */
    @DeleteMapping("/{cuit}")
    public ResponseEntity<Void> eliminar(@PathVariable String cuit) {
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");

        Empleado empleado = empleadoRepository.findById(nonNullCuit).orElseThrow(() -> new IllegalStateException("Empleado no encontrado con cuit: " + nonNullCuit));

        if (empleado != null) {
            empleadoRepository.deleteById(nonNullCuit);
            Optional<Usuario> usuario = usuarioRepository.findByEmail(empleado.getUsuario().getEmail());
            usuario.ifPresent(value -> usuarioRepository.deleteById(value.getCuit()));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos los empleados con paginación
     * @param pageable Parámetros de paginación y ordenamiento
     * @return Lista de empleados
     */
    @GetMapping("/enlistar")
    public ResponseEntity<List<Empleado>> enlistar(Pageable pageable) {
        Page<Empleado> empleados = empleadoRepository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "nombre"))));
        return ResponseEntity.ok(empleados.getContent());
    }
}
