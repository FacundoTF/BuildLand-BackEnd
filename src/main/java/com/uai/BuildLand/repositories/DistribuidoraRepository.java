package com.uai.BuildLand.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.Distribuidora;

public interface DistribuidoraRepository extends CrudRepository<Distribuidora, String>, PagingAndSortingRepository<Distribuidora, String> {
    // /**
    //  * Busca distribuidoras por nombre (case insensitive)
    //  * @param nombre Nombre a buscar
    //  * @return Lista de distribuidoras que contienen el nombre
    //  */
    // boolean existsByNombreIgnoreCase(String nombre);

    // /**
    //  * Busca distribuidoras por correo electrónico del usuario asociado (case insensitive)
    //  * @param correoElectronico Correo electrónico a buscar
    //  * @return true si existe una distribuidora con ese correo, false en caso contrario
    //  */
    // boolean existsByUsuarioCorreoElectronicoIgnoreCase(String correoElectronico);
    
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
