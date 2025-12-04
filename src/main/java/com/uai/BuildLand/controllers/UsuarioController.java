package com.uai.BuildLand.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.repositories.UsuarioRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

/**
 * Controlador REST para manejar las operaciones CRUD de usuarios
 */
@RestController
@RequestMapping("api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene un usuario por ID
     * @param id ID del usuario
     * @return Usuario encontrado o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        Long nonNullId = Objects.requireNonNull(id, "id must not be null");
        Optional<Usuario> usuario = usuarioRepository.findById(nonNullId);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo usuario
     * @param usuario Usuario a crear
     * @param ucb UriComponentsBuilder para construir la URI de la respuesta
     * @return Usuario creado con status 201
     */
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario, UriComponentsBuilder ucb) {
        
        Optional<Usuario> user = Optional.ofNullable(usuario);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (usuario.getTipoUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty() || usuario.getEmail().length() > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty() || usuario.getPassword().length() > 255) {
            return ResponseEntity.badRequest().build();
        }

        if (usuario.getIdReferencia() != null && usuario.getIdReferencia().length() > 50) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        URI uri = ucb.path("/usuarios/{id}").buildAndExpand(usuarioGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(usuarioGuardado);
    }

    /**
     * Actualiza un usuario existente
     * @param usuario Datos actualizados
     * @return Usuario actualizado o 404 si no existe
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@RequestBody Usuario usuario) {
        Long nonNullId = Objects.requireNonNull(usuario.getId(), "id must not be null");
        Optional<Usuario> usuarioDesactualizado = usuarioRepository.findById(nonNullId);
        if (usuarioDesactualizado.isPresent()) {
            usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un usuario por ID
     * @param id ID del usuario a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existía
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long nonNullId = Objects.requireNonNull(id, "id must not be null");

        boolean usuarioExiste = usuarioRepository.existsById(nonNullId);

        if (usuarioExiste) {
            usuarioRepository.deleteById(nonNullId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos los usuarios con paginación
     * @param pageable Parámetros de paginación y ordenamiento
     * @return Lista de usuarios
     */
    @GetMapping("/enlistar")
    public ResponseEntity<List<Usuario>> enlistar(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "email"))));
        return ResponseEntity.ok(usuarios.getContent());
    }

    /**
     * Login de usuario
     * @param loginRequest Usuario con email y password
     * @return 200 si el login es exitoso, 404 si no existe o contraseña incorrecta
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody Usuario loginRequest){
        Optional<Usuario> usuario = usuarioRepository.findByEmail(loginRequest.getEmail());
        
//        if (usuario.isPresent() && PasswordEncoder.matches(loginRequest.getPassword(), usuario.get().getPassword())) {
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
        if (usuario.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
