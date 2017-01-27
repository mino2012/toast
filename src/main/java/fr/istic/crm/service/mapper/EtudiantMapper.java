package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.EtudiantDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Etudiant and its DTO EtudiantDTO.
 */
@Mapper(componentModel = "spring", uses = {DiplomeMapper.class, })
public interface EtudiantMapper {

    EtudiantDTO etudiantToEtudiantDTO(Etudiant etudiant);

    List<EtudiantDTO> etudiantsToEtudiantDTOs(List<Etudiant> etudiants);

    @Mapping(target = "conventionStages", ignore = true)
    Etudiant etudiantDTOToEtudiant(EtudiantDTO etudiantDTO);

    List<Etudiant> etudiantDTOsToEtudiants(List<EtudiantDTO> etudiantDTOs);

    default Diplome diplomeFromId(Long id) {
        if (id == null) {
            return null;
        }
        Diplome diplome = new Diplome();
        diplome.setId(id);
        return diplome;
    }
}
