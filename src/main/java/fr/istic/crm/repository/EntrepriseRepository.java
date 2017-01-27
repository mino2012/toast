package fr.istic.crm.repository;

import fr.istic.crm.domain.Entreprise;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Entreprise entity.
 */
@SuppressWarnings("unused")
public interface EntrepriseRepository extends JpaRepository<Entreprise,Long> {

}
