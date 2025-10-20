package com.uai.BuildLand.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uai.BuildLand.models.Usuario;
import com.uai.BuildLand.repositories.UsuarioRepository;
import com.uai.BuildLand.security.PasswordEncoder;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody Usuario loginRequest){
        Optional<Usuario> usuario = usuarioRepository.findByEmail(loginRequest.getEmail());
        
        if (usuario.isPresent() && PasswordEncoder.matches(loginRequest.getPassword(), usuario.get().getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
