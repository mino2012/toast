#Versioning avec JBoss
## Mise en place de jBoss
L’informatique décisionnelle nous permet de répondre à cette problématique avec les
dimensions à évolution lente (slowly changing dimension ou SCD). Il existe 6 types de SCD plus
des types combinés. La liste des différents types est disponible sur wikipedia
https://en.wikipedia.org/wiki/Slowly_changing_dimension. Après étude des différents types et
des recherches sur les technologies de versioning d’entité, nous avons choisi jBoss d’Hibernate
Envers.
Cette technologie suit le type 4 de SCD avec une table d’entité et une table d’historique.
3 actions sont écoutées par jBoss, la création, la modification et la suppression. Ainsi lors d’une
de ces actions, une ligne sera rajoutée dans la table d’historique avec l’ID correspondant à l’entité
versionnée.
Dans notre projet nous versionnons les sites des entreprises, les entreprises ainsi que les
diplômes. Ces 3 entités nous permettent de restituer le contexte d’une convention de stage à un
moment donnée.
Voici la table des entreprises dans notre base de données. En plus des éléments d’une
entreprise, nous avons rajouté une colonne “date_creation” et une colonne “date_modification”
qui correspondent à des dates dans le type Long. Ces données sont générées automatiquement
par Spring que nous verrons un peu plus loin.

