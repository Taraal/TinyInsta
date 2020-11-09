Tiny Insta
==================

Application Instagram-like propulsée par Google App Engine

## Setup

Dans votre pom.xml, changez {project-id} par votre ID de projet GAE

    <configuration>
        <appId>{project-id}</appId>
    </configuration>

Même chose pour src/main/webapp/WEB-INF/appengine-web.xml

## Maven
### Run en local

    mvn appengine:run

### Déployer

    mvn appengine:deploy

## Comment utiliser git et contributer au projet
### Commandes basiques
#### Cloner le projet
    git clone https://github.com/Taraal/TinyInsta
#### Voir le statut de votre projet git
    git status
#### Ajouter des fichiers au Git
    git add -A  #Attention, ajoute TOUS les fichiers du dossier et des sous-dossiers
    git add <nom> #Ajoute un fichier ou dossier
#### Commit des changements
    git commit -m "Votre message" #Ecrire un message descriptif de ce que vous avez modifié
    OU 
    git commit -a -m "Votre message" #Equivalent à git add -A + git commit -m
#### Effacer le dernier commit effectué
    git reset HEAD~
#### Mettre les changements en ligne
    git push 

### Gestion des branches
#### Créer une branche
    git branch <nom de la branche>
#### Changer de branche
    git checkout <nom de la branche>
#### Merger la branche B sur la branche A
    git checkout A 
    git merge B
