package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Categoria;
import com.uai.BuildLand.repositories.CategoriaRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categorias")
public class CategoriaController {
    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }
    @GetMapping()
    public ResponseEntity<List<Categoria>> obtenerCategorias() {
        List<Categoria> categoria = categoriaRepository.findAll();
        if (categoria.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoria);
    }
}
