-- =============================================================================
-- Migration: V2__seed_initial_data.sql
-- Description: Comprehensive initial seed data for College Club Management System
-- Passwords below are BCrypt hashes:
-- Admin: Admin@123456 -> $2a$10$wKkG6L1qJ1KzMhFq0f4bSeP9dK8tWz9m2x5r0b7l9s0n4y6m5v8wG
-- Student: Student@123456 -> $2a$10$g5Y6qXbZ5Q9x1R8w7p2Z0u7v1N4c0d3m6l8k2j1h4f9s7a5e3r2t1
-- Note: Spring Boot DataInitializer also validates and guarantees account synchronization.
-- =============================================================================

-- Seed Users
INSERT INTO users (id, email, password_hash, first_name, last_name, student_id, department, phone, avatar_url, role, is_active, created_at)
VALUES
(1, 'admin@collegeclub.edu', '$2a$10$wKkG6L1qJ1KzMhFq0f4bSeP9dK8tWz9m2x5r0b7l9s0n4y6m5v8wG', 'Victoria', 'Sterling', NULL, 'University Administration', '+1-555-0100', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', 'SYSTEM_ADMIN', TRUE, NOW()),
(2, 'robotics.lead@collegeclub.edu', '$2a$10$wKkG6L1qJ1KzMhFq0f4bSeP9dK8tWz9m2x5r0b7l9s0n4y6m5v8wG', 'Marcus', 'Vance', 'STU-2022-041', 'Computer Science & Robotics', '+1-555-0101', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', 'CLUB_ADMIN', TRUE, NOW()),
(3, 'arts.lead@collegeclub.edu', '$2a$10$wKkG6L1qJ1KzMhFq0f4bSeP9dK8tWz9m2x5r0b7l9s0n4y6m5v8wG', 'Elena', 'Rostova', 'STU-2022-089', 'Visual Arts & Media', '+1-555-0102', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150', 'CLUB_ADMIN', TRUE, NOW()),
(4, 'coding.lead@collegeclub.edu', '$2a$10$wKkG6L1qJ1KzMhFq0f4bSeP9dK8tWz9m2x5r0b7l9s0n4y6m5v8wG', 'Devon', 'Chen', 'STU-2022-114', 'Software Engineering', '+1-555-0103', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150', 'CLUB_ADMIN', TRUE, NOW()),
(5, 'alex.student@collegeclub.edu', '$2a$10$wKkG6L1qJ1KzMhFq0f4bSeP9dK8tWz9m2x5r0b7l9s0n4y6m5v8wG', 'Alex', 'Morgan', 'STU-2023-501', 'Computer Science', '+1-555-0201', 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150', 'STUDENT', TRUE, NOW()),
(6, 'maya.student@collegeclub.edu', '$2a$10$wKkG6L1qJ1KzMhFq0f4bSeP9dK8tWz9m2x5r0b7l9s0n4y6m5v8wG', 'Maya', 'Patel', 'STU-2023-502', 'Mechanical Engineering', '+1-555-0202', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150', 'STUDENT', TRUE, NOW()),
(7, 'sam.student@collegeclub.edu', '$2a$10$wKkG6L1qJ1KzMhFq0f4bSeP9dK8tWz9m2x5r0b7l9s0n4y6m5v8wG', 'Samuel', 'Okonkwo', 'STU-2023-503', 'Architecture & Design', '+1-555-0203', 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150', 'STUDENT', TRUE, NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Seed Clubs
INSERT INTO clubs (id, name, slug, short_description, description, category, banner_url, logo_url, contact_email, meeting_schedule, location, status, created_by_id, created_at)
VALUES
(1, 'Robotics & Autonomous Systems Society', 'robotics-society', 'Building autonomous rovers, drones, and AI-driven robotic prototypes.', 'The Robotics & Autonomous Systems Society brings together forward-thinking engineers, coders, and makers. We develop competitive robotics platforms, participate in national collegiate competitions, and organize hands-on hardware workshops ranging from embedded microcontrollers to deep reinforcement learning.', 'Technology & Engineering', 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800', 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=120', 'robotics@collegeclub.edu', 'Every Tuesday & Thursday, 5:30 PM - 7:30 PM', 'Engineering Hall Lab 402', 'ACTIVE', 1, NOW()),
(2, 'ACM Collegiate Coding & Algorithms Club', 'acm-coding-club', 'Fostering algorithmic problem solving, open source projects, and competitive programming.', 'Our ACM Chapter represents the premier competitive programming and software engineering community on campus. From algorithmic whiteboarding sessions and ICPC training to full-stack open source hackathons, we prepare students for elite technical interviews and high-impact engineering careers.', 'Technology & Engineering', 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800', 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=120', 'acm@collegeclub.edu', 'Wednesdays at 6:00 PM', 'Turing Computing Center Room 108', 'ACTIVE', 1, NOW()),
(3, 'Fine Arts, Design & Photography Guild', 'arts-photography-guild', 'A sanctuary for visual storytellers, digital illustrators, sculptors, and photographers.', 'The Fine Arts & Photography Guild nurtures artistic expression across traditional and modern media. We host weekly portrait and street photography excursions, gallery exhibitions, darkroom printing masterclasses, and digital UI/UX visual design critique circles.', 'Arts & Culture', 'https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=800', 'https://images.unsplash.com/photo-1452860606245-08befc0ff44b?w=120', 'arts@collegeclub.edu', 'Fridays at 4:00 PM', 'Fine Arts Studio 3B', 'ACTIVE', 1, NOW()),
(4, 'Parliamentary Debating & Model UN Society', 'debate-mun-society', 'Sharpening rhetoric, diplomatic negotiations, critical analysis, and oratorical confidence.', 'Engage in rigorous parliamentary debate tournaments, simulate geopolitical diplomacy at Model UN conferences worldwide, and develop unshakeable persuasive speech. Open to all students passionate about international relations, public policy, and argumentation.', 'Debate & Leadership', 'https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800', 'https://images.unsplash.com/photo-1517048676732-d65bc937f952?w=120', 'debate@collegeclub.edu', 'Mondays at 6:30 PM', 'Founders Hall Senate Chamber', 'ACTIVE', 1, NOW()),
(5, 'University Athletics & Endurance Club', 'athletics-endurance-club', 'Promoting collegiate sportsmanship, marathon training, and holistic physical wellness.', 'From weekly trail running circuits and sprint drills to intramural league tournaments, the Athletics & Endurance Club fosters physical resilience, disciplined training routines, and a supportive team environment for both casual runners and varsity competitors.', 'Sports & Recreation', 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=800', 'https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=120', 'athletics@collegeclub.edu', 'Monday, Wednesday, Friday at 6:30 AM', 'Varsity Stadium Track', 'ACTIVE', 1, NOW()),
(6, 'EcoAction & Sustainability Coalition', 'ecoaction-sustainability', 'Championing campus carbon neutrality, urban gardening, and environmental advocacy.', 'Dedicated to tangible environmental stewardship across university grounds and the wider municipal community. We spearhead native tree replanting initiatives, zero-waste dining hall campaigns, biodiversity censuses, and clean energy policy panels.', 'Social & Environment', 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=800', 'https://images.unsplash.com/photo-1530595467537-0b5996c41f2d?w=120', 'ecoaction@collegeclub.edu', 'Saturdays at 10:00 AM', 'Botanical Conservatory Pavilion', 'ACTIVE', 1, NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Assign Club Administrators
INSERT INTO club_admin_assignments (club_id, user_id, assigned_by_id, assigned_at)
VALUES
(1, 2, 1, NOW()),
(2, 4, 1, NOW()),
(3, 3, 1, NOW())
ON DUPLICATE KEY UPDATE assigned_at = NOW();

-- Club Memberships
INSERT INTO club_memberships (club_id, user_id, status, join_reason, reviewed_by_id, reviewed_at, created_at)
VALUES
(1, 5, 'APPROVED', 'Deeply passionate about computer vision and autonomous robotics systems.', 2, NOW(), NOW()),
(1, 6, 'APPROVED', 'Experienced with CAD mechanical modeling and eager to build competition chassis.', 2, NOW(), NOW()),
(1, 7, 'PENDING', 'Interested in learning sensor integration and embedded firmware.', NULL, NULL, NOW()),
(2, 5, 'APPROVED', 'Looking to practice competitive algorithms and participate in hackathons.', 4, NOW(), NOW()),
(2, 6, 'PENDING', 'Eager to improve Java and data structure skills.', NULL, NULL, NOW()),
(3, 7, 'APPROVED', 'Passionate landscape photographer with 35mm film experience.', 3, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Seed Events
INSERT INTO events (id, club_id, title, slug, description, location, start_time, end_time, registration_deadline, capacity, registered_count, banner_url, status, created_by_id, created_at)
VALUES
(1, 1, 'Autonomous Rover Showcase & Hardware Hackathon', 'autonomous-rover-showcase-2026', 'A fast-paced 24-hour hands-on hardware challenge where student teams program lidar-equipped miniature rovers to navigate dynamic obstacle courses. Starter kits and sensor boards will be provided.', 'Engineering Innovation Center Room 101', DATE_ADD(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 3 DAY), 45, 18, 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800', 'PUBLISHED', 2, NOW()),
(2, 2, 'Collegiate Algorithm Masterclass: Dynamic Programming & Graphs', 'algorithm-masterclass-dp-graphs', 'An intensive masterclass analyzing advanced algorithm patterns frequently encountered in ICPC regionals and technical interviews at top engineering firms. Led by competitive programming finalists.', 'Turing Hall Lecture Theater B', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY) + INTERVAL 3 HOUR, DATE_ADD(NOW(), INTERVAL 6 DAY), 80, 42, 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800', 'PUBLISHED', 4, NOW()),
(3, 3, 'Golden Hour Architectural Photography Walk', 'golden-hour-photo-walk', 'Capture the historic collegiate arches, stone colonnades, and modern glass facades under optimal late afternoon illumination. Peer critiques and Lightroom raw processing tips following the walk.', 'University Quad Steps / Art Gallery', DATE_ADD(NOW(), INTERVAL 9 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY) + INTERVAL 4 HOUR, DATE_ADD(NOW(), INTERVAL 8 DAY), 25, 15, 'https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=800', 'PUBLISHED', 3, NOW()),
(4, 4, 'Annual Inter-Varsity Parliamentary Debate Cup', 'inter-varsity-debate-cup', 'Thirty-two collegiate teams clash on motions of technological ethics, multilateral treaties, and global climate governance. Spectators and debaters welcome.', 'Senate Hall Great Auditorium', DATE_ADD(NOW(), INTERVAL 12 DAY), DATE_ADD(NOW(), INTERVAL 13 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), 120, 60, 'https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800', 'PUBLISHED', 1, NOW()),
(5, 1, 'ROS2 Robotic Operating System Workshop Series', 'ros2-workshop-series', 'Introductory hands-on session covering node communication, publishers, subscribers, TF transforms, and Gazebo physical world simulations.', 'Engineering Hall Lab 402', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY) + INTERVAL 3 HOUR, DATE_SUB(NOW(), INTERVAL 6 DAY), 40, 32, 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800', 'COMPLETED', 2, NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Event Registrations
INSERT INTO event_registrations (event_id, user_id, status, attended, checked_in_at, created_at)
VALUES
(1, 5, 'REGISTERED', FALSE, NULL, NOW()),
(1, 6, 'REGISTERED', FALSE, NULL, NOW()),
(2, 5, 'REGISTERED', FALSE, NULL, NOW()),
(3, 7, 'REGISTERED', FALSE, NULL, NOW()),
(5, 5, 'REGISTERED', TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(5, 6, 'REGISTERED', TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Announcements
INSERT INTO announcements (id, club_id, author_id, title, content, priority, is_public, created_at)
VALUES
(1, NULL, 1, 'Welcome to the Fall Semester College Club Platform', 'Welcome all students and faculty! The college club portal is now live. Explore university organizations, request club memberships, RSVP for campus events, and participate in enriching collegiate experiences.', 'HIGH', TRUE, NOW()),
(2, 1, 2, 'Robotics Lab Equipment Inductions This Week', 'All newly admitted robotics society members must attend the 30-minute safety and soldering iron induction scheduled for Thursday at 4:30 PM before operating the high-speed CNC and reflow ovens.', 'HIGH', FALSE, NOW()),
(3, 2, 4, 'Hackathon Teaming Sheet & Mentorship Signups', 'Registrations for the upcoming ACM Coding Sprint are now live. If you are seeking teammates or would like to mentor freshman coders, please review the registration instructions.', 'NORMAL', TRUE, NOW()),
(4, NULL, 1, 'Annual University Club Fair Announced for Next Friday', 'Over 40 student clubs, societies, and academic leagues will host booths on the University Central Lawn. Refreshments and live music throughout the afternoon.', 'URGENT', TRUE, NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Event Feedbacks
INSERT INTO event_feedbacks (event_id, user_id, rating, comments, created_at)
VALUES
(5, 5, 5, 'Exceptional workshop! The hands-on Gazebo simulation helped connect the theoretical kinematic concepts with actual code.', NOW()),
(5, 6, 5, 'Clear instructions, well-configured lab machines, and supportive mentors.', NOW())
ON DUPLICATE KEY UPDATE created_at = NOW();

-- Activity Logs
INSERT INTO activity_logs (user_id, action, entity_type, entity_id, details, ip_address, created_at)
VALUES
(1, 'SYSTEM_STARTUP', 'PLATFORM', NULL, 'College Club Management System initialization completed successfully.', '127.0.0.1', NOW()),
(2, 'EVENT_CREATED', 'EVENT', 1, 'Created event: Autonomous Rover Showcase & Hardware Hackathon', '127.0.0.1', NOW()),
(4, 'EVENT_CREATED', 'EVENT', 2, 'Created event: Collegiate Algorithm Masterclass', '127.0.0.1', NOW()),
(5, 'MEMBERSHIP_REQUESTED', 'CLUB', 1, 'Student Alex Morgan submitted join request to Robotics Society', '127.0.0.1', NOW()),
(2, 'MEMBERSHIP_APPROVED', 'CLUB_MEMBERSHIP', 1, 'Approved membership for Alex Morgan', '127.0.0.1', NOW())
ON DUPLICATE KEY UPDATE created_at = NOW();
