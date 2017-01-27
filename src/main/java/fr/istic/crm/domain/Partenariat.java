package fr.istic.crm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Partenariat.
 */
@Entity
@Table(name = "partenariat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "partenariat")
public class Partenariat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date_debut")
    private ZonedDateTime dateDebut;

    @Column(name = "date_fin")
    private ZonedDateTime dateFin;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "partenariat_diplome",
               joinColumns = @JoinColumn(name="partenariats_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="diplomes_id", referencedColumnName="ID"))
    private Set<Diplome> diplomes = new HashSet<>();

    @ManyToOne
    private Entreprise entreprise;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateDebut() {
        return dateDebut;
    }

    public Partenariat dateDebut(ZonedDateTime dateDebut) {
        this.dateDebut = dateDebut;
        return this;
    }

    public void setDateDebut(ZonedDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public ZonedDateTime getDateFin() {
        return dateFin;
    }

    public Partenariat dateFin(ZonedDateTime dateFin) {
        this.dateFin = dateFin;
        return this;
    }

    public void setDateFin(ZonedDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Set<Diplome> getDiplomes() {
        return diplomes;
    }

    public Partenariat diplomes(Set<Diplome> diplomes) {
        this.diplomes = diplomes;
        return this;
    }

    public Partenariat addDiplome(Diplome diplome) {
        diplomes.add(diplome);
        diplome.getPartenariats().add(this);
        return this;
    }

    public Partenariat removeDiplome(Diplome diplome) {
        diplomes.remove(diplome);
        diplome.getPartenariats().remove(this);
        return this;
    }

    public void setDiplomes(Set<Diplome> diplomes) {
        this.diplomes = diplomes;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public Partenariat entreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
        return this;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Partenariat partenariat = (Partenariat) o;
        if (partenariat.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, partenariat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Partenariat{" +
            "id=" + id +
            ", dateDebut='" + dateDebut + "'" +
            ", dateFin='" + dateFin + "'" +
            '}';
    }
}
