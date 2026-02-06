package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.auth.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.uai.BuildLand.models.Empleado;

public interface EmpleadoRepository extends CrudRepository<Empleado, String>, PagingAndSortingRepository<Empleado, String> {
    Empleado findByUsuario(Usuario usuario);
}
