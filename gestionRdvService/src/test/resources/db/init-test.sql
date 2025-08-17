-- Script d'initialisation pour les tests avec PostgreSQL
-- Création des types ENUM requis par l'application

DO $$
BEGIN
    -- Créer le type ENUM pour les spécialités médicales
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'specialite_medicale_enum') THEN
        CREATE TYPE specialite_medicale_enum AS ENUM (
            'GENERALISTE',
            'CARDIOLOGIE',
            'DERMATOLOGIE', 
            'NEUROLOGIE',
            'PEDIATRIE',
            'GYNECOLOGIE',
            'OPHTALMOLOGIE',
            'ORL',
            'PSYCHIATRIE',
            'RADIOLOGIE',
            'CHIRURGIE',
            'ANESTHESIE',
            'DENTAIRE'
        );
    END IF;

    -- Créer le type ENUM pour les statuts de rendez-vous
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'rendez_vous_status_enum') THEN
        CREATE TYPE rendez_vous_status_enum AS ENUM (
            'PLANIFIE',
            'CONFIRME',
            'EN_COURS', 
            'TERMINE',
            'ANNULE',
            'ABSENT'
        );
    END IF;

    -- Créer le type ENUM pour les jours de la semaine
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'jour_semaine_enum') THEN
        CREATE TYPE jour_semaine_enum AS ENUM (
            'LUNDI',
            'MARDI',
            'MERCREDI',
            'JEUDI',
            'VENDREDI',
            'SAMEDI',
            'DIMANCHE'
        );
    END IF;
END $$;