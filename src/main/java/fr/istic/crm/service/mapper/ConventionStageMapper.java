package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.ConventionStageDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity ConventionStage and its DTO ConventionStageDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ConventionStageMapper {

    @Mapping(source = "etudiant.id", target = "etudiantId")
    @Mapping(source = "etudiant.nom", target = "etudiantNom")
    @Mapping(source = "lieuStage.id", target = "lieuStageId")
    @Mapping(source = "lieuStage.adresse", target = "lieuStageAdresse")
    @Mapping(source = "tuteur.id", target = "tuteurId")
    @Mapping(source = "tuteur.nom", target = "tuteurNom")
    @Mapping(source = "maitreStage.id", target = "maitreStageId")
    @Mapping(source = "maitreStage.nom", target = "maitreStageNom")
    @Mapping(source = "lieuStage.entrepriseSite", target = "entreprise")
    ConventionStageDTO conventionStageToConventionStageDTO(ConventionStage conventionStage);

    List<ConventionStageDTO> conventionStagesToConventionStageDTOs(List<ConventionStage> conventionStages);

    @Mapping(source = "etudiantId", target = "etudiant")
    @Mapping(source = "lieuStageId", target = "lieuStage")
    @Mapping(source = "tuteurId", target = "tuteur")
    @Mapping(source = "maitreStageId", target = "maitreStage")
    ConventionStage conventionStageDTOToConventionStage(ConventionStageDTO conventionStageDTO);

    List<ConventionStage> conventionStageDTOsToConventionStages(List<ConventionStageDTO> conventionStageDTOs);

    default Etudiant etudiantFromId(Long id) {
        if (id == null) {
            return null;
        }
        Etudiant etudiant = new Etudiant();
        etudiant.setId(id);
        return etudiant;
    }

    default Site siteFromId(Long id) {
        if (id == null) {
            return null;
        }
        Site site = new Site();
        site.setId(id);
        return site;
    }

    default Tuteur tuteurFromId(Long id) {
        if (id == null) {
            return null;
        }
        Tuteur tuteur = new Tuteur();
        tuteur.setId(id);
        return tuteur;
    }

    default Professionnel professionnelFromId(Long id) {
        if (id == null) {
            return null;
        }
        Professionnel professionnel = new Professionnel();
        professionnel.setId(id);
        return professionnel;
    }
}
