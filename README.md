Tiny Insta
==================

Application Instagram-like propulsée par Google App Engine

## Setup

Dans votre pom.xml, changez {project-id} par votre ID de projet GAE

    <configuration>
        <appId>{project-id}</appId>
    </configuration>

Même chose pour src/main/webapp/WEB-INF/appengine-web.xml

## To Do

- [ ] Authentification Google
- [ ] Créer un post
- [ ] Lister les posts
- [ ] Follow un autre utilisateur
- [ ] Liker un post
- [ ] Interface
- [ ] Benchmarks

# Aide au suivi
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

### Workflow complet 
#### Si feature majeure : création de branche
    git branch authentification

#### Workflow basique
    git pull
    # Codez sur nomDuFichier.java
    git add nomDuFichier.java
    git commit -m "Ajout de la classe nomDuFichier"
    git push

#### A la fin d'une feature majeure : merge de branche
    git checkout master
    git merge authentification
    git push
------------------------------------------------------------------------------------------------------------------------------------------------------------------
Projet TinyGram - ClémentPicard ~ Sylouan Corfa ~ Jules Roger ~ Kader Salifou

    url App Engine : https://tinyinsta-295119.appspot.com/
    url GitHub : https://github.com/Taraal/TinyInsta
    url interface REST : https://endpointsportal.tinyinsta-295119.cloud.goog/


Le but de ce projet était de réaliser une version réduite d'un application comme Instagram. Pour cela il nous fallait répondre à certaines contraintes. Tout d'abord, il fallait rendre l'application fonctionnelle selon certains critères.

- [ ] Authentification d'un utilisateur par son compte Google
- [ ] Créer un post (avec ou sans photo associé) lié à l'utilisateur connecté
- [ ] Lister les posts des utilisateurs que celui connecté follow
- [ ] Checher d'autres utilisateurs inscrits sur le site 
- [ ] Follow un autre utilisateur
- [ ] Like / Dislike un post
- [ ] Créer une interface graphique


Outre le fait de rendre l'application fonctionnelle, il nous a fallu travailler sur le scaling. En effet, pour pouvoir créer une application viable faut programmer de sorte que le nombre d'utilisateurs n'influe pas ou peu sur les temps de réponse du site. Il faut que cela scale ! Nous avons pour cela mis au point des fonctions de benchmark permettant de tester notre TinyGram.

- [ ] Test pour poster un message si on est follow par 10, 100, 500 followers
- [ ] Combien de likes peut on faire par seconde
- [ ] Combien de temps met l'application à retrouver les 10, 100, 500 derniers posts



//Ce quil reste a rediger :
ce qui marche 
ce qui ne marche pas 
temps benchmark
ce qu'on aurait pu faire si on avait eu le time
les diffcultées quon a eu
screens des kinds google db
does it scale ?
    

