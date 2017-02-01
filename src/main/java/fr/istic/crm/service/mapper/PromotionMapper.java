package fr.istic.crm.service.mapper;

import fr.istic.crm.domain.*;
import fr.istic.crm.service.dto.PromotionDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Promotion and its DTO PromotionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PromotionMapper {

    @Mapping(source = "filiere.id", target = "filiereId")
    @Mapping(source = "filiere.niveau", target = "filiereNiveau")
    PromotionDTO promotionToPromotionDTO(Promotion promotion);

    List<PromotionDTO> promotionsToPromotionDTOs(List<Promotion> promotions);

    @Mapping(source = "filiereId", target = "filiere")
    @Mapping(target = "etudiants", ignore = true)
    Promotion promotionDTOToPromotion(PromotionDTO promotionDTO);

    List<Promotion> promotionDTOsToPromotions(List<PromotionDTO> promotionDTOs);

    default Filiere filiereFromId(Long id) {
        if (id == null) {
            return null;
        }
        Filiere filiere = new Filiere();
        filiere.setId(id);
        return filiere;
    }
}
