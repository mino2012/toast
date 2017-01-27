package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Professionnel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Professionnel entity.
 */
public interface ProfessionnelSearchRepository extends ElasticsearchRepository<Professionnel, Long> {
}
