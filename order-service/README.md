# Order Service - Microservice de Gestion des Commandes

## Description

Ce microservice gère les commandes dans un système e-commerce. Il suit une architecture Domain-Driven Design (DDD) avec une architecture hexagonale, implémentée avec Spring Boot.

## Architecture

Le projet suit les principes DDD avec une séparation claire des couches :

- **Domain** : Contient la logique métier pure
  - `model/aggregate` : Agrégats (Order)
  - `model/entity` : Entités (OrderLine, OrderModificationHistory)
  - `model/valueobject` : Objets valeur (OrderStatus, DeliveryAddress, etc.)
  - `service` : Services du domaine
  - `repository` : Interfaces des repositories
  - `event` : Événements du domaine

- **Application** : Couche d'orchestration
  - `service` : Services applicatifs
  - `dto` : Objets de transfert de données
  - `eventhandler` : Gestionnaires d'événements

- **Infrastructure** : Implémentations techniques
  - `controller` : Contrôleurs REST
  - `repository` : Implémentations des repositories
  - `persistence` : Entités JPA et mappers
  - `messaging` : Intégration Kafka
  - `security` : Configuration de sécurité
  - `config` : Configurations diverses

## Technologies Utilisées

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **Spring Kafka**
- **PostgreSQL**
- **Flyway** (migrations de base de données)
- **MapStruct** (mapping d'objets)
- **OpenAPI/Swagger** (documentation API)
- **Maven**

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- PostgreSQL 12 ou supérieur
- Apache Kafka (optionnel pour les tests)

## Configuration

### Base de données

1. Créer une base de données PostgreSQL :
```sql
CREATE DATABASE order_db;
CREATE USER order_user WITH PASSWORD 'order_password';
GRANT ALL PRIVILEGES ON DATABASE order_db TO order_user;
```

2. Les migrations Flyway se chargeront de créer les tables automatiquement.

### Variables d'environnement

Vous pouvez configurer les variables suivantes :

- `DB_USERNAME` : Nom d'utilisateur de la base de données (défaut: order_user)
- `DB_PASSWORD` : Mot de passe de la base de données (défaut: order_password)
- `KAFKA_BOOTSTRAP_SERVERS` : Serveurs Kafka (défaut: localhost:9092)
- `JWT_SECRET` : Clé secrète pour JWT (défaut: mySecretKey...)

## Installation et Démarrage

### 1. Cloner le projet
```bash
git clone <repository-url>
cd order-service
```

### 2. Compiler le projet
```bash
mvn clean compile
```

### 3. Exécuter les tests
```bash
mvn test
```

### 4. Démarrer l'application
```bash
mvn spring-boot:run
```

Ou avec un profil spécifique :
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Construire le JAR
```bash
mvn clean package
java -jar target/order-service-1.0.0-SNAPSHOT.jar
```

## API Documentation

Une fois l'application démarrée, la documentation Swagger est disponible à :
- **Swagger UI** : http://localhost:8082/api/v1/swagger-ui/index.html
- **OpenAPI JSON** : http://localhost:8082/api/v1/v3/api-docs

## Endpoints Principaux

### Commandes
- `POST /api/v1/orders` - Créer une nouvelle commande
- `GET /api/v1/orders/{id}` - Récupérer une commande par ID
- `GET /api/v1/orders/number/{orderNumber}` - Récupérer une commande par numéro
- `GET /api/v1/orders/customer/{customerId}` - Récupérer les commandes d'un client
- `GET /api/v1/orders` - Rechercher des commandes avec filtres
- `PUT /api/v1/orders/{id}/status` - Mettre à jour le statut d'une commande
- `POST /api/v1/orders/{id}/confirm` - Confirmer une commande
- `POST /api/v1/orders/{id}/cancel` - Annuler une commande
- `GET /api/v1/orders/{id}/history` - Récupérer l'historique d'une commande

### Monitoring
- `GET /api/v1/actuator/health` - Vérification de santé
- `GET /api/v1/actuator/metrics` - Métriques de l'application

## Statuts des Commandes

Les commandes suivent un cycle de vie défini :

1. **PENDING** - En attente
2. **CONFIRMED** - Confirmée
3. **PAID** - Payée
4. **PREPARING** - En préparation
5. **READY_FOR_SHIPMENT** - Prête pour expédition
6. **SHIPPED** - Expédiée
7. **DELIVERED** - Livrée
8. **CANCELLED** - Annulée
9. **REFUNDED** - Remboursée

## Événements Kafka

Le service publie les événements suivants :
- `OrderPlaced` - Commande créée
- `OrderConfirmed` - Commande confirmée
- `OrderStatusChanged` - Statut de commande modifié
- `OrderCancelled` - Commande annulée
- `OrderReadyForShipment` - Commande prête pour expédition

## Sécurité

- Authentification JWT
- Autorisation basée sur les rôles (USER, ADMIN, OPERATOR)
- CORS configuré pour le développement
- Validation des données d'entrée

## Profils d'Environnement

- **dev** : Environnement de développement (H2 en mémoire)
- **prod** : Environnement de production (PostgreSQL)
- **test** : Environnement de test

## Structure de la Base de Données

### Tables principales :
- `orders` : Commandes principales
- `order_lines` : Lignes de commande (produits)
- `order_modification_history` : Historique des modifications

### Index optimisés pour :
- Recherches par client
- Recherches par statut
- Recherches par date
- Recherches par numéro de commande

## Développement

### Ajout de nouvelles fonctionnalités

1. **Domaine** : Ajouter la logique métier dans les agrégats/entités
2. **Application** : Créer les services applicatifs et DTOs
3. **Infrastructure** : Implémenter les contrôleurs et repositories

### Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify

# Couverture de code
mvn jacoco:report
```

## Déploiement

### Docker
```dockerfile
FROM openjdk:17-jre-slim
COPY target/order-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes
Des manifests Kubernetes peuvent être ajoutés dans le dossier `k8s/`.

## Monitoring et Observabilité

- **Actuator** : Endpoints de monitoring Spring Boot
- **Micrometer** : Métriques pour Prometheus
- **Logging** : Configuration Logback avec niveaux par environnement

## Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -am 'Ajout nouvelle fonctionnalité'`)
4. Push vers la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créer une Pull Request

## Support

Pour toute question ou problème, veuillez créer une issue dans le repository.

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

