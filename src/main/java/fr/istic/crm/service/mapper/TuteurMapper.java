package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.TuteurDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Tuteur and its DTO TuteurDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TuteurMapper {

    TuteurDTO tuteurToTuteurDTO(Tuteur tuteur);

    List<TuteurDTO> tuteursToTuteurDTOs(List<Tuteur> tuteurs);

    @Mapping(target = "conventionStages", ignore = true)
    Tuteur tuteurDTOToTuteur(TuteurDTO tuteurDTO);

    List<Tuteur> tuteurDTOsToTuteurs(List<TuteurDTO> tuteurDTOs);
}
