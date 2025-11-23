package com.uai.BuildLand;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.repositories.UsuarioRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsuariosTests {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Test
    public void verificarInicioSesion() {
        String email = "test@gmail.com";
        String password = "HolaMundo";

        // Crear un usuario de prueba con contraseña encriptada
        // Usuario usuarioGuardado = new Usuario();
        // usuarioGuardado.setUsername(email);
        // usuarioGuardado.setPassword(PasswordEncoder.encode(password));
        // usuarioGuardado.setTipoUsuario(Usuario.TipoUsuario.PERSONA_FISICA);
        // usuarioRepository.save(usuarioGuardado);

        // Intentar iniciar sesión con la contraseña en texto plano
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);

        ResponseEntity<Void> response = restTemplate.postForEntity("/usuarios/login", usuario, Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
