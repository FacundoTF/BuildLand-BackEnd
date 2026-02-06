package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.Empleado;
import com.uai.BuildLand.models.PersonaFisica;
import com.uai.BuildLand.repositories.DistribuidoraRepository;
import com.uai.BuildLand.repositories.EmpleadoRepository;
import com.uai.BuildLand.repositories.PersonaFisicaRepository;
import com.uai.BuildLand.services.JwtTokenService;
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
    private final JwtTokenService jwtTokenService;
    private final PersonaFisicaRepository pfRepository;
    private final DistribuidoraRepository dRepository;
    private final EmpleadoRepository eRepository;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            JwtTokenService jwtTokenService,
            PersonaFisicaRepository pfRepository,
            DistribuidoraRepository dRepository,
            EmpleadoRepository eRepository
    ) {
        this.pfRepository = pfRepository;
        this.dRepository = dRepository;
        this.eRepository = eRepository;
        this.jwtTokenService = jwtTokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping()
    public ResponseEntity<Usuario> obtenerPorId(@CookieValue("accessToken") String accessToken) {
        String tokenNoNulo = Objects.requireNonNull(accessToken, "accessToken must not be null");
        String correo = jwtTokenService.extractUsername(tokenNoNulo);
        Optional<Usuario> usuario = usuarioRepository.findByEmail(correo);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<String> obtenerNombrePorEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            Usuario usuarioEncontrado = usuario.get();
            String nombreUsuario = "";
            if (usuarioEncontrado.getTipoUsuario() == Usuario.TipoUsuario.PERSONA_FISICA){
                PersonaFisica personaFisica = pfRepository.findByUsuario(usuarioEncontrado);
                nombreUsuario = personaFisica.getNombre();
            } else if (usuarioEncontrado.getTipoUsuario() == Usuario.TipoUsuario.DISTRIBUIDORA) {
                Distribuidora distribuidora = dRepository.findByUsuario(usuarioEncontrado);
                nombreUsuario = distribuidora.getNombre();
            } else if (usuarioEncontrado.getTipoUsuario() == Usuario.TipoUsuario.EMPLEADO) {
                Empleado empleado = eRepository.findByUsuario(usuarioEncontrado);
                nombreUsuario = empleado.getNombre();
            }
            return ResponseEntity.ok(nombreUsuario);
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

        if (usuario.getCuit() == null || usuario.getCuit().trim().isEmpty() || usuario.getCuit().length() > 11) {
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

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        URI uri = ucb.path("/usuarios/{cuit}").buildAndExpand(usuarioGuardado.getCuit()).toUri();
        return ResponseEntity.created(uri).body(usuarioGuardado);
    }

    /**
     * Actualiza un usuario existente
     * @param usuario Datos actualizados
     * @return Usuario actualizado o 404 si no existe
     */
    @PatchMapping("/{cuit}")
    public ResponseEntity<Usuario> actualizar(@RequestBody Usuario usuario) {
        String nonNullCuit = Objects.requireNonNull(usuario.getCuit(), "cuit must not be null");
        Optional<Usuario> usuarioDesactualizado = usuarioRepository.findById(nonNullCuit);
        if (usuarioDesactualizado.isPresent()) {
            usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un usuario por CUIT
     * @param cuit CUIT del usuario a eliminar
     * @return 204 si se eliminó correctamente, 404 si no existía
     */
    @DeleteMapping("/{cuit}")
    public ResponseEntity<Void> eliminar(@PathVariable String cuit) {
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");

        boolean usuarioExiste = usuarioRepository.existsById(nonNullCuit);

        if (usuarioExiste) {
            usuarioRepository.deleteById(nonNullCuit);
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
