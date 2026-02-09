//package com.uai.BuildLand.repositories;
//
//import com.uai.BuildLand.models.auth.Usuario;
//import com.uai.BuildLand.models.auth.UsuarioGrupo;
//import com.uai.BuildLand.models.access.Grupo;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface UsuarioGrupoRepository extends JpaRepository<UsuarioGrupo, UsuarioGrupo.UsuarioGrupoId> {
//
//    List<UsuarioGrupo> findByCuit(Usuario usuario);
//
//    List<UsuarioGrupo> findByGrupo(Grupo grupo);
//
//    Optional<UsuarioGrupo> findByCuitAndGrupo(Usuario usuario, Grupo grupo);
//
//    boolean existsByCuitAndGrupo(Usuario usuario, Grupo grupo);
//
//    void deleteByCuitAndGrupo(Usuario usuario, Grupo grupo);
//}
