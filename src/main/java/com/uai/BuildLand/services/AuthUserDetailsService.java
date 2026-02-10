package com.uai.BuildLand.services;

import com.uai.BuildLand.models.access.ConjuntoGrupoPermiso;
import com.uai.BuildLand.models.access.Grupo;
import com.uai.BuildLand.models.access.Permiso;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UsuarioRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            Collection<SimpleGrantedAuthority> authorityCollection = new ArrayList<>();
            List<String> permisos = getUserPermissions(user.get());
            for(String permiso : permisos){
                authorityCollection.add(new SimpleGrantedAuthority(permiso));
            }
            return new User(username, user.get().getPassword(), authorityCollection);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    private List<String> getUserPermissions(Usuario usuario) {
        Set<String> permisosSet = new HashSet<>();
        Set<Long> gruposVisitados = new HashSet<>();

        // Recolectar permisos de la jerarquía de grupos (Composite)

        List<Grupo> grupos = usuario.getConjunto().stream()
                .filter(elemento -> elemento instanceof Grupo)
                .map(elemento -> (Grupo) elemento)
                .toList();

        for (Grupo grupo : grupos) {
            recolectarPermisosRecursivamente(grupo, permisosSet, gruposVisitados);
        }

        // Agregar permisos directos del usuario

        List<String> permisosUsuario = usuario.getConjunto().stream()
                .filter(elemento -> elemento instanceof Permiso)
                .map(elemento -> (Permiso) elemento)
                .map(Permiso::getNombre)
                .toList();
        
        permisosSet.addAll(permisosUsuario);

        return new ArrayList<>(permisosSet);
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