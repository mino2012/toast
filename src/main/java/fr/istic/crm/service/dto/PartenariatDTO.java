package fr.istic.crm.service.dto;

import java.time.ZonedDateTime;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Partenariat entity.
 */
public class PartenariatDTO implements Serializable {

    private Long id;

    private ZonedDateTime dateDebut;

    private ZonedDateTime dateFin;

    private Set<DiplomeDTO> diplomes = new HashSet<>();

    private Long entrepriseId;

    private String entrepriseNom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public ZonedDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(ZonedDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }
    public ZonedDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(ZonedDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Set<DiplomeDTO> getDiplomes() {
        return diplomes;
    }

    public void setDiplomes(Set<DiplomeDTO> diplomes) {
        this.diplomes = diplomes;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }


    public String getEntrepriseNom() {
        return entrepriseNom;
    }

    public void setEntrepriseNom(String entrepriseNom) {
        this.entrepriseNom = entrepriseNom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PartenariatDTO partenariatDTO = (PartenariatDTO) o;

        if ( ! Objects.equals(id, partenariatDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PartenariatDTO{" +
            "id=" + id +
            ", dateDebut='" + dateDebut + "'" +
            ", dateFin='" + dateFin + "'" +
            '}';
    }
}
