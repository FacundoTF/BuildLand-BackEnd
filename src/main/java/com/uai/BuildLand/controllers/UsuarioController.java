package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.ActoresSistema;
import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.Empleado;
import com.uai.BuildLand.models.PersonaFisica;
import com.uai.BuildLand.models.access.ConjuntoGrupoPermiso;
import com.uai.BuildLand.models.access.Grupo;
import com.uai.BuildLand.models.cotizacion.Cotizacion;
import com.uai.BuildLand.models.dto.TipoUsuarioDatosDto;
import com.uai.BuildLand.models.dto.VerificationCodeDto;
import com.uai.BuildLand.models.presupuesto.Presupuesto;
import com.uai.BuildLand.repositories.*;
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
    private final PresupuestoRepository prRepository;
    private final CotizacionRepository ctRepository;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            JwtTokenService jwtTokenService,
            PersonaFisicaRepository pfRepository,
            DistribuidoraRepository dRepository,
            EmpleadoRepository eRepository,
            PresupuestoRepository prRepository,
            CotizacionRepository ctRepository
    ) {
        this.pfRepository = pfRepository;
        this.dRepository = dRepository;
        this.eRepository = eRepository;
        this.jwtTokenService = jwtTokenService;
        this.usuarioRepository = usuarioRepository;
        this.prRepository = prRepository;
        this.ctRepository = ctRepository;

    }

    @GetMapping()
    public ResponseEntity<Usuario> obtenerPorId(@CookieValue("accessToken") String accessToken) {
        String tokenNoNulo = Objects.requireNonNull(accessToken, "accessToken must not be null");
        String correo = jwtTokenService.extractUsername(tokenNoNulo);
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(correo);
        if (usuarioOptional.isPresent()) {
            List<ConjuntoGrupoPermiso> conjunto = usuarioOptional.get().getConjunto();
            Usuario usuario = usuarioOptional.get();
            for (ConjuntoGrupoPermiso c : conjunto) {
                if (c instanceof Grupo && c.getNombre().equals("ADMIN")) {
                    usuario.getConjunto().remove(c);
                    break;
                }
            }
            if (usuario.getTipoUsuario() == Usuario.TipoUsuario.EMPLEADO){
                for (ConjuntoGrupoPermiso c : conjunto) {
                    if (c instanceof Grupo && c.getNombre().equals("EMPLEADOS")) {
                        usuario.getConjunto().remove(c);
                        break;
                    }
                }
            } else if (usuario.getTipoUsuario() == Usuario.TipoUsuario.PERSONA_FISICA) {
                for (ConjuntoGrupoPermiso c : conjunto) {
                    if (c instanceof Grupo && c.getNombre().equals("PERSONA FISICA")) {
                        usuario.getConjunto().remove(c);
                        break;
                    }
                }
            } else if (usuario.getTipoUsuario() == Usuario.TipoUsuario.DISTRIBUIDORA) {
                for (ConjuntoGrupoPermiso c : conjunto) {
                    if (c instanceof Grupo && c.getNombre().equals("DISTRIBUIDORAS")) {
                        usuario.getConjunto().remove(c);
                        break;
                    }
                }
            }
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cuit/{cuit}")
    public ResponseEntity<Usuario> obtenerPorCuit(@PathVariable String cuit) {
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");
        Optional<Usuario> usuario = usuarioRepository.findByCuit(nonNullCuit);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ActoresSistema> obtenerNombrePorEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            Usuario usuarioEncontrado = usuario.get();
            List<ConjuntoGrupoPermiso> conjunto = usuarioEncontrado.getConjunto();
            if (usuarioEncontrado.getTipoUsuario() == Usuario.TipoUsuario.PERSONA_FISICA){
                PersonaFisica personaFisica = pfRepository.findByUsuario(usuarioEncontrado);
                for (ConjuntoGrupoPermiso c : conjunto) {
                    if (c instanceof Grupo && (c.getNombre().equals("PERSONA FISICA") || c.getNombre().equals("ADMIN"))) {
                        personaFisica.getUsuario().getConjunto().remove(c);
                        break;
                    }
                }
                return ResponseEntity.ok(personaFisica);
            } else if (usuarioEncontrado.getTipoUsuario() == Usuario.TipoUsuario.DISTRIBUIDORA) {
                Distribuidora distribuidora = dRepository.findByUsuario(usuarioEncontrado);
                for (ConjuntoGrupoPermiso c : conjunto) {
                    if (c instanceof Grupo && (c.getNombre().equals("DISTRIBUIDORAS") || c.getNombre().equals("ADMIN"))) {
                        distribuidora.getUsuario().getConjunto().remove(c);
                        break;
                    }
                }
                return ResponseEntity.ok(distribuidora);
            } else if (usuarioEncontrado.getTipoUsuario() == Usuario.TipoUsuario.EMPLEADO) {
                Empleado empleado = eRepository.findByUsuario(usuarioEncontrado);
                for (ConjuntoGrupoPermiso c : conjunto) {
                    if (c instanceof Grupo && (c.getNombre().equals("EMPLEADOS") || c.getNombre().equals("ADMIN"))) {
                        empleado.getUsuario().getConjunto().remove(c);
                        break;
                    }
                }
                return ResponseEntity.ok(empleado);
            }
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ActoresSistema> crear(@RequestBody TipoUsuarioDatosDto dto, UriComponentsBuilder ucb) {
        
        Optional<Usuario> user = Optional.ofNullable(dto.getUsuario());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Usuario userObtenido = user.get();
        if (userObtenido.getCuit() == null || userObtenido.getCuit().trim().isEmpty() || userObtenido.getCuit().length() > 11) {
            return ResponseEntity.badRequest().build();
        }

        if (userObtenido.getTipoUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (userObtenido.getEmail() == null || userObtenido.getEmail().trim().isEmpty() || userObtenido.getEmail().length() > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (userObtenido.getPassword() == null || userObtenido.getPassword().trim().isEmpty() || userObtenido.getPassword().length() > 255) {
            return ResponseEntity.badRequest().build();
        }
        Usuario usuarioGuardado = usuarioRepository.save(userObtenido);
        if (usuarioGuardado.getCuit().isEmpty() || usuarioGuardado.getCuit().isBlank()){
            return ResponseEntity.badRequest().build();
        }
        Usuario.TipoUsuario tipoUsuario = usuarioGuardado.getTipoUsuario();
        switch (tipoUsuario) {
            case EMPLEADO -> {
                Empleado empleado = new Empleado(
                        usuarioGuardado,
                        null,
                        dto.getNombre(),
                        dto.getNumeroTelefono(),
                        dto.getDireccion(),
                        dto.getCodigoPostal(),
                        dto.getCiudad()
                );
                eRepository.save(empleado);
                URI uri = ucb.path("/usuarios/{cuit}").buildAndExpand(usuarioGuardado.getCuit()).toUri();
                return ResponseEntity.created(uri).body(empleado);
            }
            case DISTRIBUIDORA -> {
                Distribuidora distribuidora = new Distribuidora(
                        usuarioGuardado,
                        null,
                        dto.getNombre(),
                        dto.getDireccion(),
                        dto.getNumeroTelefono(),
                        dto.getDescripcion(),
                        dto.getCodigoPostal(),
                        dto.getCiudad(),
                        dto.getCategorias()
                );
                dRepository.save(distribuidora);
                URI uri = ucb.path("/usuarios/{cuit}").buildAndExpand(usuarioGuardado.getCuit()).toUri();
                return ResponseEntity.created(uri).body(distribuidora);
            }
            case PERSONA_FISICA -> {
                PersonaFisica personaFisica = new PersonaFisica(
                        usuarioGuardado,
                        null,
                        dto.getNombre(),
                        dto.getNumeroTelefono(),
                        dto.getCodigoPostal(),
                        dto.getCiudad()
                );
                pfRepository.save(personaFisica);
                URI uri = ucb.path("/usuarios/{cuit}").buildAndExpand(usuarioGuardado.getCuit()).toUri();
                return ResponseEntity.created(uri).body(personaFisica);
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }


    @PatchMapping
    public ResponseEntity<ActoresSistema> actualizar(@RequestBody TipoUsuarioDatosDto dto) {
        String nonNullCuit = Objects.requireNonNull(dto.getUsuario().getCuit(), "cuit must not be null");
        Optional<Usuario> usuarioDesactualizado = usuarioRepository.findById(nonNullCuit);

        if (usuarioDesactualizado.isPresent()) {
            Usuario.TipoUsuario tipoUsuario = usuarioDesactualizado.get().getTipoUsuario();
            ActoresSistema usuarioRol = null;
            switch (tipoUsuario){
                case EMPLEADO -> {
                    Empleado empleado = eRepository.findByUsuario(usuarioDesactualizado.get());
                    List<ConjuntoGrupoPermiso> lista = empleado.getUsuario().getConjunto();
                    for (ConjuntoGrupoPermiso c : lista) {
                        if (c instanceof Grupo && (c.getNombre().equals("EMPLEADOS") || c.getNombre().equals("ADMIN"))) {
                            dto.getUsuario().getConjunto().add(c);
                            break;
                        }
                    }
                    empleado.setNombre(dto.getNombre());
                    empleado.setNumeroTelefono(dto.getNumeroTelefono());
                    empleado.setCiudad(dto.getCiudad());
                    empleado.setCodigoPostal(dto.getCodigoPostal());
                    empleado.setDireccion(dto.getDireccion());
                    usuarioRol = eRepository.save(empleado);
                }
                case DISTRIBUIDORA -> {
                    Distribuidora distribuidora = dRepository.findByUsuario(usuarioDesactualizado.get());
                    List<ConjuntoGrupoPermiso> lista = distribuidora.getUsuario().getConjunto();
                    for (ConjuntoGrupoPermiso c : lista) {
                        if (c instanceof Grupo && (c.getNombre().equals("DISTRIBUIDORAS") || c.getNombre().equals("ADMIN"))) {
                            dto.getUsuario().getConjunto().add(c);
                            break;
                        }
                    }
                    distribuidora.setNombre(dto.getNombre());
                    distribuidora.setNumeroTelefono(dto.getNumeroTelefono());
                    distribuidora.setCiudad(dto.getCiudad());
                    distribuidora.setCodigoPostal(dto.getCodigoPostal());
                    distribuidora.setDireccion(dto.getDireccion());
                    distribuidora.setDescripcion(dto.getDescripcion());
                    distribuidora.setCategorias(dto.getCategorias());
                    usuarioRol = dRepository.save(distribuidora);
                }
                case PERSONA_FISICA -> {
                    PersonaFisica personaFisica = pfRepository.findByUsuario(usuarioDesactualizado.get());
                    List<ConjuntoGrupoPermiso> lista = personaFisica.getUsuario().getConjunto();
                    for (ConjuntoGrupoPermiso c : lista) {
                        if (c instanceof Grupo && (c.getNombre().equals("PERSONA FISICA") || c.getNombre().equals("ADMIN"))) {
                            dto.getUsuario().getConjunto().add(c);
                            break;
                        }
                    }
                    personaFisica.setNombre(dto.getNombre());
                    personaFisica.setNumeroTelefono(dto.getNumeroTelefono());
                    personaFisica.setCiudad(dto.getCiudad());
                    personaFisica.setCodigoPostal(dto.getCodigoPostal());
                    usuarioRol = pfRepository.save(personaFisica);
                }
            }
            usuarioRepository.save(dto.getUsuario());
            return ResponseEntity.ok(usuarioRol);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/resetear")
    public ResponseEntity<ActoresSistema> resetearPassword(@RequestBody VerificationCodeDto dto) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(dto.getEmail());
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            if (usuario.getEmail().equals(dto.getEmail())) {
                usuario.setPassword(dto.getCode());
                usuarioRepository.save(usuario);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cuit}")
    public ResponseEntity<Void> eliminar(@PathVariable String cuit) {
        String nonNullCuit = Objects.requireNonNull(cuit, "cuit must not be null");

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(nonNullCuit);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            switch (usuario.getTipoUsuario()) {
                case EMPLEADO -> {
                    return ResponseEntity.internalServerError().build();
                }
                case DISTRIBUIDORA -> {
                    Distribuidora distribuidora = dRepository.findByUsuario(usuario);

                    List<Cotizacion> cotizaciones = ctRepository.findByDistribuidora(distribuidora);

                    if (!cotizaciones.isEmpty()){
                        return ResponseEntity.internalServerError().build();
                    }
                    prRepository.eliminarRechazoPresupuesto(distribuidora.getCuit());
                    dRepository.delete(distribuidora);
                }
                case PERSONA_FISICA -> {
                    PersonaFisica personaFisica = pfRepository.findByUsuario(usuario);

                    List<Presupuesto> presupuestos = prRepository.findByPersona(personaFisica);
                    for (Presupuesto presupuesto : presupuestos) {
                        presupuesto.setPersona(new PersonaFisica(usuario.getEstado()));
                    }
                    prRepository.saveAll(presupuestos);

                    pfRepository.delete(personaFisica);
                }
            }
            usuarioRepository.eliminarConjuntoGrupoPermiso(nonNullCuit);
            usuarioRepository.deleteById(nonNullCuit);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enlistar")
    public ResponseEntity<List<Usuario>> enlistar(Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "email"))));
        return ResponseEntity.ok(usuarios.getContent());
    }

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
