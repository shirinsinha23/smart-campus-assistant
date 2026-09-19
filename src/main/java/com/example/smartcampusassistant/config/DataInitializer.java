package com.example.smartcampusassistant.config;

import com.example.smartcampusassistant.cafeteria.model.MealItem;
import com.example.smartcampusassistant.cafeteria.model.MealType;
import com.example.smartcampusassistant.cafeteria.repository.MealItemRepository;
import com.example.smartcampusassistant.complaint.Complaint;
import com.example.smartcampusassistant.complaint.ComplaintRepository;
import com.example.smartcampusassistant.event.model.Club;
import com.example.smartcampusassistant.event.repository.ClubRepository;
import com.example.smartcampusassistant.leave.entity.Leave;
import com.example.smartcampusassistant.leave.enums.LeaveStatus;
import com.example.smartcampusassistant.leave.enums.LeaveType;
import com.example.smartcampusassistant.leave.repository.LeaveRepository;
import com.example.smartcampusassistant.transport.entity.*;
import com.example.smartcampusassistant.transport.enums.BusStatus;
import com.example.smartcampusassistant.transport.repository.*;
import com.example.smartcampusassistant.user.Role;
import com.example.smartcampusassistant.user.User;
import com.example.smartcampusassistant.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final ClubRepository clubRepository;
    private final PasswordEncoder passwordEncoder;
    private final MealItemRepository mealItemRepository;
    private final LeaveRepository leaveRepository;
    private final BusRepository busRepository;
    private final BusRouteRepository busRouteRepository;
    private final BusScheduleRepository busScheduleRepository;
    private final SeatBookingRepository seatBookingRepository;
    private final BusLocationRepository busLocationRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("=========================================");
        System.out.println("🚀 Starting Data Initializer...");
        System.out.println("=========================================");

        initializeUsers();
        initializeClubs();
        initializeComplaints();
        initializeCafeteriaItems();
        initializeLeaves();
        initializeTransportData();

        System.out.println("=========================================");
        System.out.println("✅ Data Initialization Complete!");
        System.out.println("=========================================");
    }

    private void initializeUsers() {
        System.out.println("👥 Initializing Users...");

        // Admin
        if (!userRepository.existsByLoginId("300001")) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .phoneNumber("9876543210")
                    .loginId("300001")
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin created: 300001 - Admin");
        }

        // Faculty
        if (!userRepository.existsByLoginId("200001")) {
            User faculty1 = User.builder()
                    .name("Shirin Sinha")
                    .email("shirin@sharda.ac.in")
                    .password(passwordEncoder.encode("faculty123"))
                    .role(Role.FACULTY)
                    .phoneNumber("9876543211")
                    .loginId("200001")
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(faculty1);
            System.out.println("✅ Faculty created: 200001 - Shirin Sinha");
        }

        if (!userRepository.existsByLoginId("200002")) {
            User faculty2 = User.builder()
                    .name("Anurag Gupta")
                    .email("anurag@sharda.ac.in")
                    .password(passwordEncoder.encode("faculty123"))
                    .role(Role.FACULTY)
                    .phoneNumber("9876543212")
                    .loginId("200002")
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(faculty2);
            System.out.println("✅ Faculty created: 200002 - Anurag Gupta");
        }

        // Students
        String[][] students = {
                {"1001", "Shirin Sinha", "shirinsinha23@gmail.com", "9876543213", "CSE Core", "Block 1"},
                {"1002", "Anurag Gupta", "anurag21@gmail.com", "9876543214", "CSE AI ML", "Block 1"},
                {"1003", "Rajeeb Singh", "rajeeb21@gmail.com", "9876543215", "CSE Core", "Block 1"},
                {"1004", "Shanvi Sinha", "shanvi14@gmail.com", "9876543216", "CSE Core", "Block 1"},
                {"1005", "Dhriti Mukherjee", "dhriti21@gmail.com", "9876543217", "CSE Cybersecurity", "Block 1"},
                {"1006", "Rajeev Singh", "rajeev@sharda.ac.in", "9876543218", "CSE Core", "Block 2"},
                {"1007", "Priya Sharma", "priya@sharda.ac.in", "9876543219", "CSE AI ML", "Block 2"},
                {"1008", "Rahul Verma", "rahul@sharda.ac.in", "9876543220", "CSE Cybersecurity", "Block 2"}
        };

        for (String[] student : students) {
            String loginId = student[0];
            if (!userRepository.existsByLoginId(loginId)) {
                User user = User.builder()
                        .name(student[1])
                        .email(student[2])
                        .password(passwordEncoder.encode("student123"))
                        .role(Role.STUDENT)
                        .phoneNumber(student[3])
                        .loginId(loginId)
                        .stream(student[4])
                        .block(student[5])
                        .createdAt(LocalDateTime.now())
                        .build();
                userRepository.save(user);
                System.out.println("✅ Student created: " + loginId + " - " + student[1]);
            }
        }

        System.out.println("✅ Total users in DB: " + userRepository.count());
    }

    private void initializeClubs() {
        System.out.println("🏛️ Initializing Clubs...");

        if (clubRepository.count() > 0) {
            System.out.println("📋 Clubs already exist. Skipping...");
            return;
        }

        User faculty1 = userRepository.findByLoginId("200001").orElse(null);
        User faculty2 = userRepository.findByLoginId("200002").orElse(null);

        List<Club> clubs = new ArrayList<>();

        // 1. Tech Innovators - Technical Club
        clubs.add(Club.builder()
                .name("Tech Innovators")
                .description("Technical club for coding, hackathons, and innovation.")
                .category("Technical")
                .logoUrl("https://example.com/tech-club-logo.png")
                .isActive(true)
                .coordinator(faculty1)
                .build());

        // 2. Cultural Club
        clubs.add(Club.builder()
                .name("Cultural Club")
                .description("Celebrating diversity and culture through festivals and cultural events.")
                .category("Cultural")
                .logoUrl("https://example.com/cultural-club-logo.png")
                .isActive(true)
                .coordinator(faculty2)
                .build());

        // 3. Dance Crew
        clubs.add(Club.builder()
                .name("Dance Crew")
                .description("Dance and performing arts club.")
                .category("Dance")
                .logoUrl("https://example.com/dance-club-logo.png")
                .isActive(true)
                .coordinator(null)
                .build());

        // 4. Music Society
        clubs.add(Club.builder()
                .name("Music Society")
                .description("Music and instrumental performances.")
                .category("Music")
                .logoUrl("https://example.com/music-club-logo.png")
                .isActive(true)
                .coordinator(null)
                .build());

        // 5. Sports Club
        clubs.add(Club.builder()
                .name("Sports Club")
                .description("Sports and fitness activities.")
                .category("Sports")
                .logoUrl("https://example.com/sports-club-logo.png")
                .isActive(true)
                .coordinator(null)
                .build());

        // 6. Art & Craft
        clubs.add(Club.builder()
                .name("Art & Craft")
                .description("Creative arts and crafts including painting and sculpture.")
                .category("Art")
                .logoUrl("https://example.com/art-club-logo.png")
                .isActive(true)
                .coordinator(null)
                .build());

        // 7. Literary Club
        clubs.add(Club.builder()
                .name("Literary Club")
                .description("Debate, poetry, creative writing, and literature discussions.")
                .category("Literary")
                .logoUrl("https://example.com/literary-club-logo.png")
                .isActive(true)
                .coordinator(null)
                .build());

        // 8. Photography Club
        clubs.add(Club.builder()
                .name("Photography Club")
                .description("Photography enthusiasts capturing campus moments.")
                .category("Art")
                .logoUrl("https://example.com/photo-club-logo.png")
                .isActive(true)
                .coordinator(null)
                .build());

        // 9. Entrepreneurship Cell
        clubs.add(Club.builder()
                .name("Entrepreneurship Cell")
                .description("Fostering innovation and entrepreneurship.")
                .category("Technical")
                .logoUrl("https://example.com/entrepreneurship-logo.png")
                .isActive(true)
                .coordinator(faculty1)
                .build());

        // 10. Environment Club
        clubs.add(Club.builder()
                .name("Environment Club")
                .description("Promoting environmental awareness and sustainability.")
                .category("Cultural")
                .logoUrl("https://example.com/environment-club-logo.png")
                .isActive(true)
                .coordinator(null)
                .build());

        clubRepository.saveAll(clubs);
        System.out.println("✅ Total " + clubs.size() + " clubs created successfully!");
    }

    private void initializeComplaints() {
        System.out.println("📋 Initializing Complaints...");

        if (complaintRepository.count() > 0) {
            System.out.println("📋 Complaints already exist. Skipping...");
            return;
        }

        User shirinStudent = userRepository.findByLoginId("1001").orElse(null);
        User anuragStudent = userRepository.findByLoginId("1002").orElse(null);
        User rajeeb = userRepository.findByLoginId("1003").orElse(null);
        User shanvi = userRepository.findByLoginId("1004").orElse(null);
        User dhriti = userRepository.findByLoginId("1005").orElse(null);

        List<Complaint> complaints = new ArrayList<>();

        if (shirinStudent != null) {
            Complaint c1 = new Complaint();
            c1.setTitle("Hostel Room AC Not Working");
            c1.setDescription("The AC in room 304 has been malfunctioning for 3 days.");
            c1.setCategory("Hostel");
            c1.setStatus("PENDING");
            c1.setPriority("HIGH");
            c1.setCreatedBy(shirinStudent.getName());
            c1.setCreatedById(String.valueOf(shirinStudent.getId()));
            c1.setCreatedAt(LocalDate.now().minusDays(3).toString());
            c1.setComments(new ArrayList<>());
            complaints.add(c1);
        }

        if (rajeeb != null) {
            Complaint c2 = new Complaint();
            c2.setTitle("Library Books Not Arranged Properly");
            c2.setDescription("The books in the reference section are not arranged properly.");
            c2.setCategory("Library");
            c2.setStatus("IN_PROGRESS");
            c2.setPriority("MEDIUM");
            c2.setCreatedBy(rajeeb.getName());
            c2.setCreatedById(String.valueOf(rajeeb.getId()));
            c2.setCreatedAt(LocalDate.now().minusDays(4).toString());
            c2.setComments(new ArrayList<>());
            complaints.add(c2);
        }

        if (anuragStudent != null) {
            Complaint c3 = new Complaint();
            c3.setTitle("WiFi Not Working in Block C");
            c3.setDescription("The WiFi connection in Block C has been very slow.");
            c3.setCategory("Internet");
            c3.setStatus("RESOLVED");
            c3.setPriority("HIGH");
            c3.setCreatedBy(anuragStudent.getName());
            c3.setCreatedById(String.valueOf(anuragStudent.getId()));
            c3.setCreatedAt(LocalDate.now().minusDays(5).toString());
            c3.setComments(new ArrayList<>());
            complaints.add(c3);
        }

        if (shanvi != null) {
            Complaint c4 = new Complaint();
            c4.setTitle("Food Quality in Cafeteria");
            c4.setDescription("The food quality in the cafeteria has gone down significantly.");
            c4.setCategory("Cafeteria");
            c4.setStatus("PENDING");
            c4.setPriority("MEDIUM");
            c4.setCreatedBy(shanvi.getName());
            c4.setCreatedById(String.valueOf(shanvi.getId()));
            c4.setCreatedAt(LocalDate.now().minusDays(6).toString());
            c4.setComments(new ArrayList<>());
            complaints.add(c4);
        }

        if (shirinStudent != null) {
            Complaint c5 = new Complaint();
            c5.setTitle("Bus Delayed Every Morning");
            c5.setDescription("The college bus is consistently late by 20-25 minutes.");
            c5.setCategory("Transport");
            c5.setStatus("IN_PROGRESS");
            c5.setPriority("HIGH");
            c5.setCreatedBy(shirinStudent.getName());
            c5.setCreatedById(String.valueOf(shirinStudent.getId()));
            c5.setCreatedAt(LocalDate.now().minusDays(7).toString());
            c5.setComments(new ArrayList<>());
            complaints.add(c5);
        }

        if (dhriti != null) {
            Complaint c6 = new Complaint();
            c6.setTitle("Classroom Projector Not Working");
            c6.setDescription("The projector in Room 201 is not working.");
            c6.setCategory("Classroom");
            c6.setStatus("PENDING");
            c6.setPriority("MEDIUM");
            c6.setCreatedBy(dhriti.getName());
            c6.setCreatedById(String.valueOf(dhriti.getId()));
            c6.setCreatedAt(LocalDate.now().minusDays(2).toString());
            c6.setComments(new ArrayList<>());
            complaints.add(c6);
        }

        complaintRepository.saveAll(complaints);
        System.out.println("✅ Total " + complaints.size() + " complaints created successfully!");
    }

    private void initializeCafeteriaItems() {
        System.out.println("🍽️ Initializing Cafeteria Items...");

        if (mealItemRepository.count() > 0) {
            System.out.println("📋 Cafeteria items already exist. Skipping...");
            return;
        }

        List<MealItem> items = new ArrayList<>();

        // ===== SOUTH INDIAN DISHES =====
        items.add(MealItem.builder()
                .name("Masala Dosa")
                .description("Crispy dosa with spiced potato filling, served with sambhar and chutney")
                .price(80.0)
                .mealType(MealType.BREAKFAST)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(10)
                .build());

        items.add(MealItem.builder()
                .name("Idli Sambhar")
                .description("Steamed rice cakes served with lentil soup and coconut chutney")
                .price(60.0)
                .mealType(MealType.BREAKFAST)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(8)
                .build());

        items.add(MealItem.builder()
                .name("Vada")
                .description("Crispy fried lentil donuts, served with sambhar")
                .price(40.0)
                .mealType(MealType.BREAKFAST)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(5)
                .build());

        items.add(MealItem.builder()
                .name("Uttapam")
                .description("Thick rice pancake topped with onions, tomatoes, and green chilies")
                .price(70.0)
                .mealType(MealType.BREAKFAST)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(12)
                .build());

        // ===== NORTH INDIAN DISHES =====
        items.add(MealItem.builder()
                .name("Butter Chicken")
                .description("Creamy tomato-based chicken curry, served with naan")
                .price(180.0)
                .mealType(MealType.LUNCH)
                .category("NON-VEG")
                .isAvailable(true)
                .preparationTime(20)
                .build());

        items.add(MealItem.builder()
                .name("Paneer Butter Masala")
                .description("Paneer cubes in rich creamy tomato gravy")
                .price(150.0)
                .mealType(MealType.LUNCH)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(18)
                .build());

        items.add(MealItem.builder()
                .name("Dal Makhani")
                .description("Slow-cooked black lentils with cream and butter")
                .price(120.0)
                .mealType(MealType.LUNCH)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(15)
                .build());

        items.add(MealItem.builder()
                .name("Naan")
                .description("Tandoor-baked flatbread")
                .price(40.0)
                .mealType(MealType.LUNCH)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(5)
                .build());

        items.add(MealItem.builder()
                .name("Chicken Biryani")
                .description("Fragrant basmati rice layered with spiced chicken")
                .price(200.0)
                .mealType(MealType.LUNCH)
                .category("NON-VEG")
                .isAvailable(true)
                .preparationTime(25)
                .build());

        // ===== SNACKS =====
        items.add(MealItem.builder()
                .name("Pani Puri")
                .description("Crispy puris filled with spicy mint water and chickpeas")
                .price(50.0)
                .mealType(MealType.SNACKS)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(5)
                .build());

        items.add(MealItem.builder()
                .name("Pav Bhaji")
                .description("Spicy mashed vegetable curry served with buttered bread")
                .price(70.0)
                .mealType(MealType.SNACKS)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(10)
                .build());

        items.add(MealItem.builder()
                .name("Samosa")
                .description("Crispy pastry filled with spiced potato and peas")
                .price(30.0)
                .mealType(MealType.SNACKS)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(5)
                .build());

        items.add(MealItem.builder()
                .name("Vada Pav")
                .description("Spicy potato patty in a bun with chutney")
                .price(40.0)
                .mealType(MealType.SNACKS)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(5)
                .build());

        // ===== CHINESE DISHES =====
        items.add(MealItem.builder()
                .name("Chilli Chicken")
                .description("Spicy chicken stir-fried with bell peppers and onions")
                .price(160.0)
                .mealType(MealType.DINNER)
                .category("NON-VEG")
                .isAvailable(true)
                .preparationTime(15)
                .build());

        items.add(MealItem.builder()
                .name("Fried Rice")
                .description("Wok-tossed rice with vegetables and egg")
                .price(110.0)
                .mealType(MealType.DINNER)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(12)
                .build());

        items.add(MealItem.builder()
                .name("Hakka Noodles")
                .description("Stir-fried noodles with vegetables")
                .price(100.0)
                .mealType(MealType.DINNER)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(10)
                .build());

        items.add(MealItem.builder()
                .name("Manchurian")
                .description("Fried vegetable balls in tangy sauce")
                .price(130.0)
                .mealType(MealType.DINNER)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(15)
                .build());

        // ===== BEVERAGES =====
        items.add(MealItem.builder()
                .name("Mango Lassi")
                .description("Sweet yogurt blended with mango pulp")
                .price(80.0)
                .mealType(MealType.BEVERAGES)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(3)
                .build());

        items.add(MealItem.builder()
                .name("Filter Coffee")
                .description("Authentic South Indian filter coffee")
                .price(30.0)
                .mealType(MealType.BEVERAGES)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(3)
                .build());

        items.add(MealItem.builder()
                .name("Masala Chai")
                .description("Spiced Indian tea with milk")
                .price(20.0)
                .mealType(MealType.BEVERAGES)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(2)
                .build());

        items.add(MealItem.builder()
                .name("Cold Drink")
                .description("Refreshing cold beverage")
                .price(40.0)
                .mealType(MealType.BEVERAGES)
                .category("VEG")
                .isAvailable(true)
                .preparationTime(2)
                .build());

        mealItemRepository.saveAll(items);
        System.out.println("✅ Total " + items.size() + " cafeteria items created successfully!");
    }

    // ==================== LEAVE INITIALIZATION ====================
    private void initializeLeaves() {
        System.out.println("📋 Initializing Leaves...");

        if (leaveRepository.count() > 0) {
            System.out.println("📋 Leaves already exist. Skipping...");
            return;
        }

        User shirinStudent = userRepository.findByLoginId("1001").orElse(null);
        User anuragStudent = userRepository.findByLoginId("1002").orElse(null);
        User rajeeb = userRepository.findByLoginId("1003").orElse(null);
        User shanvi = userRepository.findByLoginId("1004").orElse(null);
        User dhriti = userRepository.findByLoginId("1005").orElse(null);
        User faculty1 = userRepository.findByLoginId("200001").orElse(null);

        List<Leave> leaves = new ArrayList<>();

        // 1. Sick Leave - Approved
        if (shirinStudent != null && faculty1 != null) {
            Leave l1 = Leave.builder()
                    .title("Medical Leave - Fever")
                    .description("I have high fever and severe body ache. Need rest for 3 days.")
                    .leaveType(LeaveType.SICK)
                    .startDate(LocalDate.now().minusDays(10))
                    .endDate(LocalDate.now().minusDays(7))
                    .user(shirinStudent)
                    .status(LeaveStatus.APPROVED)
                    .approvedBy(faculty1)
                    .approvedAt(LocalDateTime.now().minusDays(7))
                    .build();
            leaves.add(l1);
            System.out.println("✅ Created leave: Sick Leave - Fever (Approved)");
        }

        // 2. Casual Leave - Pending
        if (anuragStudent != null) {
            Leave l2 = Leave.builder()
                    .title("Casual Leave - Personal Work")
                    .description("Need to attend a family function. Requesting 2 days leave.")
                    .leaveType(LeaveType.CASUAL)
                    .startDate(LocalDate.now().plusDays(5))
                    .endDate(LocalDate.now().plusDays(6))
                    .user(anuragStudent)
                    .status(LeaveStatus.PENDING)
                    .build();
            leaves.add(l2);
            System.out.println("✅ Created leave: Casual Leave - Personal Work (Pending)");
        }

        // 3. Emergency Leave - Rejected
        if (rajeeb != null) {
            Leave l3 = Leave.builder()
                    .title("Emergency Leave")
                    .description("Family emergency, need to go home immediately.")
                    .leaveType(LeaveType.EMERGENCY)
                    .startDate(LocalDate.now().minusDays(3))
                    .endDate(LocalDate.now().minusDays(2))
                    .user(rajeeb)
                    .status(LeaveStatus.REJECTED)
                    .reasonForRejection("No supporting documents provided. Please apply again with proper documentation.")
                    .build();
            leaves.add(l3);
            System.out.println("✅ Created leave: Emergency Leave (Rejected)");
        }

        // 4. Vacation Leave - Pending
        if (shanvi != null) {
            Leave l4 = Leave.builder()
                    .title("Vacation Leave")
                    .description("Planning a vacation with family. Requesting 5 days leave.")
                    .leaveType(LeaveType.VACATION)
                    .startDate(LocalDate.now().plusDays(15))
                    .endDate(LocalDate.now().plusDays(19))
                    .user(shanvi)
                    .status(LeaveStatus.PENDING)
                    .build();
            leaves.add(l4);
            System.out.println("✅ Created leave: Vacation Leave (Pending)");
        }

        // 5. Sick Leave - Pending
        if (dhriti != null) {
            Leave l5 = Leave.builder()
                    .title("Sick Leave - Stomach Infection")
                    .description("Severe stomach infection, doctor advised 4 days rest.")
                    .leaveType(LeaveType.SICK)
                    .startDate(LocalDate.now().plusDays(2))
                    .endDate(LocalDate.now().plusDays(5))
                    .user(dhriti)
                    .status(LeaveStatus.PENDING)
                    .build();
            leaves.add(l5);
            System.out.println("✅ Created leave: Sick Leave - Stomach Infection (Pending)");
        }

        // 6. Study Leave - Approved
        if (shirinStudent != null && faculty1 != null) {
            Leave l6 = Leave.builder()
                    .title("Study Leave - Exam Preparation")
                    .description("Need time to prepare for final exams. Requesting 3 days leave.")
                    .leaveType(LeaveType.STUDY)
                    .startDate(LocalDate.now().minusDays(20))
                    .endDate(LocalDate.now().minusDays(17))
                    .user(shirinStudent)
                    .status(LeaveStatus.APPROVED)
                    .approvedBy(faculty1)
                    .approvedAt(LocalDateTime.now().minusDays(17))
                    .build();
            leaves.add(l6);
            System.out.println("✅ Created leave: Study Leave - Exam Preparation (Approved)");
        }

        // 7. Other Leave - Cancelled
        if (anuragStudent != null) {
            Leave l7 = Leave.builder()
                    .title("Personal Leave")
                    .description("Need to attend a wedding function.")
                    .leaveType(LeaveType.OTHER)
                    .startDate(LocalDate.now().minusDays(8))
                    .endDate(LocalDate.now().minusDays(7))
                    .user(anuragStudent)
                    .status(LeaveStatus.CANCELLED)
                    .build();
            leaves.add(l7);
            System.out.println("✅ Created leave: Personal Leave (Cancelled)");
        }

        // 8. Casual Leave - Approved
        if (rajeeb != null && faculty1 != null) {
            Leave l8 = Leave.builder()
                    .title("Casual Leave - Doctor Appointment")
                    .description("Need to visit doctor for routine checkup.")
                    .leaveType(LeaveType.CASUAL)
                    .startDate(LocalDate.now().minusDays(14))
                    .endDate(LocalDate.now().minusDays(13))
                    .user(rajeeb)
                    .status(LeaveStatus.APPROVED)
                    .approvedBy(faculty1)
                    .approvedAt(LocalDateTime.now().minusDays(13))
                    .build();
            leaves.add(l8);
            System.out.println("✅ Created leave: Casual Leave - Doctor Appointment (Approved)");
        }

        leaveRepository.saveAll(leaves);
        System.out.println("✅ Total " + leaves.size() + " leaves created successfully!");

        System.out.println("📋 Leave Summary:");
        System.out.println("   ✅ Pending: " + leaves.stream().filter(l -> l.getStatus() == LeaveStatus.PENDING).count());
        System.out.println("   ✅ Approved: " + leaves.stream().filter(l -> l.getStatus() == LeaveStatus.APPROVED).count());
        System.out.println("   ✅ Rejected: " + leaves.stream().filter(l -> l.getStatus() == LeaveStatus.REJECTED).count());
        System.out.println("   ✅ Cancelled: " + leaves.stream().filter(l -> l.getStatus() == LeaveStatus.CANCELLED).count());
    }

    // ===== INITIALIZE TRANSPORT DATA =====
    private void initializeTransportData() {
        System.out.println("🚌 Initializing Transport Data...");

        if (busRepository.count() > 0) {
            System.out.println("📋 Transport data already exists. Skipping...");
            return;
        }

        // ===== CREATE BUSES =====
        List<Bus> buses = new ArrayList<>();

        Bus bus1 = Bus.builder()
                .busNumber("UP-32-DX-0596")
                .driverName("Rajesh Kumar")
                .driverPhone("9876543210")
                .capacity(40)
                .registrationNumber("UP-32-DX-0596")
                .status(BusStatus.ACTIVE)
                .build();
        buses.add(bus1);

        Bus bus2 = Bus.builder()
                .busNumber("UP-32-DX-1234")
                .driverName("Suresh Patel")
                .driverPhone("9876543211")
                .capacity(30)
                .registrationNumber("UP-32-DX-1234")
                .status(BusStatus.ACTIVE)
                .build();
        buses.add(bus2);

        Bus bus3 = Bus.builder()
                .busNumber("UP-26-BP-7890")
                .driverName("Amit Sharma")
                .driverPhone("9876543212")
                .capacity(20)
                .registrationNumber("UP-26-BP-7890")
                .status(BusStatus.ACTIVE)
                .build();
        buses.add(bus3);

        busRepository.saveAll(buses);
        System.out.println("✅ " + buses.size() + " buses created");

        // ===== CREATE ROUTES (College to Sectors - City routes) =====
        List<BusRoute> routes = new ArrayList<>();

        // Route 1 - College to Sector 143
        BusRoute route1 = BusRoute.builder()
                .routeName("College to Sector 143")
                .description("Bus service from College to Sector 143")
                .startLocation("College Main Gate")
                .endLocation("Sector 143")
                .routeType("CITY")
                .totalDistanceKm(12.5)
                .estimatedDurationMinutes(30)
                .isActive(true)
                .build();
        route1.setStopsList(Arrays.asList("College Main Gate", "Sector 140", "Sector 141", "Sector 142", "Sector 143"));
        routes.add(route1);

        // Route 2 - College to Sector 62
        BusRoute route2 = BusRoute.builder()
                .routeName("College to Sector 62")
                .description("Bus service from College to Sector 62")
                .startLocation("College Main Gate")
                .endLocation("Sector 62")
                .routeType("CITY")
                .totalDistanceKm(8.0)
                .estimatedDurationMinutes(20)
                .isActive(true)
                .build();
        route2.setStopsList(Arrays.asList("College Main Gate", "Sector 59", "Sector 60", "Sector 61", "Sector 62"));
        routes.add(route2);

        // Route 3 - College to Sector 15
        BusRoute route3 = BusRoute.builder()
                .routeName("College to Sector 15")
                .description("Bus service from College to Sector 15")
                .startLocation("College Main Gate")
                .endLocation("Sector 15")
                .routeType("CITY")
                .totalDistanceKm(6.5)
                .estimatedDurationMinutes(18)
                .isActive(true)
                .build();
        route3.setStopsList(Arrays.asList("College Main Gate", "Sector 12", "Sector 13", "Sector 14", "Sector 15"));
        routes.add(route3);

        // Route 4 - College to Sector 20
        BusRoute route4 = BusRoute.builder()
                .routeName("College to Sector 20")
                .description("Bus service from College to Sector 20")
                .startLocation("College Main Gate")
                .endLocation("Sector 20")
                .routeType("CITY")
                .totalDistanceKm(7.0)
                .estimatedDurationMinutes(20)
                .isActive(true)
                .build();
        route4.setStopsList(Arrays.asList("College Main Gate", "Sector 17", "Sector 18", "Sector 19", "Sector 20"));
        routes.add(route4);

        // Route 5 - College to Indirapuram
        BusRoute route5 = BusRoute.builder()
                .routeName("College to Indirapuram")
                .description("Bus service from College to Indirapuram")
                .startLocation("College Back Gate")
                .endLocation("Indirapuram")
                .routeType("CITY")
                .totalDistanceKm(15.0)
                .estimatedDurationMinutes(40)
                .isActive(true)
                .build();
        route5.setStopsList(Arrays.asList("College Back Gate", "Vaishali", "Kaushambi", "Anand Vihar", "Indirapuram"));
        routes.add(route5);

        // Route 6 - College to Noida Sector 62
        BusRoute route6 = BusRoute.builder()
                .routeName("College to Noida Sector 62")
                .description("Bus service from College to Noida Sector 62")
                .startLocation("College Main Gate")
                .endLocation("Noida Sector 62")
                .routeType("CITY")
                .totalDistanceKm(18.0)
                .estimatedDurationMinutes(45)
                .isActive(true)
                .build();
        route6.setStopsList(Arrays.asList("College Main Gate", "Noida Sector 58", "Noida Sector 59", "Noida Sector 60", "Noida Sector 61", "Noida Sector 62"));
        routes.add(route6);

        // Route 7 - Sector 143 to College (Return)
        BusRoute route7 = BusRoute.builder()
                .routeName("Sector 143 to College")
                .description("Bus service from Sector 143 to College")
                .startLocation("Sector 143")
                .endLocation("College Main Gate")
                .routeType("CITY")
                .totalDistanceKm(12.5)
                .estimatedDurationMinutes(30)
                .isActive(true)
                .build();
        route7.setStopsList(Arrays.asList("Sector 143", "Sector 142", "Sector 141", "Sector 140", "College Main Gate"));
        routes.add(route7);

        // Route 8 - Sector 62 to College (Return)
        BusRoute route8 = BusRoute.builder()
                .routeName("Sector 62 to College")
                .description("Bus service from Sector 62 to College")
                .startLocation("Sector 62")
                .endLocation("College Main Gate")
                .routeType("CITY")
                .totalDistanceKm(8.0)
                .estimatedDurationMinutes(20)
                .isActive(true)
                .build();
        route8.setStopsList(Arrays.asList("Sector 62", "Sector 61", "Sector 60", "Sector 59", "College Main Gate"));
        routes.add(route8);

        busRouteRepository.saveAll(routes);
        System.out.println("✅ " + routes.size() + " routes created");

        // ===== CREATE SCHEDULES =====
        List<BusSchedule> schedules = new ArrayList<>();

        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};

        // Schedules for Bus 1 on Route 1 (College to Sector 143)
        for (String day : days) {
            BusSchedule morning = BusSchedule.builder()
                    .bus(bus1)
                    .route(route1)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(8, 0))
                    .arrivalTime(LocalTime.of(8, 30))
                    .isActive(true)
                    .build();
            schedules.add(morning);

            BusSchedule evening = BusSchedule.builder()
                    .bus(bus1)
                    .route(route1)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(17, 0))
                    .arrivalTime(LocalTime.of(17, 30))
                    .isActive(true)
                    .build();
            schedules.add(evening);
        }

        // Schedules for Bus 2 on Route 2 (College to Sector 62)
        for (String day : days) {
            BusSchedule morning = BusSchedule.builder()
                    .bus(bus2)
                    .route(route2)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(8, 15))
                    .arrivalTime(LocalTime.of(8, 35))
                    .isActive(true)
                    .build();
            schedules.add(morning);

            BusSchedule evening = BusSchedule.builder()
                    .bus(bus2)
                    .route(route2)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(17, 15))
                    .arrivalTime(LocalTime.of(17, 35))
                    .isActive(true)
                    .build();
            schedules.add(evening);
        }

        // Schedules for Bus 3 on Route 3 (College to Sector 15)
        for (String day : days) {
            BusSchedule morning = BusSchedule.builder()
                    .bus(bus3)
                    .route(route3)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(8, 30))
                    .arrivalTime(LocalTime.of(8, 48))
                    .isActive(true)
                    .build();
            schedules.add(morning);

            BusSchedule evening = BusSchedule.builder()
                    .bus(bus3)
                    .route(route3)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(17, 30))
                    .arrivalTime(LocalTime.of(17, 48))
                    .isActive(true)
                    .build();
            schedules.add(evening);
        }

        // Additional schedules for return routes (Route 7 and 8)
        // Bus 1 on Route 7 (Sector 143 to College)
        for (String day : days) {
            BusSchedule morning = BusSchedule.builder()
                    .bus(bus1)
                    .route(route7)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(9, 0))
                    .arrivalTime(LocalTime.of(9, 30))
                    .isActive(true)
                    .build();
            schedules.add(morning);

            BusSchedule evening = BusSchedule.builder()
                    .bus(bus1)
                    .route(route7)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(18, 0))
                    .arrivalTime(LocalTime.of(18, 30))
                    .isActive(true)
                    .build();
            schedules.add(evening);
        }

        // Bus 2 on Route 8 (Sector 62 to College)
        for (String day : days) {
            BusSchedule morning = BusSchedule.builder()
                    .bus(bus2)
                    .route(route8)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(9, 15))
                    .arrivalTime(LocalTime.of(9, 35))
                    .isActive(true)
                    .build();
            schedules.add(morning);

            BusSchedule evening = BusSchedule.builder()
                    .bus(bus2)
                    .route(route8)
                    .dayOfWeek(day)
                    .departureTime(LocalTime.of(18, 15))
                    .arrivalTime(LocalTime.of(18, 35))
                    .isActive(true)
                    .build();
            schedules.add(evening);
        }

        busScheduleRepository.saveAll(schedules);
        System.out.println("✅ " + schedules.size() + " schedules created");

        // ===== CREATE BUS LOCATIONS (For tracking) =====
        List<BusLocation> locations = new ArrayList<>();

        BusLocation loc1 = BusLocation.builder()
                .bus(bus1)
                .latitude(28.6129)
                .longitude(77.2295)
                .locationName("College Main Gate")
                .speedKmh(0.0)
                .headingDegrees(0.0)
                .timestamp(LocalDateTime.now())
                .isActive(true)
                .build();
        locations.add(loc1);

        BusLocation loc2 = BusLocation.builder()
                .bus(bus2)
                .latitude(28.6139)
                .longitude(77.2290)
                .locationName("Sector 62")
                .speedKmh(0.0)
                .headingDegrees(0.0)
                .timestamp(LocalDateTime.now())
                .isActive(true)
                .build();
        locations.add(loc2);

        BusLocation loc3 = BusLocation.builder()
                .bus(bus3)
                .latitude(28.6145)
                .longitude(77.2285)
                .locationName("Sector 143")
                .speedKmh(0.0)
                .headingDegrees(0.0)
                .timestamp(LocalDateTime.now())
                .isActive(true)
                .build();
        locations.add(loc3);

        busLocationRepository.saveAll(locations);
        System.out.println("✅ " + locations.size() + " bus locations created");

        System.out.println("✅ Transport Data Initialization Complete!");
    }
}