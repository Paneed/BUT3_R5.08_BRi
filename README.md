Projet - Une plateforme de services dynamiques BRi - R5.08 - Qualité de développement

Membre de l'équipe :
- Phuong NGUYEN (304)
- Alexis SAYSANA (305)
- Shihong WANG (301)

Objectif :
Le projet BRi vise à concevoir une plateforme de services dynamiques.
L'objectif est de permettre à des programmeurs de publier, mettre à jour ou retirer des services à distance et à des amateurs (utilisateurs non programmeurs) d'accéder à des services dynamiquement, sans redémarrage du serveur.
Le projet introduit les concepts de modularité, communication client/serveur et gestion concurrente de services dynamiques.

Fonctionnalités principales :

- Serveur principal : BRiLaunch
- Deux clients :
  - ClientProgrammeur → publication / mise à jour / suppression de services
  - ClientAmateur → utilisation de services disponibles
- Chargement dynamique de classes via sockets
- Communication entre programmeurs et amateurs sur des ports distincts
- Gestion multi-clients avec threads
- Architecture modulaire et extensible
