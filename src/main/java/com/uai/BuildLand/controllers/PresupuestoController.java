package com.uai.BuildLand.controllers;

import com.uai.BuildLand.controllers.specifications.PresupuestoSpecs;
import com.uai.BuildLand.models.*;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.cotizacion.Cotizacion;
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
import org.springframework.security.oauth2.jwt.Jwt;
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
    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final EmailService emailService;
    private final DistribuidoraRepository distribuidoraRepository;
    private final JwtTokenService jwtTokenService;
    private final UsuarioRepository usuarioRepository;

    public PresupuestoController(PresupuestoRepository presupuestoRepository, ProductoRepository productoRepository, MarcaRepository marcaRepository, EmailService emailService, DistribuidoraRepository distribuidoraRepository, JwtTokenService jwtTokenService, UsuarioRepository usuarioRepository) {
        this.jwtTokenService = jwtTokenService;
        this.presupuestoRepository = presupuestoRepository;
        this.productoRepository = productoRepository;
        this.marcaRepository = marcaRepository;
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
            if (detalle.getProducto() == null) {
                return ResponseEntity.badRequest().build();
            }else {
                Optional<Producto> producto = productoRepository.findByNombre((detalle.getProducto().getNombre()));
                Optional<Marca> marca = marcaRepository.findByNombre((detalle.getProducto().getMarca().getNombre()));
                if(producto.isEmpty() || marca.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                detalle.setProducto(producto.get());
                detalle.getProducto().setMarca(marca.get());
                boolean valido = verificarDatosInternos(detalle.getProducto(), detalle);

                if (!valido) {
                    return ResponseEntity.badRequest().build();
                }
            }
            detalle.setPresupuesto(presupuesto);
        }

        presupuesto.setFecha(new Date());
        presupuesto.setCotizaciones(new ArrayList<Cotizacion>());

        Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);

        // Enviar email con los detalles del presupuesto
        try {
            emailService.sendPresupuestoMail(presupuestoGuardado);
        } catch (Exception e) {
            // Log del error pero no fallar la creación del presupuesto
            System.err.println("Error al enviar email de presupuesto: " + e.getMessage());
        }

        URI uri = ucb.path("api/presupuestos/{id}").buildAndExpand(presupuestoGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(presupuestoGuardado);
    }

    private boolean verificarDatosInternos(Producto producto, DetallePresupuesto detallePresupuesto) {
        if (detallePresupuesto.getProducto().getMarca() == null) {
            return false;
        }

        if (detallePresupuesto.getCantidad() <= 0) {
            return false;
        }

        if(detallePresupuesto.getProducto() == null) {
            return false;
        }

        return true;
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
    public ResponseEntity<List<Presupuesto>> enlistar(Pageable pageable, @RequestParam String cuit, @CookieValue (name = "accessToken") String jwtToken) {
//        String correo = jwtTokenService.extractUsername(jwtToken);
//        Optional<Usuario> usuario = usuarioRepository.findByEmail(correo);
//        if (usuario.isEmpty()) {
//            return ResponseEntity.status(401).build();
//        }
        // Obtener la distribuidora por CUIT
        Optional<Distribuidora> distribuidoraOpt = distribuidoraRepository.findById(cuit);
        if (distribuidoraOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Distribuidora distribuidora = distribuidoraOpt.get();
        List<Categoria> categoriasDistribuidora = distribuidora.getCategorias();

        // Si la distribuidora no tiene categorías, retornar lista vacía
        if (categoriasDistribuidora == null || categoriasDistribuidora.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        // Crear una especificación para filtrar presupuestos por las categorías de la distribuidora
        Specification<Presupuesto> spec = PresupuestoSpecs.categoriasIn(categoriasDistribuidora);

        // Obtener los presupuestos filtrados con paginación y orden por fecha descendente
        Page<Presupuesto> presupuestos = presupuestoRepository.findAll(spec, PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "fecha"))));

        return ResponseEntity.ok(presupuestos.getContent());
    }
}
