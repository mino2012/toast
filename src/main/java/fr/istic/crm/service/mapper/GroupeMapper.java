package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.GroupeDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Groupe and its DTO GroupeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface GroupeMapper {

    GroupeDTO groupeToGroupeDTO(Groupe groupe);

    List<GroupeDTO> groupesToGroupeDTOs(List<Groupe> groupes);

    @Mapping(target = "entreprises", ignore = true)
    Groupe groupeDTOToGroupe(GroupeDTO groupeDTO);

    List<Groupe> groupeDTOsToGroupes(List<GroupeDTO> groupeDTOs);
}
