package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.DiplomeDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Diplome and its DTO DiplomeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DiplomeMapper {

    DiplomeDTO diplomeToDiplomeDTO(Diplome diplome);

    List<DiplomeDTO> diplomesToDiplomeDTOs(List<Diplome> diplomes);

    @Mapping(target = "filieres", ignore = true)
    @Mapping(target = "partenariats", ignore = true)
    @Mapping(target = "intervenants", ignore = true)
    Diplome diplomeDTOToDiplome(DiplomeDTO diplomeDTO);

    List<Diplome> diplomeDTOsToDiplomes(List<DiplomeDTO> diplomeDTOs);
}
