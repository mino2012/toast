package fr.istic.crm.repository.search;

import fr.istic.crm.domain.Tuteur;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Tuteur entity.
 */
public interface TuteurSearchRepository extends ElasticsearchRepository<Tuteur, Long> {
}
