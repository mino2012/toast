package fr.istic.crm.repository;

import fr.istic.crm.domain.Filiere;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Filiere entity.
 */
@SuppressWarnings("unused")
public interface FiliereRepository extends JpaRepository<Filiere,Long> {

}
