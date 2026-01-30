package com.uai.BuildLand.controllers;

import com.uai.BuildLand.models.Categoria;
import com.uai.BuildLand.models.Distribuidora;
import com.uai.BuildLand.repositories.CategoriaRepository;

import com.uai.BuildLand.repositories.DistribuidoraRepository;
import com.uai.BuildLand.services.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/categorias")
public class CategoriaController {
    private final CategoriaRepository categoriaRepository;
    private final DistribuidoraRepository distribuidoraRepository;
    private final JwtTokenService jwtTokenService;


    @GetMapping()
    public ResponseEntity<List<Categoria>> obtenerCategorias() {
        List<Categoria> categoria = categoriaRepository.findAll();
        if (categoria.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoria);
    }
    @GetMapping("/por-distribuidora")
    public ResponseEntity<List<Categoria>> obtenerCategoriasPorDistribuidora(@CookieValue("accessToken") String accessToken){
        String correo = jwtTokenService.extractUsername(accessToken);
        Optional<Distribuidora> distribuidoraOpt = distribuidoraRepository.findByUsuario_Email(correo);
        if (distribuidoraOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Categoria> categorias = categoriaRepository.findByDistribuidoraId(distribuidoraOpt.get().getCuit());
        if (categorias.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(categorias);
    }
}
