package fr.istic.crm.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Diplome entity.
 */
public class DiplomeDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    private Long debutVersion;

    private Long finVersion;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DiplomeDTO diplomeDTO = (DiplomeDTO) o;

        if ( ! Objects.equals(id, diplomeDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DiplomeDTO{" +
            "id=" + id +
            ", nom='" + nom + "'" +
            ", debutVersion='" + debutVersion + "'" +
            ", finVersion='" + finVersion + "'" +
            '}';
    }
}
