package com.uai.BuildLand.controllers;

import com.uai.BuildLand.controllers.specifications.PresupuestoSpecs;
import com.uai.BuildLand.models.Categoria;
import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.PersonaFisica;
import com.uai.BuildLand.models.ProductoMarca;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.dto.PaginationResultDto;
import com.uai.BuildLand.models.presupuesto.DetallePresupuesto;
import com.uai.BuildLand.models.presupuesto.Presupuesto;
import com.uai.BuildLand.repositories.*;
import com.uai.BuildLand.services.EmailService;
import com.uai.BuildLand.services.JwtTokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/presupuestos")
@CrossOrigin(origins = "*")
public class PresupuestoController {

    private final PresupuestoRepository presupuestoRepository;
    private final ProductoMarcaRepository productoMarcaRepository;
    private final EmailService emailService;
    private final DistribuidoraRepository distribuidoraRepository;
    private final JwtTokenService jwtTokenService;
    private final UsuarioRepository usuarioRepository;
    private final DetallePresupuestoRepository detallePresupuestoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final PersonaFisicaRepository personaFisicaRepository;

    public PresupuestoController(PresupuestoRepository presupuestoRepository, EmailService emailService, DistribuidoraRepository distribuidoraRepository, JwtTokenService jwtTokenService, UsuarioRepository usuarioRepository, DetallePresupuestoRepository detallePresupuestoRepository, ProductoMarcaRepository productoMarcaRepository, CotizacionRepository cotizacionRepository, PersonaFisicaRepository personaFisicaRepository) {
        this.productoMarcaRepository = productoMarcaRepository;
        this.detallePresupuestoRepository = detallePresupuestoRepository;
        this.jwtTokenService = jwtTokenService;
        this.presupuestoRepository = presupuestoRepository;
        this.emailService = emailService;
        this.distribuidoraRepository = distribuidoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.personaFisicaRepository = personaFisicaRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presupuesto> obtenerPorId(@PathVariable Long id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);
        return presupuesto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Presupuesto> crear(@RequestBody Presupuesto presupuesto, UriComponentsBuilder ucb, @CookieValue (name = "accessToken") String jwtToken) {
        String correo = jwtTokenService.extractUsername(jwtToken);
        Optional<Usuario> usuario = usuarioRepository.findByEmail(correo);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        Usuario usuarioEncontrado = usuario.get();

        Optional<PersonaFisica> personaFisicaOpt = Optional.ofNullable(personaFisicaRepository.findByUsuario(usuarioEncontrado));
        if (personaFisicaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PersonaFisica personaFisica = personaFisicaOpt.get();

        presupuesto.setPersona(personaFisica);

        if (presupuesto.getPersona() == null || presupuesto.getCategoria() == null) {
            return ResponseEntity.badRequest().build();
        }


        for (DetallePresupuesto detalle : presupuesto.getDetalles()) {
            if (detalle.getProductoMarca() == null) {
                return ResponseEntity.badRequest().build();
            }else {
                Optional<ProductoMarca> productoMarca = productoMarcaRepository.findAllByMarca_NombreAndProducto_Nombre(
                        detalle.getProductoMarca().getMarca().getNombre(),
                        detalle.getProductoMarca().getProducto().getNombre()
                );
                if (productoMarca.isEmpty()){
                    return ResponseEntity.badRequest().build();
                }
                detalle.setProductoMarca(productoMarca.get());
            }
            detalle.setPresupuesto(presupuesto);
        }

        presupuesto.setFecha(new Date());

        Date fechaVencimiento = new Date();
        fechaVencimiento.setTime(presupuesto.getFecha().getTime() + (7 * 24 * 60 * 60 * 1000));
//        fechaVencimiento.setTime(presupuesto.getFecha().getTime() + (3 * 60 * 1000)); // 3 minutos para pruebas
        presupuesto.setFechaLimite(fechaVencimiento);

        presupuesto.setCotizaciones(new ArrayList<>());

        Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);

        // Enviar email con los detalles del presupuesto
        try {
            emailService.sendPresupuestoMail(presupuestoGuardado);
        } catch (Exception e) {
            System.err.println("Error al enviar email de presupuesto: " + e.getMessage());
        }

        URI uri = ucb.path("api/presupuestos/{id}").buildAndExpand(presupuestoGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(presupuestoGuardado);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Presupuesto> actualizar(@PathVariable Long id, @RequestBody Presupuesto presupuestoActualizado) {
        Optional<Presupuesto> presupuestoExistenteOpt = presupuestoRepository.findById(id);
        if (presupuestoExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Presupuesto presupuestoExistente = presupuestoExistenteOpt.get();

        if (presupuestoActualizado.getPersona() != null) {
            presupuestoExistente.setPersona(presupuestoActualizado.getPersona());
        }
        if (presupuestoActualizado.getCategoria() != null) {
            presupuestoExistente.setCategoria(presupuestoActualizado.getCategoria());
        }
        if (presupuestoActualizado.getFecha() != null) {
            presupuestoExistente.setFecha(presupuestoActualizado.getFecha());
        }
        if (presupuestoActualizado.getDetalles() != null && !presupuestoActualizado.getDetalles().isEmpty()) {
            presupuestoExistente.getDetalles().clear();
            presupuestoActualizado.getDetalles().forEach(detalle -> {
                detalle.setPresupuesto(presupuestoExistente);
                presupuestoExistente.getDetalles().add(detalle);
            });
        }

        Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuestoExistente);
        return ResponseEntity.ok(presupuestoGuardado);
    }

    @PostMapping("/rechazar/{id}")
    public ResponseEntity<Void> rechazarPresupuesto(@PathVariable Long id, @CookieValue (name = "accessToken") String jwtToken) {
        String correo = jwtTokenService.extractUsername(jwtToken);
        Optional<Usuario> usuario = usuarioRepository.findByEmail(correo);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        // Obtener la distribuidora por CUIT
        Optional<Distribuidora> distribuidoraOpt = distribuidoraRepository.findById(usuario.get().getCuit());
        if (distribuidoraOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Distribuidora distribuidora = distribuidoraOpt.get();

        Optional<Presupuesto> presupuestoOpt = presupuestoRepository.findById(id);

        if (presupuestoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Presupuesto presupuesto = presupuestoOpt.get();

        Optional<List<Object>> resultado = cotizacionRepository.findByPresupuesto_IdPresupuestoAndDistribuidora_Cuit(presupuesto.getId(), distribuidora.getCuit());

        if (resultado.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            presupuestoRepository.rechazarPresupuesto(presupuesto.getId(), distribuidora.getCuit());
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (presupuestoRepository.existsById(id)) {
            presupuestoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enlistar")
    public ResponseEntity<PaginationResultDto> enlistar(Pageable pageable, @CookieValue (name = "accessToken") String jwtToken) {
        String correo = jwtTokenService.extractUsername(jwtToken);
        Page<Presupuesto> presupuestos = null;
        Optional<Usuario> usuario = usuarioRepository.findByEmail(correo);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        // TODO: Cuando es una persona fisica, en la paginacion hacer un spec que filtre los presupuestos cuyas cotizaciones fueron ya reclamadas. Debe haber una tabla donde se almacene el id del presupuesto

        if (usuario.get().getTipoUsuario() == Usuario.TipoUsuario.DISTRIBUIDORA){
            presupuestos = enlistarPresupuestosDistribuidora(pageable, usuario.get().getCuit());
        } else if (usuario.get().getTipoUsuario() == Usuario.TipoUsuario.PERSONA_FISICA){
            presupuestos = enlistarPresupuestosPersonaFisica(pageable, usuario.get().getCuit());
        } else if (usuario.get().getTipoUsuario() == Usuario.TipoUsuario.EMPLEADO){
            presupuestos = enlistarPresupuestosPersonaFisica(pageable, usuario.get().getCuit());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        assert presupuestos != null;
        List<Presupuesto> presupuestoLista = presupuestos.getContent().stream().peek(c -> c.getDetalles().forEach(d -> {
            c.setCotizaciones(null);
        })).toList();

        if (presupuestos == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (presupuestos.getContent().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        PaginationResultDto resultDto = new PaginationResultDto(presupuestoLista, presupuestos.hasPrevious(), presupuestos.hasNext());

        return ResponseEntity.ok(resultDto);
    }
    private Page<Presupuesto> enlistarPresupuestosDistribuidora(Pageable pageable, String cuit) {
        // Obtener la distribuidora por CUIT
        Optional<Distribuidora> distribuidoraOpt = distribuidoraRepository.findById(cuit);
        if (distribuidoraOpt.isEmpty()) {
            return null;
        }

        Distribuidora distribuidora = distribuidoraOpt.get();
        List<Categoria> categoriasDistribuidora = distribuidora.getCategorias();

        // Si la distribuidora no tiene categorías, retornar lista vacía
        if (categoriasDistribuidora == null || categoriasDistribuidora.isEmpty()) {
            return null;
        }

        // Crear una especificación para filtrar presupuestos por las categorías de la distribuidora
        Specification<Presupuesto> spec = PresupuestoSpecs.categoriasIn(categoriasDistribuidora);
        Specification<Presupuesto> fechaLimiteSpec = PresupuestoSpecs.fechaLimiteBefore(new Date());
        Specification<Presupuesto> noRechazadoSpec = PresupuestoSpecs.noRechazadoPor(distribuidora.getCuit());
        spec = spec.and(fechaLimiteSpec).and(noRechazadoSpec);

        // Obtener los presupuestos filtrados con paginación y orden por fecha descendente

        return presupuestoRepository.findAll(spec, PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "fecha"))));
    }

    private Page<Presupuesto> enlistarPresupuestosPersonaFisica(Pageable pageable, String cuit){
        // Obtener la distribuidora por CUIT
        Optional<PersonaFisica> personaFisicaOpt = personaFisicaRepository.findById(cuit);
        if (personaFisicaOpt.isEmpty()) {
            return null;
        }

        PersonaFisica personaFisica = personaFisicaOpt.get();

        Specification<Presupuesto> fechaLimiteSpec = PresupuestoSpecs.fechaLimiteAfter(new Date());
        Specification<Presupuesto> hechoPorSpec = PresupuestoSpecs.hechoPor(personaFisica.getCuit());
        fechaLimiteSpec = fechaLimiteSpec.and(hechoPorSpec);

        return presupuestoRepository.findAll(fechaLimiteSpec, PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "fecha"))));
    }

    @GetMapping("/detalles/{id}/enlistar")
    public ResponseEntity<PaginationResultDto> enlistarDetalles(Pageable pageable, @PathVariable Long id, @CookieValue (name = "accessToken") String jwtToken) {
        Page<DetallePresupuesto> detallesOpt = detallePresupuestoRepository.findAllByPresupuesto_IdPresupuesto(id, PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "idDetallePresupuesto"))
        ));
        if (detallesOpt.getContent().isEmpty()){
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(new PaginationResultDto(detallesOpt.getContent(), detallesOpt.hasPrevious(), detallesOpt.hasNext()));
    }

    @GetMapping("/detalles/{id}/cotizar")
    public ResponseEntity<List<DetallePresupuesto>> obtenerDetallesParaCotizar(@PathVariable Long id) {
        Optional<List<DetallePresupuesto>> detalles = detallePresupuestoRepository.findAllByPresupuesto_IdPresupuesto(id);
        return detalles.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/verificarFechaLimite/{id}")
    public ResponseEntity<Boolean> verificarFechaLimite(@PathVariable Long id) {
        Optional<Presupuesto> presupuestoOpt = presupuestoRepository.findById(id);
        if (presupuestoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Presupuesto presupuesto = presupuestoOpt.get();
        Date fechaActual = new Date();
        boolean dentroDeLimite = fechaActual.after(presupuesto.getFechaLimite()) || fechaActual.equals(presupuesto.getFechaLimite());
        return ResponseEntity.ok(dentroDeLimite);
    }

}
