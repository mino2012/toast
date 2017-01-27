package fr.istic.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Groupe.
 */
@Entity
@Table(name = "groupe")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "groupe")
public class Groupe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "debut_version")
    private Long debutVersion;

    @Column(name = "fin_version")
    private Long finVersion;

    @OneToMany(mappedBy = "groupe")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Entreprise> entreprises = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public Groupe nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getDebutVersion() {
        return debutVersion;
    }

    public Groupe debutVersion(Long debutVersion) {
        this.debutVersion = debutVersion;
        return this;
    }

    public void setDebutVersion(Long debutVersion) {
        this.debutVersion = debutVersion;
    }

    public Long getFinVersion() {
        return finVersion;
    }

    public Groupe finVersion(Long finVersion) {
        this.finVersion = finVersion;
        return this;
    }

    public void setFinVersion(Long finVersion) {
        this.finVersion = finVersion;
    }

    public Set<Entreprise> getEntreprises() {
        return entreprises;
    }

    public Groupe entreprises(Set<Entreprise> entreprises) {
        this.entreprises = entreprises;
        return this;
    }

    public Groupe addEntreprise(Entreprise entreprise) {
        entreprises.add(entreprise);
        entreprise.setGroupe(this);
        return this;
    }

    public Groupe removeEntreprise(Entreprise entreprise) {
        entreprises.remove(entreprise);
        entreprise.setGroupe(null);
        return this;
    }

    public void setEntreprises(Set<Entreprise> entreprises) {
        this.entreprises = entreprises;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Groupe groupe = (Groupe) o;
        if (groupe.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, groupe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Groupe{" +
            "id=" + id +
            ", nom='" + nom + "'" +
            ", debutVersion='" + debutVersion + "'" +
            ", finVersion='" + finVersion + "'" +
            '}';
    }
}
