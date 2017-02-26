##Get started

Je suis un nouveau développeur et j'ai envie de contribuer au projet "Toast Next Gen"  

###Voici quelques conseils avant de débuter

La documentation de JHipster 3.12.2 est disponible ici : https://jhipster.github.io/documentation-archive/v3.12.2 Elle donne l'ensemble des outils à télécharger avant de commencer à travailler sur un projet JHipster.

**Où est ce que je peux changer la configuration de l'environnement de dev (nom de la base de données, nom d'utilisateur, mot de passe, ...)**

Il faut éditer le fichier *src/main/resources/config/application-dev.yml*

**Je souhaite observer le modèle de données. Comment faire ?**

JHipster propose un outil très intéressant qui est [JDL Studio](https://jhipster.github.io/jdl-studio/). Pour charger le schéma de notre application il suffit d'importer dans JDL Studio le fichier *.jh contenu dans le répertoire jdl.

**Je souhaite utiliser le module Google Maps développé mais celui-ci ne fonctionne pas**

La clé googleapis utilisée est une clé personnelle. Celle-ci a pu être supprimée ou modifiée. Si vous voulez utiliser ce service il faut mettre sa propre clé.  
[Lien pour obtenir une clé](https://developers.google.com/maps/documentation/javascript/).

**(OPTIONNEL) Mettre en place Travis (intégration continue)**

Nous sommes soucieux de la qualité du code produit et nous avons souhaité nous prémunir d'éventuelles régressions en utilisant Travis.  
[Doc Travis](https://travis-ci.org/)

*N.B. : le repository Github doit être public*
