package fr.istic.crm.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Entreprise entity.
 */
public class EntrepriseDTO implements Serializable {

    private Long id;

    private String nom;

    private String pays;

    @NotNull
    private String numSiret;

    @NotNull
    private String numSiren;

    private String telephone;

    private Long debutVersion;

    private Long finVersion;


    private Long groupeId;
    

    private String groupeNom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }
    public String getNumSiret() {
        return numSiret;
    }

    public void setNumSiret(String numSiret) {
        this.numSiret = numSiret;
    }
    public String getNumSiren() {
        return numSiren;
    }

    public void setNumSiren(String numSiren) {
        this.numSiren = numSiren;
    }
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public Long getDebutVersion() {
        return debutVersion;
    }

    public void setDebutVersion(Long debutVersion) {
        this.debutVersion = debutVersion;
    }
    public Long getFinVersion() {
        return finVersion;
    }

    public void setFinVersion(Long finVersion) {
        this.finVersion = finVersion;
    }

    public Long getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Long groupeId) {
        this.groupeId = groupeId;
    }


    public String getGroupeNom() {
        return groupeNom;
    }

    public void setGroupeNom(String groupeNom) {
        this.groupeNom = groupeNom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EntrepriseDTO entrepriseDTO = (EntrepriseDTO) o;

        if ( ! Objects.equals(id, entrepriseDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EntrepriseDTO{" +
            "id=" + id +
            ", nom='" + nom + "'" +
            ", pays='" + pays + "'" +
            ", numSiret='" + numSiret + "'" +
            ", numSiren='" + numSiren + "'" +
            ", telephone='" + telephone + "'" +
            ", debutVersion='" + debutVersion + "'" +
            ", finVersion='" + finVersion + "'" +
            '}';
    }
}
