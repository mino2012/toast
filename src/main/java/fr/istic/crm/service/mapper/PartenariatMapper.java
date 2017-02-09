package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.PartenariatDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Partenariat and its DTO PartenariatDTO.
 */
@Mapper(componentModel = "spring", uses = {DiplomeMapper.class, })
public interface PartenariatMapper {

    @Mapping(source = "entreprise.id", target = "entrepriseId")
    @Mapping(source = "entreprise.nom", target = "entrepriseNom")
    @Mapping(source = "diplomes", target = "diplomes")
    PartenariatDTO partenariatToPartenariatDTO(Partenariat partenariat);

    List<PartenariatDTO> partenariatsToPartenariatDTOs(List<Partenariat> partenariats);

    @Mapping(source = "entrepriseId", target = "entreprise")
    Partenariat partenariatDTOToPartenariat(PartenariatDTO partenariatDTO);

    List<Partenariat> partenariatDTOsToPartenariats(List<PartenariatDTO> partenariatDTOs);

    default Diplome diplomeFromId(Long id) {
        if (id == null) {
            return null;
        }
        Diplome diplome = new Diplome();
        diplome.setId(id);
        return diplome;
    }

    default Entreprise entrepriseFromId(Long id) {
        if (id == null) {
            return null;
        }
        Entreprise entreprise = new Entreprise();
        entreprise.setId(id);
        return entreprise;
    }
}
