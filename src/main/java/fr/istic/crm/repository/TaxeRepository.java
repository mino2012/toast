package fr.istic.crm.repository;

import fr.istic.crm.domain.Taxe;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Taxe entity.
 */
@SuppressWarnings("unused")
public interface TaxeRepository extends JpaRepository<Taxe,Long> {

}
