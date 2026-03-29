package com.pnpe.backend.config;

import com.pnpe.backend.model.*;
import com.pnpe.backend.model.enums.*;
import com.pnpe.backend.repository.*;
import com.pnpe.backend.service.impl.SequenceGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    private static final String DOSSIER_PREFIX = "DEM-";
    private static final long DOSSIER_START = 1001L;

    private static final String OPEN_PREFIX = "OPEN-";
    private static final long OPEN_START = 50001L;

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository,
                               AgencyRepository agencyRepository,
                               DepartmentRepository departmentRepository,
                               UserRepository userRepository,
                               AgentProfileRepository agentProfileRepository,
                               JobSeekerRepository jobSeekerRepository,
                               CompanyRepository companyRepository,
                               InterviewRepository interviewRepository,
                               PreRegistrationRepository preRegistrationRepository,
                               EmploymentPlacementRepository employmentPlacementRepository,
                               TrainingProgramRepository trainingProgramRepository,
                               TrainingEnrollmentRepository trainingEnrollmentRepository,
                               CounselorActionRepository counselorActionRepository,
                               PasswordEncoder passwordEncoder,
                               SequenceGenerator sequenceGenerator) {
        return args -> {
            if (roleRepository.count() == 0) {
                for (RoleName roleName : RoleName.values()) {
                    Role role = new Role();
                    role.setName(roleName);
                    role.setLabel(roleName.name());
                    roleRepository.save(role);
                }
            }

            if (agencyRepository.count() == 0) {
                List<Agency> agencies = new ArrayList<>();
                agencies.add(buildAgency("LBV-SIEGE", "PNPE Siège Libreville", "Libreville", "Estuaire", true));
                agencies.add(buildAgency("PG-ANT", "PNPE Port-Gentil", "Port-Gentil", "Ogooué-Maritime", false));
                agencies.add(buildAgency("FRA-ANT", "PNPE Franceville", "Franceville", "Haut-Ogooué", false));
                agencies.add(buildAgency("OYM-ANT", "PNPE Oyem", "Oyem", "Woleu-Ntem", false));
                agencies.add(buildAgency("MOU-ANT", "PNPE Mouila", "Mouila", "Ngounié", false));
                agencies.add(buildAgency("LIB-ANT", "PNPE Lambaréné", "Lambaréné", "Moyen-Ogooué", false));
                agencies.add(buildAgency("TCH-ANT", "PNPE Tchibanga", "Tchibanga", "Nyanga", false));
                agencyRepository.saveAll(agencies);

                Agency siege = agencies.get(0);
                Department accueil = buildDepartment("Accueil et orientation", DepartmentType.ADMINISTRATION, siege);
                Department insertion = buildDepartment("Conseil emploi et insertion", DepartmentType.INSERTION, siege);
                Department formation = buildDepartment("Formation et accompagnement", DepartmentType.FORMATION, siege);
                departmentRepository.save(accueil);
                departmentRepository.save(insertion);
                departmentRepository.save(formation);

                User dg = buildUser(
                        "Directeur", "General", "dg@pnpe.ga", "+24101000000",
                        "Directeur Général",
                        roleRepository.findByName(RoleName.ROLE_DG).orElseThrow(),
                        siege, insertion, passwordEncoder
                );

                User admin = buildUser(
                        "Super", "Admin", "admin@pnpe.ga", "+24101000001",
                        "Super administrateur",
                        roleRepository.findByName(RoleName.ROLE_SUPER_ADMIN).orElseThrow(),
                        siege, insertion, passwordEncoder
                );

                User accueilUser = buildUser(
                        "Rose", "Mbadinga", "accueil@pnpe.ga", "+24101000002",
                        "Agent d'accueil",
                        roleRepository.findByName(RoleName.ROLE_ACCUEIL).orElseThrow(),
                        siege, accueil, passwordEncoder
                );

                User scanUser = buildUser(
                        "Paul", "Ekoga", "scan@pnpe.ga", "+24101000003",
                        "Opérateur pôle scan",
                        roleRepository.findByName(RoleName.ROLE_POLE_SCAN).orElseThrow(),
                        siege, accueil, passwordEncoder
                );

                User counselorUser = buildUser(
                        "Marina", "Nzeng", "conseiller@pnpe.ga", "+24101000004",
                        "Conseillère emploi",
                        roleRepository.findByName(RoleName.ROLE_CONSEILLER).orElseThrow(),
                        siege, insertion, passwordEncoder
                );

                userRepository.save(dg);
                userRepository.save(admin);
                userRepository.save(accueilUser);
                userRepository.save(scanUser);
                userRepository.save(counselorUser);

                AgentProfile counselor = new AgentProfile();
                counselor.setUser(counselorUser);
                counselor.setAgentCode("CNS-0001");
                counselor.setMonthlyTargetInsertions(3);
                counselor.setMonthlyTargetInterviews(25);
                counselor.setCounselor(true);
                counselor.setSpecialty("Insertion générale");
                agentProfileRepository.save(counselor);

                PreRegistration preRegistration = new PreRegistration();
                preRegistration.setRequestNumber(sequenceGenerator.nextPreRegistrationNumber());
                preRegistration.setFirstName("Chamberlin");
                preRegistration.setLastName("Mouloungui");
                preRegistration.setPhone("+24106000000");
                preRegistration.setEmail("chamberlin@example.com");
                preRegistration.setCity("Libreville");
                preRegistration.setDateOfBirth(LocalDate.of(1994, 9, 12));
                preRegistration.setEducationLevel("Licence");
                preRegistration.setPrimarySkill("Développement Java");
                preRegistration.setAutonomousOnPortal(true);
                preRegistration.setHasRequiredDocuments(true);
                preRegistration.setProjectSummary("Recherche un emploi dans le développement logiciel.");
                preRegistration.setWelcomeNotes("Pré-inscription à distance en attente de validation conseiller.");
                preRegistration.setSubmittedAt(LocalDateTime.now().minusDays(2));
                preRegistration.setRegistrationChannel(RegistrationChannel.HOME);
                preRegistration.setStatus(PreRegistrationStatus.READY_FOR_COUNSELOR);
                preRegistration.setAgency(siege);
                preRegistration.setReferredCounselor(counselor);
                preRegistrationRepository.save(preRegistration);

                JobSeeker seeker = new JobSeeker();
                seeker.setDossierNumber(generateNextDossierNumber(jobSeekerRepository));
                seeker.setOpenNumber(generateNextOpenNumber(jobSeekerRepository));
                seeker.setFirstName("Jean");
                seeker.setLastName("Mouloungui");
                seeker.setGender(Gender.MALE);
                seeker.setDateOfBirth(LocalDate.of(1999, 5, 10));
                seeker.setPhone("+24122222222");
                seeker.setEmail("jean@example.com");
                seeker.setCity("Libreville");
                seeker.setAddress("Nzeng-Ayong");
                seeker.setEducationLevel("Licence");
                seeker.setPrimarySkill("Comptabilité");
                seeker.setProjectSummary("Souhaite intégrer un poste d'assistant comptable.");
                seeker.setActionPlanSummary("Actualisation CV, 2 entretiens, atelier soft skills.");
                seeker.setSelfRegistered(false);
                seeker.setStatus(JobSeekerStatus.ACTIVE);
                seeker.setAgency(siege);
                seeker.setAssignedAgent(counselor);
                seeker.setRegistrationValidatedAt(LocalDateTime.now().minusDays(10));
                jobSeekerRepository.save(seeker);

                Company total = new Company();
                total.setName("TotalEnergies Gabon");
                total.setSector("Énergie");
                total.setCity("Port-Gentil");
                total.setAddress("Port-Gentil");
                total.setContactName("Mme Obame");
                total.setContactEmail("rh@total.ga");
                total.setContactPhone("+24133333333");
                total.setPartnershipNotes("Partenaire stratégique pour les entretiens et insertions.");
                total.setStatus(CompanyStatus.STRATEGIC_PARTNER);
                companyRepository.save(total);

                Interview interview = new Interview();
                interview.setJobSeeker(seeker);
                interview.setCompany(total);
                interview.setAgentProfile(counselor);
                interview.setJobTitle("Assistant comptable");
                interview.setInterviewDate(LocalDateTime.now().plusDays(2));
                interview.setLocation("Siège TotalEnergies");
                interview.setMode("Présentiel");
                interview.setStatus(InterviewStatus.SCHEDULED);
                interviewRepository.save(interview);

                CounselorAction action = new CounselorAction();
                action.setJobSeeker(seeker);
                action.setCounselor(counselor);
                action.setActionType(CounselorActionType.ACTION_PLAN_CREATED);
                action.setActionDate(LocalDateTime.now().minusDays(8));
                action.setSummary("Plan d'action initial défini");
                action.setDetails("Mise à jour du CV, orientation vers entretien et suivi téléphonique.");
                counselorActionRepository.save(action);

                TrainingProgram training = new TrainingProgram();
                training.setTitle("Soft skills et préparation à l'entretien");
                training.setDescription("Atelier court pour améliorer la présentation, la ponctualité et la communication en entretien.");
                training.setTrainerName("Service accompagnement PNPE");
                training.setLocation("PNPE Siège Libreville");
                training.setStartDate(LocalDate.now().plusDays(5));
                training.setEndDate(LocalDate.now().plusDays(7));
                training.setCapacity(25);
                training.setStatus(TrainingStatus.PLANNED);
                training.setAgency(siege);
                trainingProgramRepository.save(training);

                TrainingEnrollment enrollment = new TrainingEnrollment();
                enrollment.setTrainingProgram(training);
                enrollment.setJobSeeker(seeker);
                enrollment.setStatus(EnrollmentStatus.ASSIGNED);
                trainingEnrollmentRepository.save(enrollment);
            }
        };
    }

    private String generateNextDossierNumber(JobSeekerRepository jobSeekerRepository) {
        Long maxValue = jobSeekerRepository.findMaxDossierNumberValue();
        long nextValue = (maxValue == null || maxValue < DOSSIER_START) ? DOSSIER_START : maxValue + 1;

        String candidate = DOSSIER_PREFIX + nextValue;
        while (jobSeekerRepository.existsByDossierNumber(candidate)) {
            nextValue++;
            candidate = DOSSIER_PREFIX + nextValue;
        }
        return candidate;
    }

    private String generateNextOpenNumber(JobSeekerRepository jobSeekerRepository) {
        Long maxValue = jobSeekerRepository.findMaxOpenNumberValue();
        long nextValue = (maxValue == null || maxValue < OPEN_START) ? OPEN_START : maxValue + 1;

        String candidate = OPEN_PREFIX + nextValue;
        while (jobSeekerRepository.existsByOpenNumber(candidate)) {
            nextValue++;
            candidate = OPEN_PREFIX + nextValue;
        }
        return candidate;
    }

    private Agency buildAgency(String code, String name, String city, String province, boolean headquarters) {
        Agency agency = new Agency();
        agency.setCode(code);
        agency.setName(name);
        agency.setCity(city);
        agency.setProvince(province);
        agency.setHeadquarters(headquarters);
        agency.setActive(true);
        return agency;
    }

    private Department buildDepartment(String name, DepartmentType type, Agency agency) {
        Department department = new Department();
        department.setName(name);
        department.setType(type);
        department.setAgency(agency);
        return department;
    }

    private User buildUser(String firstName, String lastName, String email, String phone,
                           String jobTitle, Role role, Agency agency, Department department,
                           PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setJobTitle(jobTitle);
        user.setPassword(passwordEncoder.encode("Admin@12345"));
        user.setRole(role);
        user.setAgency(agency);
        user.setDepartment(department);
        return user;
    }
}