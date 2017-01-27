package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Entreprise;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Entreprise entity.
 */
public interface EntrepriseSearchRepository extends ElasticsearchRepository<Entreprise, Long> {
}
