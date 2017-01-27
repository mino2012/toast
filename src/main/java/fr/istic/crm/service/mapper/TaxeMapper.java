package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.TaxeDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Taxe and its DTO TaxeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TaxeMapper {

    @Mapping(source = "entreprise.id", target = "entrepriseId")
    @Mapping(source = "entreprise.nom", target = "entrepriseNom")
    TaxeDTO taxeToTaxeDTO(Taxe taxe);

    List<TaxeDTO> taxesToTaxeDTOs(List<Taxe> taxes);

    @Mapping(source = "entrepriseId", target = "entreprise")
    Taxe taxeDTOToTaxe(TaxeDTO taxeDTO);

    List<Taxe> taxeDTOsToTaxes(List<TaxeDTO> taxeDTOs);

    default Entreprise entrepriseFromId(Long id) {
        if (id == null) {
            return null;
        }
        Entreprise entreprise = new Entreprise();
        entreprise.setId(id);
        return entreprise;
    }
}
