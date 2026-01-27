package com.uai.BuildLand.controllers;

import com.uai.BuildLand.controllers.specifications.PresupuestoSpecs;
import com.uai.BuildLand.models.Categoria;
import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.models.Producto;
import com.uai.BuildLand.models.ProductoMarca;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.cotizacion.Cotizacion;
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

    public PresupuestoController(PresupuestoRepository presupuestoRepository, EmailService emailService, DistribuidoraRepository distribuidoraRepository, JwtTokenService jwtTokenService, UsuarioRepository usuarioRepository, DetallePresupuestoRepository detallePresupuestoRepository, ProductoMarcaRepository productoMarcaRepository) {
        this.productoMarcaRepository = productoMarcaRepository;
        this.detallePresupuestoRepository = detallePresupuestoRepository;
        this.jwtTokenService = jwtTokenService;
        this.presupuestoRepository = presupuestoRepository;
        this.emailService = emailService;
        this.distribuidoraRepository = distribuidoraRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presupuesto> obtenerPorId(@PathVariable Long id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);
        return presupuesto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Presupuesto> crear(@RequestBody Presupuesto presupuesto, UriComponentsBuilder ucb) {
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

        presupuesto.setCotizaciones(new ArrayList<Cotizacion>());

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
        List<Categoria> categoriasDistribuidora = distribuidora.getCategorias();

        // Si la distribuidora no tiene categorías, retornar lista vacía
        if (categoriasDistribuidora == null || categoriasDistribuidora.isEmpty()) {
            return ResponseEntity.ok(new PaginationResultDto(new ArrayList<>(), false, false));
        }

        // Crear una especificación para filtrar presupuestos por las categorías de la distribuidora
        Specification<Presupuesto> spec = PresupuestoSpecs.categoriasIn(categoriasDistribuidora);
        Specification<Presupuesto> fechaLimiteSpec = PresupuestoSpecs.fechaLimiteBefore(new Date());
        spec = spec.and(fechaLimiteSpec);

        // Obtener los presupuestos filtrados con paginación y orden por fecha descendente
        Page<Presupuesto> presupuestos = presupuestoRepository.findAll(spec, PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "fecha"))));

        if (presupuestos.getContent().isEmpty()){
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(new PaginationResultDto(presupuestos.getContent(), presupuestos.hasPrevious(), presupuestos.hasNext()));
    }

    @GetMapping("/detalles/{id}/enlistar")
    public ResponseEntity<PaginationResultDto> enlistarDetalles(Pageable pageable, @PathVariable Long id, @CookieValue (name = "accessToken") String jwtToken) {
        String correo = jwtTokenService.extractUsername(jwtToken);
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
}
