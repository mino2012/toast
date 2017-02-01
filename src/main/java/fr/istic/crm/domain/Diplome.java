package fr.istic.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * A Diplome.
 */
@Entity
@Table(name = "diplome")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "diplome")
@Audited(targetAuditMode = NOT_AUDITED)
@EntityListeners(AuditingEntityListener.class)
public class Diplome implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "date_creation", nullable = false, updatable = false)
    @CreatedDate
    private Long dateCreation;

    @Column(name = "date_modification")
    @LastModifiedDate
    private Long dateModification;

    @OneToMany(mappedBy = "diplome")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotAudited
    private Set<Filiere> filieres = new HashSet<>();

    @ManyToMany(mappedBy = "diplomes")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Partenariat> partenariats = new HashSet<>();

    @ManyToMany(mappedBy = "diplomes")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Professionnel> intervenants = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public Diplome nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getDateCreation() {
        return dateCreation;
    }

    public Diplome dateCreation(Long dateCreation) {
        this.dateCreation = dateCreation;
        return this;
    }

    public void setDateCreation(Long dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Long getDateModification() {
        return dateModification;
    }

    public Diplome dateModification(Long dateModification) {
        this.dateModification = dateModification;
        return this;
    }

    public void setDateModification(Long dateModification) {
        this.dateModification = dateModification;
    }

    public Set<Filiere> getFilieres() {
        return filieres;
    }

    public Diplome filieres(Set<Filiere> filieres) {
        this.filieres = filieres;
        return this;
    }

    public Diplome addFiliere(Filiere filiere) {
        filieres.add(filiere);
        filiere.setDiplome(this);
        return this;
    }

    public Diplome removeFiliere(Filiere filiere) {
        filieres.remove(filiere);
        filiere.setDiplome(null);
        return this;
    }

    public void setFilieres(Set<Filiere> filieres) {
        this.filieres = filieres;
    }

    public Set<Partenariat> getPartenariats() {
        return partenariats;
    }

    public Diplome partenariats(Set<Partenariat> partenariats) {
        this.partenariats = partenariats;
        return this;
    }

    public Diplome addPartenariat(Partenariat partenariat) {
        partenariats.add(partenariat);
        partenariat.getDiplomes().add(this);
        return this;
    }

    public Diplome removePartenariat(Partenariat partenariat) {
        partenariats.remove(partenariat);
        partenariat.getDiplomes().remove(this);
        return this;
    }

    public void setPartenariats(Set<Partenariat> partenariats) {
        this.partenariats = partenariats;
    }

    public Set<Professionnel> getIntervenants() {
        return intervenants;
    }

    public Diplome intervenants(Set<Professionnel> professionnels) {
        this.intervenants = professionnels;
        return this;
    }

    public Diplome addIntervenant(Professionnel professionnel) {
        intervenants.add(professionnel);
        professionnel.getDiplomes().add(this);
        return this;
    }

    public Diplome removeIntervenant(Professionnel professionnel) {
        intervenants.remove(professionnel);
        professionnel.getDiplomes().remove(this);
        return this;
    }

    public void setIntervenants(Set<Professionnel> professionnels) {
        this.intervenants = professionnels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Diplome diplome = (Diplome) o;
        if (diplome.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, diplome.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Diplome{" +
            "id=" + id +
            ", nom='" + nom + "'" +
            ", dateCreation='" + dateCreation + "'" +
            ", dateModification='" + dateModification + "'" +
            '}';
    }
}
