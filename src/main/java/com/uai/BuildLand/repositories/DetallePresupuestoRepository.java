package com.uai.BuildLand.repositories;

import com.uai.BuildLand.models.presupuesto.DetallePresupuesto;
import com.uai.BuildLand.models.presupuesto.Presupuesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface DetallePresupuestoRepository extends JpaRepository<DetallePresupuesto, Long>, PagingAndSortingRepository<DetallePresupuesto, Long> {
    Page<DetallePresupuesto> findAllByPresupuesto_IdPresupuesto(Long presupuestoIdPresupuesto, Pageable pageable);

    Optional<List<DetallePresupuesto>> findAllByPresupuesto_IdPresupuesto(Long id);
}
