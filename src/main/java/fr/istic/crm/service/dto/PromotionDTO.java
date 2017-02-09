package fr.istic.crm.service.dto;

import fr.istic.crm.domain.Diplome;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Promotion entity.
 */
public class PromotionDTO implements Serializable {

    private Long id;

    @NotNull
    private ZonedDateTime annee;

    private Long filiereId;


    private String filiereNiveau;

    private Diplome diplome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public ZonedDateTime getAnnee() {
        return annee;
    }

    public void setAnnee(ZonedDateTime annee) {
        this.annee = annee;
    }

    public Long getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(Long filiereId) {
        this.filiereId = filiereId;
    }


    public String getFiliereNiveau() {
        return filiereNiveau;
    }

    public void setFiliereNiveau(String filiereNiveau) {
        this.filiereNiveau = filiereNiveau;
    }

    public Diplome getDiplome() {
        return diplome;
    }

    public void setDiplome(Diplome diplome) {
        this.diplome = diplome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PromotionDTO promotionDTO = (PromotionDTO) o;

        if ( ! Objects.equals(id, promotionDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PromotionDTO{" +
            "id=" + id +
            ", annee='" + annee + "'" +
            '}';
    }
}
