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

/**
 * A Entreprise.
 */
@Entity
@Table(name = "entreprise")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "entreprise")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Entreprise implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "pays")
    private String pays;

    @NotNull
    @Column(name = "num_siret", nullable = false)
    private String numSiret;

    @NotNull
    @Column(name = "num_siren", nullable = false)
    private String numSiren;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "date_creation", nullable = false, updatable = false)
    @CreatedDate
    private Long dateCreation;

    @Column(name = "date_modification")
    @LastModifiedDate
    private Long dateModification;

    @OneToMany(mappedBy = "entreprise")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotAudited
    private Set<Partenariat> partenariats = new HashSet<>();

    @OneToMany(mappedBy = "entrepriseSite")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotAudited
    private Set<Site> sites = new HashSet<>();

    @OneToMany(mappedBy = "entreprisePersonnel")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Professionnel> personnels = new HashSet<>();

    @OneToMany(mappedBy = "entreprise")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotAudited
    private Set<Taxe> taxes = new HashSet<>();

    @OneToOne(mappedBy = "entrepriseSiege")
    @JsonIgnore
    @NotAudited
    private Site siege;

    @OneToOne(mappedBy = "entrepriseContact")
    @JsonIgnore
    private Professionnel contact;

    @ManyToOne
    private Groupe groupe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public Entreprise nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPays() {
        return pays;
    }

    public Entreprise pays(String pays) {
        this.pays = pays;
        return this;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getNumSiret() {
        return numSiret;
    }

    public Entreprise numSiret(String numSiret) {
        this.numSiret = numSiret;
        return this;
    }

    public void setNumSiret(String numSiret) {
        this.numSiret = numSiret;
    }

    public String getNumSiren() {
        return numSiren;
    }

    public Entreprise numSiren(String numSiren) {
        this.numSiren = numSiren;
        return this;
    }

    public void setNumSiren(String numSiren) {
        this.numSiren = numSiren;
    }

    public String getTelephone() {
        return telephone;
    }

    public Entreprise telephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getDateCreation() {
        return dateCreation;
    }

    public Entreprise dateCreation(Long dateCreation) {
        this.dateCreation = dateCreation;
        return this;
    }

    public void setDateCreation(Long dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Long getDateModification() {
        return dateModification;
    }

    public Entreprise dateModification(Long dateModification) {
        this.dateModification = dateModification;
        return this;
    }

    public void setDateModification(Long dateModification) {
        this.dateModification = dateModification;
    }

    public Set<Partenariat> getPartenariats() {
        return partenariats;
    }

    public Entreprise partenariats(Set<Partenariat> partenariats) {
        this.partenariats = partenariats;
        return this;
    }

    public Entreprise addPartenariat(Partenariat partenariat) {
        partenariats.add(partenariat);
        partenariat.setEntreprise(this);
        return this;
    }

    public Entreprise removePartenariat(Partenariat partenariat) {
        partenariats.remove(partenariat);
        partenariat.setEntreprise(null);
        return this;
    }

    public void setPartenariats(Set<Partenariat> partenariats) {
        this.partenariats = partenariats;
    }

    public Set<Site> getSites() {
        return sites;
    }

    public Entreprise sites(Set<Site> sites) {
        this.sites = sites;
        return this;
    }

    public Entreprise addSite(Site site) {
        sites.add(site);
        site.setEntrepriseSite(this);
        return this;
    }

    public Entreprise removeSite(Site site) {
        sites.remove(site);
        site.setEntrepriseSite(null);
        return this;
    }

    public void setSites(Set<Site> sites) {
        this.sites = sites;
    }

    public Set<Professionnel> getPersonnels() {
        return personnels;
    }

    public Entreprise personnels(Set<Professionnel> professionnels) {
        this.personnels = professionnels;
        return this;
    }

    public Entreprise addPersonnel(Professionnel professionnel) {
        personnels.add(professionnel);
        professionnel.setEntreprisePersonnel(this);
        return this;
    }

    public Entreprise removePersonnel(Professionnel professionnel) {
        personnels.remove(professionnel);
        professionnel.setEntreprisePersonnel(null);
        return this;
    }

    public void setPersonnels(Set<Professionnel> professionnels) {
        this.personnels = professionnels;
    }

    public Set<Taxe> getTaxes() {
        return taxes;
    }

    public Entreprise taxes(Set<Taxe> taxes) {
        this.taxes = taxes;
        return this;
    }

    public Entreprise addTaxe(Taxe taxe) {
        taxes.add(taxe);
        taxe.setEntreprise(this);
        return this;
    }

    public Entreprise removeTaxe(Taxe taxe) {
        taxes.remove(taxe);
        taxe.setEntreprise(null);
        return this;
    }

    public void setTaxes(Set<Taxe> taxes) {
        this.taxes = taxes;
    }

    public Site getSiege() {
        return siege;
    }

    public Entreprise siege(Site site) {
        this.siege = site;
        return this;
    }

    public void setSiege(Site site) {
        this.siege = site;
    }

    public Professionnel getContact() {
        return contact;
    }

    public Entreprise contact(Professionnel professionnel) {
        this.contact = professionnel;
        return this;
    }

    public void setContact(Professionnel professionnel) {
        this.contact = professionnel;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public Entreprise groupe(Groupe groupe) {
        this.groupe = groupe;
        return this;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entreprise entreprise = (Entreprise) o;
        if (entreprise.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, entreprise.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Entreprise{" +
            "id=" + id +
            ", nom='" + nom + "'" +
            ", pays='" + pays + "'" +
            ", numSiret='" + numSiret + "'" +
            ", numSiren='" + numSiren + "'" +
            ", telephone='" + telephone + "'" +
            ", dateCreation='" + dateCreation + "'" +
            ", dateModification='" + dateModification + "'" +
            '}';
    }
}
