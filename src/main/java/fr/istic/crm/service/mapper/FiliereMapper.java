package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.FiliereDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Filiere and its DTO FiliereDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FiliereMapper {

    @Mapping(source = "diplome.id", target = "diplomeId")
    @Mapping(source = "diplome.nom", target = "diplomeNom")
    FiliereDTO filiereToFiliereDTO(Filiere filiere);

    List<FiliereDTO> filieresToFiliereDTOs(List<Filiere> filieres);

    @Mapping(target = "promotions", ignore = true)
    @Mapping(source = "diplomeId", target = "diplome")
    Filiere filiereDTOToFiliere(FiliereDTO filiereDTO);

    List<Filiere> filiereDTOsToFilieres(List<FiliereDTO> filiereDTOs);

    default Diplome diplomeFromId(Long id) {
        if (id == null) {
            return null;
        }
        Diplome diplome = new Diplome();
        diplome.setId(id);
        return diplome;
    }
}
