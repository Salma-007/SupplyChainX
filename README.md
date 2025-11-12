# 🚛 SupplyChainX – Système Intégré de Gestion de la Supply Chain

## 📦 Présentation
**SupplyChainX** est une application **monolithique Spring Boot** permettant la **gestion complète de la chaîne d’approvisionnement**, depuis l’achat des matières premières jusqu’à la livraison des produits finis.  
Elle vise à **automatiser**, **centraliser** et **tracer** l’ensemble des opérations liées à l’approvisionnement, la production et la distribution.

---

## ⚙️ Stack Technique
- **Backend** : Spring Boot 3.x  
- **ORM** : Hibernate / JPA  
- **Base de données** : MySQL ou PostgreSQL  
- **Migrations** : Liquibase  
- **Architecture** : MVC (Repository / Service / Controller)  
- **Documentation API** : Swagger / OpenAPI  
- **Mapping** : DTO + MapStruct  
- **Validation & Exceptions** : Bean Validation, `@ControllerAdvice`  
- **Sécurité (simulée)** : Spring AOP (vérification email + mot de passe via headers)  
- **Tests** : JUnit 5, Mockito, TestContainers *(bonus)*  

---

## 🧩 Modules Fonctionnels

### 1. Approvisionnement
- Gestion des **fournisseurs**, **matières premières** et **commandes d’approvisionnement**  
- Recherche, pagination, suppression conditionnelle  
- Suivi du **stock critique** (scheduler + email SMTP en option)

### 2. Production
- Gestion des **produits finis** et **ordres de production**  
- Vérification de la disponibilité des matières via la **BOM (Bill of Materials)**  
- Statuts : En attente / En production / Terminé / Bloqué  

### 3. Livraison & Distribution
- Gestion des **clients**, **commandes clients** et **livraisons**  
- Affectation véhicule / chauffeur, calcul du coût total  
- Statuts : Planifiée / En cours / Livrée  

---

## 👥 Gestion des Utilisateurs
Chaque utilisateur possède un **rôle unique** déterminant ses permissions :

| Module | Rôles |
|---------|--------|
| Approvisionnement | GESTIONNAIRE_APPROVISIONNEMENT, RESPONSABLE_ACHATS, SUPERVISEUR_LOGISTIQUE |
| Production | CHEF_PRODUCTION, PLANIFICATEUR, SUPERVISEUR_PRODUCTION |
| Livraison | GESTIONNAIRE_COMMERCIAL, RESPONSABLE_LOGISTIQUE, SUPERVISEUR_LIVRAISONS |
| Administration | ADMIN (accès complet) |

---

## 🧠 Objectifs du Développeur
- Implémenter une **application monolithique bien structurée**
- Respecter les **bonnes pratiques POO**
- Assurer la **traçabilité complète des flux**
- Produire un code **testé et maintenable**

---

## 🚀 Lancement du Projet

### 🧰 Prérequis
- Java 17+
- Maven 3+
- Docker & Docker Compose installés

### ▶️ Exécution
```bash
# Cloner le projet
git clone https://github.com/Salma-007/SupplyChainX
cd supplychainx

# Construire et exécuter
mvn clean install
docker-compose up --build
