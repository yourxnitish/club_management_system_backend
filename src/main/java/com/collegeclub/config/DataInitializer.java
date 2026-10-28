package com.collegeclub.config;

import com.collegeclub.entity.*;
import com.collegeclub.enums.*;
import com.collegeclub.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ClubAdminAssignmentRepository assignmentRepository;
    private final ClubMembershipRepository membershipRepository;
    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final AnnouncementRepository announcementRepository;
    private final EventFeedbackRepository feedbackRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ClubRepository clubRepository,
                           ClubAdminAssignmentRepository assignmentRepository,
                           ClubMembershipRepository membershipRepository,
                           EventRepository eventRepository,
                           EventRegistrationRepository registrationRepository,
                           AnnouncementRepository announcementRepository,
                           EventFeedbackRepository feedbackRepository,
                           ActivityLogRepository activityLogRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.assignmentRepository = assignmentRepository;
        this.membershipRepository = membershipRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.announcementRepository = announcementRepository;
        this.feedbackRepository = feedbackRepository;
        this.activityLogRepository = activityLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Checking database initialization status...");

        // 1. Seed Users if not present
        if (userRepository.count() == 0) {
            logger.info("Seeding initial users and administrative accounts...");

            String adminPass = passwordEncoder.encode("Admin@123456");
            String studentPass = passwordEncoder.encode("Student@123456");

            User admin = new User("admin@collegeclub.edu", adminPass, "Victoria", "Sterling", Role.SYSTEM_ADMIN);
            admin.setDepartment("University Administration");
            admin.setPhone("+1-555-0100");
            admin.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150");
            admin = userRepository.save(admin);

            User roboticsAdmin = new User("robotics.lead@collegeclub.edu", adminPass, "Marcus", "Vance", Role.CLUB_ADMIN);
            roboticsAdmin.setStudentId("STU-2022-041");
            roboticsAdmin.setDepartment("Computer Science & Robotics");
            roboticsAdmin.setPhone("+1-555-0101");
            roboticsAdmin.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150");
            roboticsAdmin = userRepository.save(roboticsAdmin);

            User artsAdmin = new User("arts.lead@collegeclub.edu", adminPass, "Elena", "Rostova", Role.CLUB_ADMIN);
            artsAdmin.setStudentId("STU-2022-089");
            artsAdmin.setDepartment("Visual Arts & Media");
            artsAdmin.setPhone("+1-555-0102");
            artsAdmin.setAvatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150");
            artsAdmin = userRepository.save(artsAdmin);

            User codingAdmin = new User("coding.lead@collegeclub.edu", adminPass, "Devon", "Chen", Role.CLUB_ADMIN);
            codingAdmin.setStudentId("STU-2022-114");
            codingAdmin.setDepartment("Software Engineering");
            codingAdmin.setPhone("+1-555-0103");
            codingAdmin.setAvatarUrl("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150");
            codingAdmin = userRepository.save(codingAdmin);

            User alex = new User("alex.student@collegeclub.edu", studentPass, "Alex", "Morgan", Role.STUDENT);
            alex.setStudentId("STU-2023-501");
            alex.setDepartment("Computer Science");
            alex.setPhone("+1-555-0201");
            alex.setAvatarUrl("https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150");
            alex = userRepository.save(alex);

            User maya = new User("maya.student@collegeclub.edu", studentPass, "Maya", "Patel", Role.STUDENT);
            maya.setStudentId("STU-2023-502");
            maya.setDepartment("Mechanical Engineering");
            maya.setPhone("+1-555-0202");
            maya.setAvatarUrl("https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150");
            maya = userRepository.save(maya);

            User sam = new User("sam.student@collegeclub.edu", studentPass, "Samuel", "Okonkwo", Role.STUDENT);
            sam.setStudentId("STU-2023-503");
            sam.setDepartment("Architecture & Design");
            sam.setPhone("+1-555-0203");
            sam.setAvatarUrl("https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150");
            sam = userRepository.save(sam);

            // 2. Seed Clubs
            Club roboticsClub = new Club();
            roboticsClub.setName("Robotics & Autonomous Systems Society");
            roboticsClub.setSlug("robotics-society");
            roboticsClub.setCategory("Technology & Engineering");
            roboticsClub.setShortDescription("Building autonomous rovers, drones, and AI-driven robotic prototypes.");
            roboticsClub.setDescription("The Robotics & Autonomous Systems Society brings together forward-thinking engineers, coders, and makers. We develop competitive robotics platforms, participate in national collegiate competitions, and organize hands-on hardware workshops ranging from embedded microcontrollers to deep reinforcement learning.");
            roboticsClub.setBannerUrl("https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800");
            roboticsClub.setLogoUrl("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=120");
            roboticsClub.setContactEmail("robotics@collegeclub.edu");
            roboticsClub.setMeetingSchedule("Every Tuesday & Thursday, 5:30 PM - 7:30 PM");
            roboticsClub.setLocation("Engineering Hall Lab 402");
            roboticsClub.setStatus(ClubStatus.ACTIVE);
            roboticsClub.setCreatedBy(admin);
            roboticsClub = clubRepository.save(roboticsClub);

            Club acmClub = new Club();
            acmClub.setName("ACM Collegiate Coding & Algorithms Club");
            acmClub.setSlug("acm-coding-club");
            acmClub.setCategory("Technology & Engineering");
            acmClub.setShortDescription("Fostering algorithmic problem solving, open source projects, and competitive programming.");
            acmClub.setDescription("Our ACM Chapter represents the premier competitive programming and software engineering community on campus. From algorithmic whiteboarding sessions and ICPC training to full-stack open source hackathons, we prepare students for elite technical interviews and high-impact engineering careers.");
            acmClub.setBannerUrl("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800");
            acmClub.setLogoUrl("https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=120");
            acmClub.setContactEmail("acm@collegeclub.edu");
            acmClub.setMeetingSchedule("Wednesdays at 6:00 PM");
            acmClub.setLocation("Turing Computing Center Room 108");
            acmClub.setStatus(ClubStatus.ACTIVE);
            acmClub.setCreatedBy(admin);
            acmClub = clubRepository.save(acmClub);

            Club artsClub = new Club();
            artsClub.setName("Fine Arts, Design & Photography Guild");
            artsClub.setSlug("arts-photography-guild");
            artsClub.setCategory("Arts & Culture");
            artsClub.setShortDescription("A sanctuary for visual storytellers, digital illustrators, sculptors, and photographers.");
            artsClub.setDescription("The Fine Arts & Photography Guild nurtures artistic expression across traditional and modern media. We host weekly portrait and street photography excursions, gallery exhibitions, darkroom printing masterclasses, and digital UI/UX visual design critique circles.");
            artsClub.setBannerUrl("https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=800");
            artsClub.setLogoUrl("https://images.unsplash.com/photo-1452860606245-08befc0ff44b?w=120");
            artsClub.setContactEmail("arts@collegeclub.edu");
            artsClub.setMeetingSchedule("Fridays at 4:00 PM");
            artsClub.setLocation("Fine Arts Studio 3B");
            artsClub.setStatus(ClubStatus.ACTIVE);
            artsClub.setCreatedBy(admin);
            artsClub = clubRepository.save(artsClub);

            Club debateClub = new Club();
            debateClub.setName("Parliamentary Debating & Model UN Society");
            debateClub.setSlug("debate-mun-society");
            debateClub.setCategory("Debate & Leadership");
            debateClub.setShortDescription("Sharpening rhetoric, diplomatic negotiations, critical analysis, and oratorical confidence.");
            debateClub.setDescription("Engage in rigorous parliamentary debate tournaments, simulate geopolitical diplomacy at Model UN conferences worldwide, and develop unshakeable persuasive speech. Open to all students passionate about international relations, public policy, and argumentation.");
            debateClub.setBannerUrl("https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800");
            debateClub.setLogoUrl("https://images.unsplash.com/photo-1517048676732-d65bc937f952?w=120");
            debateClub.setContactEmail("debate@collegeclub.edu");
            debateClub.setMeetingSchedule("Mondays at 6:30 PM");
            debateClub.setLocation("Founders Hall Senate Chamber");
            debateClub.setStatus(ClubStatus.ACTIVE);
            debateClub.setCreatedBy(admin);
            debateClub = clubRepository.save(debateClub);

            Club sportsClub = new Club();
            sportsClub.setName("University Athletics & Endurance Club");
            sportsClub.setSlug("athletics-endurance-club");
            sportsClub.setCategory("Sports & Recreation");
            sportsClub.setShortDescription("Promoting collegiate sportsmanship, marathon training, and holistic physical wellness.");
            sportsClub.setDescription("From weekly trail running circuits and sprint drills to intramural league tournaments, the Athletics & Endurance Club fosters physical resilience, disciplined training routines, and a supportive team environment for both casual runners and varsity competitors.");
            sportsClub.setBannerUrl("https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=800");
            sportsClub.setLogoUrl("https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=120");
            sportsClub.setContactEmail("athletics@collegeclub.edu");
            sportsClub.setMeetingSchedule("Monday, Wednesday, Friday at 6:30 AM");
            sportsClub.setLocation("Varsity Stadium Track");
            sportsClub.setStatus(ClubStatus.ACTIVE);
            sportsClub.setCreatedBy(admin);
            sportsClub = clubRepository.save(sportsClub);

            Club ecoClub = new Club();
            ecoClub.setName("EcoAction & Sustainability Coalition");
            ecoClub.setSlug("ecoaction-sustainability");
            ecoClub.setCategory("Social & Environment");
            ecoClub.setShortDescription("Championing campus carbon neutrality, urban gardening, and environmental advocacy.");
            ecoClub.setDescription("Dedicated to tangible environmental stewardship across university grounds and the wider municipal community. We spearhead native tree replanting initiatives, zero-waste dining hall campaigns, biodiversity censuses, and clean energy policy panels.");
            ecoClub.setBannerUrl("https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800");
            ecoClub.setLogoUrl("https://images.unsplash.com/photo-1530595467537-0b5996c41f2d?w=120");
            ecoClub.setContactEmail("ecoaction@collegeclub.edu");
            ecoClub.setMeetingSchedule("Saturdays at 10:00 AM");
            ecoClub.setLocation("Botanical Conservatory Pavilion");
            ecoClub.setStatus(ClubStatus.ACTIVE);
            ecoClub.setCreatedBy(admin);
            ecoClub = clubRepository.save(ecoClub);

            // 3. Assign Admins
            assignmentRepository.save(new ClubAdminAssignment(roboticsClub, roboticsAdmin, admin));
            assignmentRepository.save(new ClubAdminAssignment(acmClub, codingAdmin, admin));
            assignmentRepository.save(new ClubAdminAssignment(artsClub, artsAdmin, admin));

            // 4. Seed Memberships
            ClubMembership m1 = new ClubMembership(roboticsClub, alex, "Deeply passionate about computer vision and autonomous robotics systems.");
            m1.setStatus(MembershipStatus.APPROVED);
            m1.setReviewedBy(roboticsAdmin);
            m1.setReviewedAt(LocalDateTime.now().minusDays(10));
            membershipRepository.save(m1);

            ClubMembership m2 = new ClubMembership(roboticsClub, maya, "Experienced with CAD mechanical modeling and eager to build competition chassis.");
            m2.setStatus(MembershipStatus.APPROVED);
            m2.setReviewedBy(roboticsAdmin);
            m2.setReviewedAt(LocalDateTime.now().minusDays(8));
            membershipRepository.save(m2);

            ClubMembership m3 = new ClubMembership(roboticsClub, sam, "Interested in learning sensor integration and embedded firmware.");
            m3.setStatus(MembershipStatus.PENDING);
            membershipRepository.save(m3);

            ClubMembership m4 = new ClubMembership(acmClub, alex, "Looking to practice competitive algorithms and participate in hackathons.");
            m4.setStatus(MembershipStatus.APPROVED);
            m4.setReviewedBy(codingAdmin);
            m4.setReviewedAt(LocalDateTime.now().minusDays(5));
            membershipRepository.save(m4);

            ClubMembership m5 = new ClubMembership(artsClub, sam, "Passionate landscape photographer with 35mm film experience.");
            m5.setStatus(MembershipStatus.APPROVED);
            m5.setReviewedBy(artsAdmin);
            m5.setReviewedAt(LocalDateTime.now().minusDays(3));
            membershipRepository.save(m5);

            // 5. Seed Events
            LocalDateTime now = LocalDateTime.now();

            Event e1 = new Event();
            e1.setClub(roboticsClub);
            e1.setTitle("Autonomous Rover Showcase & Hardware Hackathon");
            e1.setSlug("autonomous-rover-showcase-2026");
            e1.setDescription("A fast-paced 24-hour hands-on hardware challenge where student teams program lidar-equipped miniature rovers to navigate dynamic obstacle courses. Starter kits and sensor boards will be provided.");
            e1.setLocation("Engineering Innovation Center Room 101");
            e1.setStartTime(now.plusDays(4).withHour(10).withMinute(0));
            e1.setEndTime(now.plusDays(5).withHour(16).withMinute(0));
            e1.setRegistrationDeadline(now.plusDays(3).withHour(23).withMinute(59));
            e1.setCapacity(45);
            e1.setRegisteredCount(18);
            e1.setBannerUrl("https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800");
            e1.setStatus(EventStatus.PUBLISHED);
            e1.setCreatedBy(roboticsAdmin);
            e1 = eventRepository.save(e1);

            Event e2 = new Event();
            e2.setClub(acmClub);
            e2.setTitle("Collegiate Algorithm Masterclass: Dynamic Programming & Graphs");
            e2.setSlug("algorithm-masterclass-dp-graphs");
            e2.setDescription("An intensive masterclass analyzing advanced algorithm patterns frequently encountered in ICPC regionals and technical interviews at top engineering firms. Led by competitive programming finalists.");
            e2.setLocation("Turing Hall Lecture Theater B");
            e2.setStartTime(now.plusDays(7).withHour(18).withMinute(0));
            e2.setEndTime(now.plusDays(7).withHour(21).withMinute(0));
            e2.setRegistrationDeadline(now.plusDays(6).withHour(23).withMinute(59));
            e2.setCapacity(80);
            e2.setRegisteredCount(42);
            e2.setBannerUrl("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800");
            e2.setStatus(EventStatus.PUBLISHED);
            e2.setCreatedBy(codingAdmin);
            e2 = eventRepository.save(e2);

            Event e3 = new Event();
            e3.setClub(artsClub);
            e3.setTitle("Golden Hour Architectural Photography Walk");
            e3.setSlug("golden-hour-photo-walk");
            e3.setDescription("Capture the historic collegiate arches, stone colonnades, and modern glass facades under optimal late afternoon illumination. Peer critiques and Lightroom raw processing tips following the walk.");
            e3.setLocation("University Quad Steps / Art Gallery");
            e3.setStartTime(now.plusDays(9).withHour(16).withMinute(30));
            e3.setEndTime(now.plusDays(9).withHour(20).withMinute(0));
            e3.setRegistrationDeadline(now.plusDays(8).withHour(23).withMinute(59));
            e3.setCapacity(25);
            e3.setRegisteredCount(15);
            e3.setBannerUrl("https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=800");
            e3.setStatus(EventStatus.PUBLISHED);
            e3.setCreatedBy(artsAdmin);
            e3 = eventRepository.save(e3);

            Event e4 = new Event();
            e4.setClub(debateClub);
            e4.setTitle("Annual Inter-Varsity Parliamentary Debate Cup");
            e4.setSlug("inter-varsity-debate-cup");
            e4.setDescription("Thirty-two collegiate teams clash on motions of technological ethics, multilateral treaties, and global climate governance. Spectators and debaters welcome.");
            e4.setLocation("Senate Hall Great Auditorium");
            e4.setStartTime(now.plusDays(12).withHour(9).withMinute(0));
            e4.setEndTime(now.plusDays(13).withHour(18).withMinute(0));
            e4.setRegistrationDeadline(now.plusDays(10).withHour(23).withMinute(59));
            e4.setCapacity(120);
            e4.setRegisteredCount(60);
            e4.setBannerUrl("https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800");
            e4.setStatus(EventStatus.PUBLISHED);
            e4.setCreatedBy(admin);
            e4 = eventRepository.save(e4);

            Event ePast = new Event();
            ePast.setClub(roboticsClub);
            ePast.setTitle("ROS2 Robotic Operating System Workshop Series");
            ePast.setSlug("ros2-workshop-series");
            ePast.setDescription("Introductory hands-on session covering node communication, publishers, subscribers, TF transforms, and Gazebo physical world simulations.");
            ePast.setLocation("Engineering Hall Lab 402");
            ePast.setStartTime(now.minusDays(5).withHour(14).withMinute(0));
            ePast.setEndTime(now.minusDays(5).withHour(17).withMinute(0));
            ePast.setRegistrationDeadline(now.minusDays(6).withHour(23).withMinute(59));
            ePast.setCapacity(40);
            ePast.setRegisteredCount(32);
            ePast.setBannerUrl("https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800");
            ePast.setStatus(EventStatus.COMPLETED);
            ePast.setCreatedBy(roboticsAdmin);
            ePast = eventRepository.save(ePast);

            // 6. Seed Registrations
            EventRegistration r1 = new EventRegistration(e1, alex);
            registrationRepository.save(r1);

            EventRegistration r2 = new EventRegistration(e1, maya);
            registrationRepository.save(r2);

            EventRegistration r3 = new EventRegistration(e2, alex);
            registrationRepository.save(r3);

            EventRegistration r4 = new EventRegistration(e3, sam);
            registrationRepository.save(r4);

            EventRegistration rPast1 = new EventRegistration(ePast, alex);
            rPast1.setAttended(true);
            rPast1.setCheckedInAt(now.minusDays(5).withHour(14).withMinute(5));
            registrationRepository.save(rPast1);

            EventRegistration rPast2 = new EventRegistration(ePast, maya);
            rPast2.setAttended(true);
            rPast2.setCheckedInAt(now.minusDays(5).withHour(14).withMinute(10));
            registrationRepository.save(rPast2);

            // 7. Seed Announcements
            Announcement a1 = new Announcement();
            a1.setTitle("Welcome to the Fall Semester College Club Platform");
            a1.setContent("Welcome all students and faculty! The college club portal is now live. Explore university organizations, request club memberships, RSVP for campus events, and participate in enriching collegiate experiences.");
            a1.setPriority(Priority.HIGH);
            a1.setPublic(true);
            a1.setAuthor(admin);
            announcementRepository.save(a1);

            Announcement a2 = new Announcement();
            a2.setClub(roboticsClub);
            a2.setTitle("Robotics Lab Equipment Inductions This Week");
            a2.setContent("All newly admitted robotics society members must attend the 30-minute safety and soldering iron induction scheduled for Thursday at 4:30 PM before operating the high-speed CNC and reflow ovens.");
            a2.setPriority(Priority.HIGH);
            a2.setPublic(false);
            a2.setAuthor(roboticsAdmin);
            announcementRepository.save(a2);

            Announcement a3 = new Announcement();
            a3.setClub(acmClub);
            a3.setTitle("Hackathon Teaming Sheet & Mentorship Signups");
            a3.setContent("Registrations for the upcoming ACM Coding Sprint are now live. If you are seeking teammates or would like to mentor freshman coders, please review the registration instructions.");
            a3.setPriority(Priority.NORMAL);
            a3.setPublic(true);
            a3.setAuthor(codingAdmin);
            announcementRepository.save(a3);

            Announcement a4 = new Announcement();
            a4.setTitle("Annual University Club Fair Announced for Next Friday");
            a4.setContent("Over 40 student clubs, societies, and academic leagues will host booths on the University Central Lawn. Refreshments and live music throughout the afternoon.");
            a4.setPriority(Priority.URGENT);
            a4.setPublic(true);
            a4.setAuthor(admin);
            announcementRepository.save(a4);

            // 8. Seed Feedbacks
            feedbackRepository.save(new EventFeedback(ePast, alex, 5, "Exceptional workshop! The hands-on Gazebo simulation helped connect the theoretical kinematic concepts with actual code."));
            feedbackRepository.save(new EventFeedback(ePast, maya, 5, "Clear instructions, well-configured lab machines, and supportive mentors."));

            // 9. Seed Activity Logs
            activityLogRepository.save(new ActivityLog(admin, "SYSTEM_STARTUP", "PLATFORM", null, "College Club Management System initialization completed successfully.", "127.0.0.1"));
            activityLogRepository.save(new ActivityLog(roboticsAdmin, "EVENT_CREATED", "EVENT", e1.getId(), "Created event: Autonomous Rover Showcase", "127.0.0.1"));
            activityLogRepository.save(new ActivityLog(codingAdmin, "EVENT_CREATED", "EVENT", e2.getId(), "Created event: Collegiate Algorithm Masterclass", "127.0.0.1"));
            activityLogRepository.save(new ActivityLog(alex, "MEMBERSHIP_REQUESTED", "CLUB", roboticsClub.getId(), "Student Alex Morgan submitted join request to Robotics Society", "127.0.0.1"));
            activityLogRepository.save(new ActivityLog(roboticsAdmin, "MEMBERSHIP_APPROVED", "CLUB_MEMBERSHIP", m1.getId(), "Approved membership for Alex Morgan", "127.0.0.1"));

            logger.info("Database seeding completed successfully.");
        } else {
            logger.info("Database already seeded. Skipping initial seed.");
        }
    }
}
