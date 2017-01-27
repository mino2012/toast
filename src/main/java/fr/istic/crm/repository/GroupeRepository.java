package fr.istic.crm.repository;

import fr.istic.crm.domain.Groupe;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Groupe entity.
 */
@SuppressWarnings("unused")
public interface GroupeRepository extends JpaRepository<Groupe,Long> {

}
