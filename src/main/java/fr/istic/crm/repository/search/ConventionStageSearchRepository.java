package fr.istic.crm.repository.search;

import fr.istic.crm.domain.ConventionStage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ConventionStage entity.
 */
public interface ConventionStageSearchRepository extends ElasticsearchRepository<ConventionStage, Long> {
}
