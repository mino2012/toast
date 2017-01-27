package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.ProfessionnelDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Professionnel and its DTO ProfessionnelDTO.
 */
@Mapper(componentModel = "spring", uses = {DiplomeMapper.class, })
public interface ProfessionnelMapper {

    @Mapping(source = "entrepriseContact.id", target = "entrepriseContactId")
    @Mapping(source = "entreprisePersonnel.id", target = "entreprisePersonnelId")
    @Mapping(source = "entreprisePersonnel.nom", target = "entreprisePersonnelNom")
    ProfessionnelDTO professionnelToProfessionnelDTO(Professionnel professionnel);

    List<ProfessionnelDTO> professionnelsToProfessionnelDTOs(List<Professionnel> professionnels);

    @Mapping(source = "entrepriseContactId", target = "entrepriseContact")
    @Mapping(target = "conventionStages", ignore = true)
    @Mapping(source = "entreprisePersonnelId", target = "entreprisePersonnel")
    Professionnel professionnelDTOToProfessionnel(ProfessionnelDTO professionnelDTO);

    List<Professionnel> professionnelDTOsToProfessionnels(List<ProfessionnelDTO> professionnelDTOs);

    default Entreprise entrepriseFromId(Long id) {
        if (id == null) {
            return null;
        }
        Entreprise entreprise = new Entreprise();
        entreprise.setId(id);
        return entreprise;
    }

    default Diplome diplomeFromId(Long id) {
        if (id == null) {
            return null;
        }
        Diplome diplome = new Diplome();
        diplome.setId(id);
        return diplome;
    }
}
