package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.auth.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.Distribuidora;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DistribuidoraRepository extends CrudRepository<Distribuidora, String>, PagingAndSortingRepository<Distribuidora, String> {
    boolean existsByNombreIgnoreCase(String nombre);

    @Modifying
    @Query("DELETE FROM PresupuestoRechazado pr WHERE pr.cuit = :distribuidora")
    void borrarPresupuestosRechazadosYAceptados(@Param("distribuidora") Distribuidora distribuidora);

    @Modifying
    @Query(value = "DELETE FROM distribuidora_categoria WHERE distribuidora_categoria.cuit = :cuit", nativeQuery = true)
    void borrarCategoriasDeDistribuidora(@Param("cuit") String cuit);

    List<Distribuidora> findByCategoriasNombre(String nombre);

    Optional<Distribuidora> findByUsuario_Email(String usuarioEmail);

    // /**
    //  * Busca distribuidoras por correo electrónico del usuario asociado (case insensitive)
    //  * @param correoElectronico Correo electrónico a buscar
    //  * @return true si existe una distribuidora con ese correo, false en caso contrario
    //  */
     boolean existsByUsuario(Usuario correoElectronico);

    Distribuidora findByUsuario(Usuario usuario);

    Distribuidora findByCuit(String cuit);

    /**
     * Busca una distribuidora por CUIT (campo id)
     * @param cuit CUIT a buscar
     * @return Optional con la distribuidora encontrada
     */
    // java.util.Optional<Distribuidora> findByIdIgnoreCase(String cuit);
    
    // /**
    //  * Verifica si existe una distribuidora con el CUIT especificado (campo id)
    //  * @param cuit CUIT a buscar
    //  * @return true si existe, false en caso contrario
    //  */
    // boolean existsByIdIgnoreCase(String cuit);
    
    // /**
    //  * Elimina una distribuidora por CUIT (campo id)
    //  * @param cuit CUIT de la distribuidora a eliminar
    //  */
    // void deleteByIdIgnoreCase(String cuit);
    
    // /**
    //  * Busca distribuidoras que contengan el texto en el nombre
    //  * @param nombre Texto a buscar en el nombre
    //  * @return Lista de distribuidoras
    //  */
    // java.util.List<Distribuidora> findByNombreContainingIgnoreCase(String nombre);
}
