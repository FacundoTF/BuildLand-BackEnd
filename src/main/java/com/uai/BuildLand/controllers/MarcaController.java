package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Marca;
import com.uai.BuildLand.repositories.MarcaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/marcas")
@CrossOrigin(origins = "*")
public class MarcaController {

    private final MarcaRepository marcaRepository;

    public MarcaController(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @GetMapping("/categorias/{nombreCategoria}")
    public ResponseEntity<List<Marca>> obtenerPorCategoria(@PathVariable String nombreCategoria) {
        List<Marca> marcas = marcaRepository.findByCategoriaNombre(nombreCategoria);
        if (marcas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(marcas);
    }
}
