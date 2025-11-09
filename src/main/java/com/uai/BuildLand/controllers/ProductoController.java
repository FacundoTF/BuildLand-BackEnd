package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Producto;
import com.uai.BuildLand.repositories.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto, UriComponentsBuilder ucb) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty() || producto.getCategoria() == null) {
            return ResponseEntity.badRequest().build();
        }

        Producto productoGuardado = productoRepository.save(producto);
        URI uri = ucb.path("/productos/{id}").buildAndExpand(productoGuardado.getId()).toUri();
        return ResponseEntity.created(uri).body(productoGuardado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        Optional<Producto> productoExistenteOpt = productoRepository.findById(id);
        if (productoExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Producto productoExistente = productoExistenteOpt.get();

        if (productoActualizado.getNombre() != null && !productoActualizado.getNombre().trim().isEmpty()) {
            productoExistente.setNombre(productoActualizado.getNombre());
        }
        if (productoActualizado.getPrecio() > 0) {
            productoExistente.setPrecio(productoActualizado.getPrecio());
        }
        if (productoActualizado.getCategoria() != null) {
            productoExistente.setCategoria(productoActualizado.getCategoria());
        }

        Producto productoGuardado = productoRepository.save(productoExistente);
        return ResponseEntity.ok(productoGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enlistar")
    public ResponseEntity<List<Producto>> enlistar(Pageable pageable) {
        Page<Producto> productos = productoRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "nombre"))));
        return ResponseEntity.ok(productos.getContent());
    }
}