![Image of table Entreprise](http://img11.hostingpics.net/pics/444051tableentreprise.png)

Prenons l’exemple de l’entreprise Sopra-Stéria Rennes Ouest. On remarque très rapidement que
des modifications ont été apportées sur cette entreprise car la date de modification et de
création ne sont pas identiques. En allant voir dans notre table “Entreprise_aud”, comprenez
table d’historique des entreprises, pour l’ID numéro 1 (celui de Sopra-Stéria Rennes Ouest) nous
avons 2 révisions. 

![Image of table Entreprise Aud](http://img11.hostingpics.net/pics/941984tableentrepriseaud.png)

La colonne ‘revtype’ nous permet de savoir quelle action a été faite sur cet entité, 0 pour un
ajout, 1 pour la modification et 2 pour une suppression. jBoss nous permet donc d’avoir un
traçage sur chaque action d’entité. Voyons maintenant comment nous l’avons mis en place dans
notre application.

## La gestion des dates avec Spring
Nous nous sommes basés sur les dates de modification pour pouvoir récupérer un contexte
d’une convention de stage. L’idée est simple, pour un stage donné, nous avons une entreprise et
nous souhaitons récupérer la version de l’entreprise la plus récente avant la date de début du
stage. L’équation donne donc : entreprise.date_modification <= stage.date_debut (cf. Annexe 5 :
Etude de la récupération du contexte d’un stage page 49). Pour implémenter nos dates nous
avons mis en place Spring avec les annotations @CreatedDate et @LastModifiedDate. Dans
notre exemple d’entreprise voici ce que cela donne : 

```java
@Column(name = "date_creation", nullable = false, updatable = false)
@CreatedDate
private Long dateCreation;

@Column(name = "date_modification")
@LastModifiedDate
private Long dateModification;
```

## La récupération du contexte d’un stage avec jBoss
Une fois notre table de versionning et notre système de gestion des dates en place, il nous reste
plus qu’à faire le système de récupération du contexte d’un stage. Pour cela nous avons mis en
place des nouveaux services. Le premier concerne la récupération des anciennes versions d’une
entité donnée comme par exemple la récupération des anciennes versions de la société SopraStéria.
Ce service permet d’avoir une vue d’ensemble sur les modifications d’une entité.
jBoss propose une librairie afin de générer des requête et d’interroger notre table
d’historique. Toute la documentation concernant ces fonctions est disponible ici
http://docs.jboss.org/envers/docs/index.html#queries. 

```java
@Transactional(readOnly = true)
public List findAnciennesVersions(Long id) {
    init();
    log.debug("Request to get Entreprise : {}", id);

    List anciennesVersions = reader.createQuery()
        .forRevisionsOfEntity(Entreprise.class, false, true)
        .add(AuditEntity.id().eq(id))
        .getResultList();

    log.debug("OLD VERSION" + anciennesVersions);

    return anciennesVersions;
}
```

La fonction init() permet d’initialiser le reader (AuditQueryReader) récupéré via l’EntityManager.
.forRevisionsOfEntity permet d’indiquer que nous voulons récupérer des versions d’entreprise.
.add(AuditEntity.id().eq(id)) ajoute une contrainte à notre requête (un Where). On lui demande
de récupérer les versions d’une entreprise de l’ID donné.
On récupère alors un objet jBoss contenant la listes des révisions ainsi que l’action faite sur
chacunes d’entre elles. L’appel au service se fait alors simplement avec Angular qui interrogera 
une API que nous avons configuré dans notre fichier ressource de l’entité entreprise. Voici le
résultat pour l’entreprise Sopra-Stéria :

![Image of table history](http://img11.hostingpics.net/pics/211529historiquemodif.png)

Le deuxième service concerne la récupération d’une version d’une entité en particulier. C’est ce
service qui nous permettra de récupérer le contexte d’un stage. Reprenons notre équation
entreprise.date_modification <= stage.date_debut. Nous souhaitons générer une requête
répondant à celle-ci. 

```java
@Transactional(readOnly = true)
public Object findEntrepriseAtCreationStage(Long id) {
    log.debug("Request to get ConventionStage : {}", id);
    ConventionStage stage = conventionStageRepository.findOne(id);
    if (stage.getDateDebut() != null) {
        Instant instant = stage.getDateDebut().toInstant();
        Date dateDebutStage = java.util.Date.from(instant);
        reader = AuditReaderFactory.get(manager);
        Object revision = reader.createQuery()
            .forRevisionsOfEntity(Entreprise.class, false, true)
            // We are only interested in the first revision
            .add(AuditEntity.id().eq(stage.getLieuStage().getEntrepriseSite().getId()))
            .add(AuditEntity.property("dateModification").le(dateDebutStage.getTime()))
            .addOrder(AuditEntity.property("dateModification").desc())
            .getResultList();
        return revision;
    } else {
        log.error("CONVENTION : Date de début de stage indisponible");
        return null;
    }
}
```

C’est le même principe côté jBoss on lui indique qu’on souhaite récupérer des versions
d’Entreprise avec .forRevisionOfEntity et on lui applique des filtres.
Le premier .add indique qu’on récupère les versions pour l’id correspondant à l’entreprise du
lieu du stage (dont l’id est en paramètre de la fonction). Le deuxième .add est la traduction de
notre équation en jBoss. On lui demande de faire un filtre “Less or equal Than” (le) sur la
propriété DateModification par rapport à la date de début de stage qu’on aura préalablement
traduit en long. Puis un applique un order by sur cette même propriété afin d’avoir la version la
plus récente avant le début du stage en premier dans notre liste. 

Ce service est ensuite appelé lors de l’affichage des conventions de stages afin d’afficher les
informations correspondant à la date de début du stage. 

```javascript

function onSuccess(data, headers) {
    vm.links = ParseLinks.parse(headers('link'));
    vm.totalItems = headers('X-Total-Count');
    vm.queryCount = vm.totalItems;
    /* Update site at date creation of convention */
    angular.forEach(data, function (convention) {
        /* Call synchrone function to wait the answer */
        $http({
            url: 'api/siteCreationStage/',
            method: "GET",
            params: {id: convention.id}
        }).then(function (response) {
            if (response) {
                response = angular.fromJson(response);
                convention.lieuStageAdresse = response.data[0][0].adresse;
            }
        });
        $http({
            url: 'api/entrepriseCreationStage/',
            method: "GET",
            params: {id: convention.id}
        }).then(function (response) {
            if (response) {
                response = angular.fromJson(response);
                convention.entreprise.nom = response.data[0][0].nom;
            }
        });
    });
    vm.conventionStages = data;
    vm.page = pagingParams.page;
    }
```

On obtient le résultat suivant, avec les informations correspondant à la date de début du stage
pour une même entité.

![Image of ConventionStage](http://img11.hostingpics.net/pics/522175conventionstage.png)

