package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.auth.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.Distribuidora;

import java.util.List;

public interface DistribuidoraRepository extends CrudRepository<Distribuidora, String>, PagingAndSortingRepository<Distribuidora, String> {
    boolean existsByNombreIgnoreCase(String nombre);

    List<Distribuidora> findByCategoriasNombre(String nombre);

    // /**
    //  * Busca distribuidoras por correo electrónico del usuario asociado (case insensitive)
    //  * @param correoElectronico Correo electrónico a buscar
    //  * @return true si existe una distribuidora con ese correo, false en caso contrario
    //  */
     boolean existsByUsuario(Usuario correoElectronico);
    
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
