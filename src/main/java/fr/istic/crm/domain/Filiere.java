package fr.istic.crm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Filiere.
 */
@Entity
@Table(name = "filiere")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "filiere")
public class Filiere implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "niveau", nullable = false)
    private String niveau;

    @OneToMany(mappedBy = "filiere")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Promotion> promotions = new HashSet<>();

    @ManyToOne
    private Diplome diplome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNiveau() {
        return niveau;
    }

    public Filiere niveau(String niveau) {
        this.niveau = niveau;
        return this;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public Set<Promotion> getPromotions() {
        return promotions;
    }

    public Filiere promotions(Set<Promotion> promotions) {
        this.promotions = promotions;
        return this;
    }

    public Filiere addPromotion(Promotion promotion) {
        promotions.add(promotion);
        promotion.setFiliere(this);
        return this;
    }

    public Filiere removePromotion(Promotion promotion) {
        promotions.remove(promotion);
        promotion.setFiliere(null);
        return this;
    }

    public void setPromotions(Set<Promotion> promotions) {
        this.promotions = promotions;
    }

    public Diplome getDiplome() {
        return diplome;
    }

    public Filiere diplome(Diplome diplome) {
        this.diplome = diplome;
        return this;
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
        Filiere filiere = (Filiere) o;
        if (filiere.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, filiere.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Filiere{" +
            "id=" + id +
            ", niveau='" + niveau + "'" +
            '}';
    }
}
