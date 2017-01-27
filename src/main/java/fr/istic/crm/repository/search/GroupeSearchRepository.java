package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Groupe;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Groupe entity.
 */
public interface GroupeSearchRepository extends ElasticsearchRepository<Groupe, Long> {
}
