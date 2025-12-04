package com.uai.BuildLand.services;

//import com.evoila.jwt.repository.UserRepository;
import com.uai.BuildLand.models.access.Permiso;
import com.uai.BuildLand.models.auth.AuthUsuario;
import com.uai.BuildLand.models.auth.Usuario;
import com.uai.BuildLand.models.dto.AuthUserInfoDto;
import com.uai.BuildLand.repositories.UsuarioRepository;
import com.uai.BuildLand.repositories.UsuarioGrupoRepository;
import com.uai.BuildLand.repositories.UsuarioPermisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UsuarioRepository userRepository;
    private final UsuarioGrupoRepository usuarioGrupoRepository;
    private final UsuarioPermisoRepository usuarioPermisoRepository;

    @Override
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

        return permisosSet.stream().collect(Collectors.toList());
    }
}