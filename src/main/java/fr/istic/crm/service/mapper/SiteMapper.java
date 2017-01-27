package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.SiteDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Site and its DTO SiteDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SiteMapper {

    @Mapping(source = "entrepriseSiege.id", target = "entrepriseSiegeId")
    @Mapping(source = "entrepriseSite.id", target = "entrepriseSiteId")
    @Mapping(source = "entrepriseSite.nom", target = "entrepriseSiteNom")
    SiteDTO siteToSiteDTO(Site site);

    List<SiteDTO> sitesToSiteDTOs(List<Site> sites);

    @Mapping(source = "entrepriseSiegeId", target = "entrepriseSiege")
    @Mapping(target = "conventionStages", ignore = true)
    @Mapping(source = "entrepriseSiteId", target = "entrepriseSite")
    Site siteDTOToSite(SiteDTO siteDTO);

    List<Site> siteDTOsToSites(List<SiteDTO> siteDTOs);

    default Entreprise entrepriseFromId(Long id) {
        if (id == null) {
            return null;
        }
        Entreprise entreprise = new Entreprise();
        entreprise.setId(id);
        return entreprise;
    }
}
