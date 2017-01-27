package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Etudiant;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Etudiant entity.
 */
public interface EtudiantSearchRepository extends ElasticsearchRepository<Etudiant, Long> {
}
