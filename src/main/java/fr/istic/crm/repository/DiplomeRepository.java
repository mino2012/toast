package fr.istic.crm.repository;

import fr.istic.crm.domain.Diplome;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Diplome entity.
 */
@SuppressWarnings("unused")
public interface DiplomeRepository extends JpaRepository<Diplome,Long> {

}
