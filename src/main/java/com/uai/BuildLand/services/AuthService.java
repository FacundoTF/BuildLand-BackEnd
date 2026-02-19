package com.uai.BuildLand.services;

import com.uai.BuildLand.models.access.ConjuntoGrupoPermiso;
import com.uai.BuildLand.models.access.Grupo;
import com.uai.BuildLand.models.access.Permiso;
import com.uai.BuildLand.models.auth.AuthRequest;
import com.uai.BuildLand.models.auth.AuthResponse;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.dto.AuthUserInfoDto;
import com.uai.BuildLand.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UsuarioRepository usuarioRepository;

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

    @Transactional(readOnly = true)
    public AuthUserInfoDto getUserInfo(String username) {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Grupo> grupos = usuario
                .getConjunto()
                .stream()
                .filter(c -> c instanceof Grupo)
                .map(c -> (Grupo) c).toList();

        List<String> nombresGrupos = grupos.stream()
                .map(ConjuntoGrupoPermiso::getNombre)
                .toList();

        Set<String> permisosSet = new HashSet<>();
        Set<Long> gruposVisitados = new HashSet<>();

        // Recolectar permisos de la jerarquía de grupos (Composite)

        for (Grupo g : grupos) {
            recolectarPermisosRecursivamente(g, permisosSet, gruposVisitados);
        }

        // Agregar permisos directos del usuario

        List<Permiso> permisos = usuario
                .getConjunto()
                .stream()
                .filter(c -> c instanceof Permiso)
                .map(p -> (Permiso) p).toList();

        List<String> permisosUsuario = permisos.stream()
                .map(Permiso::getNombre)
                .toList();
        
        permisosSet.addAll(permisosUsuario);

        return new AuthUserInfoDto(nombresGrupos, new ArrayList<>(permisosSet));
    }

    private void recolectarPermisosRecursivamente(Grupo grupo, Set<String> permisosSet, Set<Long> gruposVisitados) {
        if (grupo == null || gruposVisitados.contains(grupo.getId())) {
            return;
        }

        gruposVisitados.add(grupo.getId());

        for (ConjuntoGrupoPermiso elemento : grupo.getElementos()) {
            if (elemento instanceof Permiso) {
                permisosSet.add(elemento.getNombre());
            } else if (elemento instanceof Grupo) {
                recolectarPermisosRecursivamente((Grupo) elemento, permisosSet, gruposVisitados);
            }
        }
    }
}