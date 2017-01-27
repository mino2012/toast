package fr.istic.crm.repository;

import fr.istic.crm.domain.Tuteur;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tuteur entity.
 */
@SuppressWarnings("unused")
public interface TuteurRepository extends JpaRepository<Tuteur,Long> {

}
