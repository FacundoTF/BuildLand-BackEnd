package com.uai.BuildLand.services;

import com.uai.BuildLand.models.auth.AuthRequest;
import com.uai.BuildLand.models.auth.AuthResponse;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.dto.AuthUserInfoDto;
import com.uai.BuildLand.repositories.UsuarioGrupoRepository;
import com.uai.BuildLand.repositories.UsuarioPermisoRepository;
import com.uai.BuildLand.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioGrupoRepository usuarioGrupoRepository;
    private final UsuarioPermisoRepository usuarioPermisoRepository;

    public AuthUserInfoDto authenticate(AuthRequest authRequest) {
        var token = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        AuthUserInfoDto authUserInfoDto = getUserInfo(authentication.getName());

        String jwtToken = jwtTokenService.generateToken(authentication, authUserInfoDto);
        Long expiresAt = jwtTokenService.extractExpirationTime(jwtToken);

        String refreshJwtToken = jwtTokenService.generateRefreshToken(authentication, authUserInfoDto);
        Long refreshTokenExpiresAt = jwtTokenService.extractExpirationTime(refreshJwtToken);

        return new AuthUserInfoDto(
                new AuthResponse(jwtToken, authentication.getName(), expiresAt, refreshJwtToken, refreshTokenExpiresAt),
                authUserInfoDto.getGrupos(),
                authUserInfoDto.getPermisos()
        );
    }

    public AuthUserInfoDto getUserInfo(String username) {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<String> grupos = usuarioGrupoRepository.findByUsuario(usuario)
                .stream()
                .map(ug -> ug.getGrupo().getNombre())
                .collect(Collectors.toList());

        HashSet<String> permisosSet = new HashSet<>();

        if(!grupos.isEmpty()){
            List<String> permisosGrupos = usuarioGrupoRepository.findByUsuario(usuario)
                    .stream()
                    .flatMap(ug -> ug.getGrupo().getPermisos().stream())
                    .map(p -> p.getNombre())
                    .collect(Collectors.toList());
            
            permisosSet.addAll(permisosGrupos);
        }

        List<String> permisosUsuario = usuarioPermisoRepository.findByUsuario(usuario)
                .stream()
                .map(up -> up.getPermiso().getNombre())
                .collect(Collectors.toList());
        
        permisosSet.addAll(permisosUsuario);

        List<String> permisos = permisosSet.stream().collect(Collectors.toList());

        return new AuthUserInfoDto(grupos, permisos);
    }
}