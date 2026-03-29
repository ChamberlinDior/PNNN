# PNPE Backend - version démo métier renforcée

Backend Spring Boot orienté métier PNPE, focalisé sur le parcours réel du demandeur d'emploi, le rôle du conseiller, le pôle accueil/scan, le suivi des entretiens, des entreprises, des formations et la vision DG sur les 7 antennes.

## Modules inclus
- Authentification JWT
- Référentiel agences / départements / rôles / conseillers
- Pré-inscription sur place ou depuis chez soi
- Conversion pré-inscription -> demandeur validé avec numéro OPEN
- Gestion documentaire (CV, CNI/CNIE, passeport, carte de séjour, permis, NIF, diplômes, attestations, pièces recto/verso, etc.)
- Suivi conseiller / historique métier / plan d'action
- Entretiens entreprise avec suivi post-entretien
- Formations et inscriptions en formation
- Insertions / placements
- Dashboard DG global par antenne

## Comptes de démo
- DG : dg@pnpe.ga / Admin@12345
- Super admin : admin@pnpe.ga / Admin@12345
- Conseiller : conseiller@pnpe.ga / Admin@12345
- Accueil : accueil@pnpe.ga / Admin@12345
- Pôle scan : scan@pnpe.ga / Admin@12345

## Endpoints principaux
- `POST /api/auth/login`
- `POST /api/public/pre-registrations`
- `POST /api/pre-registrations/{id}/validate?counselorId=...`
- `GET /api/job-seekers/search?keyword=...`
- `POST /api/documents/pre-registration/{id}`
- `POST /api/documents/job-seeker/{id}`
- `POST /api/interviews`
- `PATCH /api/interviews/{id}/status`
- `POST /api/trainings`
- `POST /api/trainings/enrollments`
- `POST /api/placements`
- `GET /api/dashboard/summary`

## Notes
Ce projet est pensé comme une base de démonstration métier très complète. Il reste volontairement ouvert pour qu'on puisse ensuite ajouter des règles plus fines, des validations documentaires plus strictes, des notifications, de la GED avancée et des autorisations plus granulaires.
