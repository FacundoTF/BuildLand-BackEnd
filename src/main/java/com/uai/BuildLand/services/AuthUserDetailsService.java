package com.uai.BuildLand.services;

//import com.evoila.jwt.repository.UserRepository;
import com.uai.BuildLand.models.auth.AuthUsuario;
import com.uai.BuildLand.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UsuarioRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(AuthUsuario::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}