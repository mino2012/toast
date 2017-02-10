package fr.istic.crm.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Site entity.
 */
public class SiteDTO implements Serializable {

    private Long id;

    private String adresse;

    private String codePostal;

    private String ville;

    private String pays;

    private String telephone;

    private Long dateCreation;

    private Long dateModification;


    private Long entrepriseSiegeId;

    private String entrepriseSiegeNom;

    private Long entrepriseSiteId;

    private String entrepriseSiteNom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }
    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public Long getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Long dateCreation) {
        this.dateCreation = dateCreation;
    }
    public Long getDateModification() {
        return dateModification;
    }

    public void setDateModification(Long dateModification) {
        this.dateModification = dateModification;
    }

    public Long getEntrepriseSiegeId() {
        return entrepriseSiegeId;
    }

    public void setEntrepriseSiegeId(Long entrepriseId) {
        this.entrepriseSiegeId = entrepriseId;
    }

    public Long getEntrepriseSiteId() {
        return entrepriseSiteId;
    }

    public void setEntrepriseSiteId(Long entrepriseId) {
        this.entrepriseSiteId = entrepriseId;
    }

    public String getEntrepriseSiteNom() {
        return entrepriseSiteNom;
    }

    public void setEntrepriseSiteNom(String entrepriseNom) {
        this.entrepriseSiteNom = entrepriseNom;
    }

    public String getEntrepriseSiegeNom() {
        return entrepriseSiegeNom;
    }

    public void setEntrepriseSiegeNom(String entrepriseSiegeNom) {
        this.entrepriseSiegeNom = entrepriseSiegeNom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SiteDTO siteDTO = (SiteDTO) o;

        if ( ! Objects.equals(id, siteDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SiteDTO{" +
            "id=" + id +
            ", adresse='" + adresse + "'" +
            ", codePostal='" + codePostal + "'" +
            ", ville='" + ville + "'" +
            ", pays='" + pays + "'" +
            ", telephone='" + telephone + "'" +
            ", dateCreation='" + dateCreation + "'" +
            ", dateModification='" + dateModification + "'" +
            '}';
    }
}
