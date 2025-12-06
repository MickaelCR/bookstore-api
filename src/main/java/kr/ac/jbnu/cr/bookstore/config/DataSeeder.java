package kr.ac.jbnu.cr.bookstore.config;

import kr.ac.jbnu.cr.bookstore.model.*;
import kr.ac.jbnu.cr.bookstore.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random(42);

    public DataSeeder(UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      BookRepository bookRepository,
                      ReviewRepository reviewRepository,
                      FavoriteRepository favoriteRepository,
                      CartRepository cartRepository,
                      CartItemRepository cartItemRepository,
                      OrderRepository orderRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already seeded. Skipping...");
            return;
        }

        logger.info("Starting database seeding...");

        List<User> users = seedUsers();
        List<Category> categories = seedCategories();
        List<Book> books = seedBooks(categories);
        seedReviews(users, books);
        seedFavorites(users, books);
        seedOrders(users, books);

        logger.info("Database seeding completed!");
        logger.info("Created: {} users, {} categories, {} books",
                users.size(), categories.size(), books.size());
    }

    private List<User> seedUsers() {
        List<User> users = new ArrayList<>();

        // Admin user
        users.add(User.builder()
                .email("admin@bookstore.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .username("Admin")
                .phoneNumber("010-1234-5678")
                .role(Role.ADMIN)
                .birthDate(LocalDate.of(1985, 3, 15))
                .bio("Bookstore administrator")
                .build());

        // Regular users with Korean names
        String[][] userData = {
                {"kim.minjun@email.com", "Kim Minjun", "010-2345-6789", "1990-05-20", "Avid reader and book collector"},
                {"lee.soyeon@email.com", "Lee Soyeon", "010-3456-7890", "1992-08-12", "Literature enthusiast"},
                {"park.jihoon@email.com", "Park Jihoon", "010-4567-8901", "1988-11-30", "Science fiction lover"},
                {"choi.eunji@email.com", "Choi Eunji", "010-5678-9012", "1995-02-14", "Romance novel fan"},
                {"jung.hyunwoo@email.com", "Jung Hyunwoo", "010-6789-0123", "1991-07-25", "History book enthusiast"},
                {"kang.minji@email.com", "Kang Minji", "010-7890-1234", "1993-04-08", "Self-help book reader"},
                {"yoon.seojun@email.com", "Yoon Seojun", "010-8901-2345", "1989-09-17", "Technical book collector"},
                {"han.yuna@email.com", "Han Yuna", "010-9012-3456", "1994-12-03", "Children's book lover"},
                {"shin.dongwook@email.com", "Shin Dongwook", "010-0123-4567", "1987-06-22", "Business book reader"},
                {"lim.subin@email.com", "Lim Subin", "010-1234-5670", "1996-01-09", "Poetry enthusiast"},
                {"hong.jiwon@email.com", "Hong Jiwon", "010-2345-6781", "1990-10-28", "Mystery novel fan"},
                {"bae.youngho@email.com", "Bae Youngho", "010-3456-7892", "1986-03-11", "Philosophy reader"}
        };

        for (String[] data : userData) {
            users.add(User.builder()
                    .email(data[0])
                    .passwordHash(passwordEncoder.encode("password123"))
                    .username(data[1])
                    .phoneNumber(data[2])
                    .role(Role.USER)
                    .birthDate(LocalDate.parse(data[3]))
                    .bio(data[4])
                    .build());
        }

        return userRepository.saveAll(users);
    }

    private List<Category> seedCategories() {
        String[][] categoryData = {
                {"Fiction", "Novels, short stories, and literary fiction"},
                {"Non-Fiction", "Factual books including biographies and essays"},
                {"Science Fiction", "Futuristic and speculative fiction"},
                {"Fantasy", "Magical worlds and mythical creatures"},
                {"Mystery & Thriller", "Suspenseful and detective stories"},
                {"Romance", "Love stories and romantic fiction"},
                {"Horror", "Scary and supernatural stories"},
                {"Biography", "Life stories of real people"},
                {"History", "Historical events and periods"},
                {"Science", "Scientific discoveries and theories"},
                {"Technology", "Computing, engineering, and tech trends"},
                {"Business", "Management, finance, and entrepreneurship"},
                {"Self-Help", "Personal development and motivation"},
                {"Health & Wellness", "Fitness, nutrition, and mental health"},
                {"Cooking", "Recipes and culinary arts"},
                {"Travel", "Travel guides and adventures"},
                {"Art & Photography", "Visual arts and photography"},
                {"Children's Books", "Books for young readers"},
                {"Young Adult", "Books for teenage readers"},
                {"Poetry", "Poems and verse collections"}
        };

        List<Category> categories = new ArrayList<>();
        for (String[] data : categoryData) {
            categories.add(Category.builder()
                    .name(data[0])
                    .description(data[1])
                    .build());
        }

        return categoryRepository.saveAll(categories);
    }

    private List<Book> seedBooks(List<Category> categories) {
        List<Book> books = new ArrayList<>();

        // Fiction books
        Category fiction = findCategory(categories, "Fiction");
        Category sciFi = findCategory(categories, "Science Fiction");
        Category fantasy = findCategory(categories, "Fantasy");
        Category mystery = findCategory(categories, "Mystery & Thriller");
        Category romance = findCategory(categories, "Romance");
        Category horror = findCategory(categories, "Horror");
        Category biography = findCategory(categories, "Biography");
        Category history = findCategory(categories, "History");
        Category science = findCategory(categories, "Science");
        Category technology = findCategory(categories, "Technology");
        Category business = findCategory(categories, "Business");
        Category selfHelp = findCategory(categories, "Self-Help");
        Category health = findCategory(categories, "Health & Wellness");
        Category cooking = findCategory(categories, "Cooking");
        Category travel = findCategory(categories, "Travel");
        Category children = findCategory(categories, "Children's Books");
        Category youngAdult = findCategory(categories, "Young Adult");
        Category poetry = findCategory(categories, "Poetry");

        // Literary Fiction
        books.add(createBook("The Silent Echo", "Park Kyung-ni", "Moonlight Publishing",
                "A profound exploration of family secrets spanning three generations in rural Korea.",
                "978-89-01-00001-1", 18500, 45, fiction));

        books.add(createBook("Autumn Leaves Fall", "Kim Young-ha", "Seoul Books",
                "A melancholic tale of lost love and redemption set in 1980s Seoul.",
                "978-89-01-00002-8", 16000, 62, fiction));

        books.add(createBook("The Glass Garden", "Han Kang", "Changbi Publishers",
                "An introspective novel about a woman's journey to find herself.",
                "978-89-01-00003-5", 19000, 38, fiction));

        books.add(createBook("Midnight Train to Busan", "Cho Nam-joo", "Minumsa",
                "A gripping story of strangers whose lives intertwine on a night train.",
                "978-89-01-00004-2", 17500, 55, fiction));

        books.add(createBook("The Fisherman's Daughter", "Shin Kyung-sook", "Munhakdongne",
                "A poignant story of sacrifice and love in a small coastal village.",
                "978-89-01-00005-9", 15500, 70, fiction));

        // Science Fiction
        books.add(createBook("Beyond the Nebula", "Lee Sang-hyuk", "SF Korea",
                "Humanity's first contact with an alien civilization challenges everything we know.",
                "978-89-02-00001-0", 21000, 40, sciFi));

        books.add(createBook("The Android's Dream", "Bae Myung-hoon", "Alma Publishing",
                "In 2150, an android questions the nature of consciousness and humanity.",
                "978-89-02-00002-7", 19500, 35, sciFi));

        books.add(createBook("Quantum Echoes", "Kim Bo-young", "Arzak",
                "A physicist discovers messages from parallel universes.",
                "978-89-02-00003-4", 22000, 28, sciFi));

        books.add(createBook("The Mars Collective", "Park Min-gyu", "Munhakdongne",
                "Life in the first permanent Mars colony tests human resilience.",
                "978-89-02-00004-1", 20000, 42, sciFi));

        books.add(createBook("Neural Link", "Djuna", "Viche",
                "When minds can connect directly, privacy becomes the ultimate luxury.",
                "978-89-02-00005-8", 18500, 50, sciFi));

        // Fantasy
        books.add(createBook("The Dragon's Heir", "Lee Yeong-do", "Golden Bough",
                "A young prince discovers his bloodline holds the key to saving the realm.",
                "978-89-03-00001-9", 23000, 65, fantasy));

        books.add(createBook("Shadows of the Moon Kingdom", "Jeon Sam-hye", "Fantasy Books",
                "An epic tale of magic, betrayal, and redemption in ancient Korea.",
                "978-89-03-00002-6", 21500, 48, fantasy));

        books.add(createBook("The Witch of Namsan", "Kim Cho-yeop", "Changbi",
                "A modern witch navigates Seoul's hidden magical underworld.",
                "978-89-03-00003-3", 17000, 72, fantasy));

        books.add(createBook("Guardians of the Four Seas", "Son Won-pyeong", "Munhakdongne",
                "Four elemental guardians must reunite to prevent catastrophe.",
                "978-89-03-00004-0", 19500, 55, fantasy));

        books.add(createBook("The Immortal's Garden", "Chung Serang", "Arzak",
                "A thousand-year-old immortal finds unexpected connection in modern times.",
                "978-89-03-00005-7", 18000, 60, fantasy));

        // Mystery & Thriller
        books.add(createBook("The Seventh Witness", "Kim Un-su", "Munhakdongne",
                "A detective uncovers a conspiracy that reaches the highest levels of power.",
                "978-89-04-00001-8", 16500, 80, mystery));

        books.add(createBook("Midnight in Gangnam", "Jeong Yu-jeong", "Eunhaengnamu",
                "A series of murders in Seoul's wealthiest district reveals dark secrets.",
                "978-89-04-00002-5", 18000, 95, mystery));

        books.add(createBook("The Perfect Alibi", "You Jeong", "Changbi",
                "A lawyer defends a client she suspects may actually be guilty.",
                "978-89-04-00003-2", 17500, 67, mystery));

        books.add(createBook("Traces of Blood", "Kim Young-ha", "Munhakdongne",
                "A forensic scientist becomes entangled in a case that hits too close to home.",
                "978-89-04-00004-9", 19000, 52, mystery));

        books.add(createBook("The Silent Neighbor", "Yun Ko-eun", "Minumsa",
                "Strange occurrences in an apartment complex lead to terrifying discoveries.",
                "978-89-04-00005-6", 16000, 88, mystery));

        // Romance
        books.add(createBook("Spring in Seoul", "Kim Rae-won", "Romance House",
                "Two strangers meet during cherry blossom season and find unexpected love.",
                "978-89-05-00001-7", 14500, 120, romance));

        books.add(createBook("The Coffeehouse Promise", "Lee Da-hye", "Love Books",
                "A barista and a regular customer share more than just coffee.",
                "978-89-05-00002-4", 13500, 95, romance));

        books.add(createBook("Second Chance at Jeju", "Park So-young", "Heart Publishing",
                "Ex-lovers reunite on Jeju Island ten years after their breakup.",
                "978-89-05-00003-1", 15000, 82, romance));

        books.add(createBook("The Bookshop of Dreams", "Han Ji-min", "Moonlight",
                "A bookshop owner finds love through the books she recommends.",
                "978-89-05-00004-8", 14000, 110, romance));

        books.add(createBook("Rainy Days in Busan", "Choi Eun-young", "Romance House",
                "A photographer and a marine biologist discover love by the sea.",
                "978-89-05-00005-5", 15500, 75, romance));

        // Horror
        books.add(createBook("The Haunting of Gwangju", "Hwang Sun-mi", "Dark Pages",
                "A family moves into a house with a terrifying history.",
                "978-89-06-00001-6", 17000, 45, horror));

        books.add(createBook("Whispers in the Dark", "Kim Ae-ran", "Horror House",
                "A psychiatrist's patients share stories that become all too real.",
                "978-89-06-00002-3", 16500, 38, horror));

        books.add(createBook("The Cursed Village", "Jeong Se-rang", "Munhakdongne",
                "Investigators uncover ancient evil in a remote mountain village.",
                "978-89-06-00003-0", 18000, 52, horror));

        // Biography
        books.add(createBook("The Innovator's Journey", "Lee Kun-hee Foundation", "Business Books",
                "The life and legacy of Korea's most influential business leader.",
                "978-89-07-00001-5", 25000, 35, biography));

        books.add(createBook("Running Toward Dreams", "Son Heung-min", "Sports Publishing",
                "The inspiring autobiography of Korea's football superstar.",
                "978-89-07-00002-2", 22000, 150, biography));

        books.add(createBook("Notes from a Kitchen", "Baek Jong-won", "Cuisine Books",
                "The culinary journey of Korea's most beloved chef.",
                "978-89-07-00003-9", 23000, 88, biography));

        books.add(createBook("Breaking Barriers", "Kim Yuna", "Wisdom House",
                "The story of Korea's figure skating legend.",
                "978-89-07-00004-6", 21000, 120, biography));

        // History
        books.add(createBook("Joseon: A Complete History", "Prof. Lee Ki-baik", "History Press",
                "Comprehensive account of the Joseon Dynasty from 1392 to 1897.",
                "978-89-08-00001-4", 35000, 25, history));

        books.add(createBook("The Korean War: Untold Stories", "Prof. Park Myung-lim", "Historical Society",
                "Personal accounts and newly discovered facts about the Korean War.",
                "978-89-08-00002-1", 28000, 40, history));

        books.add(createBook("Ancient Kingdoms of Korea", "Prof. Kim Won-yong", "Academic Press",
                "Archaeological discoveries reveal the glory of Goguryeo, Baekje, and Silla.",
                "978-89-08-00003-8", 32000, 30, history));

        books.add(createBook("Modern Korea: 1945-Present", "Prof. Choi Jang-jip", "Contemporary Books",
                "Korea's transformation from war-torn nation to global power.",
                "978-89-08-00004-5", 27000, 55, history));

        // Science
        books.add(createBook("The Universe in Your Hand", "Dr. Kim Sang-wook", "Science Books",
                "Making astrophysics accessible to everyday readers.",
                "978-89-09-00001-3", 19000, 75, science));

        books.add(createBook("The Brain Explained", "Dr. Jung Jae-seung", "Medical Press",
                "A neuroscientist's guide to understanding how our minds work.",
                "978-89-09-00002-0", 21000, 60, science));

        books.add(createBook("Climate Change Korea", "Prof. Cho Chun-ho", "Environment Press",
                "How climate change is affecting the Korean peninsula.",
                "978-89-09-00003-7", 18500, 45, science));

        books.add(createBook("The Gene Revolution", "Dr. Lee Hong-kyu", "Bio Publishing",
                "CRISPR and the future of genetic engineering.",
                "978-89-09-00004-4", 22000, 38, science));

        // Technology
        books.add(createBook("AI: The Korean Perspective", "Dr. Lee Sang-wan", "Tech Books",
                "How Korea is leading the artificial intelligence revolution.",
                "978-89-10-00001-2", 24000, 55, technology));

        books.add(createBook("Semiconductor Nation", "Kim Ki-nam", "Industry Press",
                "Inside Korea's dominance in the global chip industry.",
                "978-89-10-00002-9", 26000, 42, technology));

        books.add(createBook("The 5G Revolution", "Prof. Park Sung-ho", "Telecom Books",
                "How next-generation networks are transforming society.",
                "978-89-10-00003-6", 21000, 65, technology));

        books.add(createBook("Blockchain Korea", "Lee Seung-gun", "Fintech Press",
                "The potential of blockchain technology in Korean industries.",
                "978-89-10-00004-3", 19500, 48, technology));

        books.add(createBook("Robotics and the Future", "Dr. Oh Jun-ho", "KAIST Press",
                "From HUBO to humanoids: Korea's robotics journey.",
                "978-89-10-00005-0", 23000, 35, technology));

        // Business
        books.add(createBook("The Samsung Way", "Prof. Song Jae-yong", "Business Press",
                "Management lessons from Korea's largest conglomerate.",
                "978-89-11-00001-1", 25000, 80, business));

        books.add(createBook("K-Startup Playbook", "Bae Ki-hong", "Entrepreneur Books",
                "How Korean startups are disrupting global markets.",
                "978-89-11-00002-8", 22000, 65, business));

        books.add(createBook("The Art of Korean Negotiation", "Prof. Lee Dong-ki", "MBA Press",
                "Cultural insights for successful business dealings in Korea.",
                "978-89-11-00003-5", 19000, 55, business));

        books.add(createBook("Hallyu Economics", "Dr. Kim Jin-woo", "Culture Economy",
                "The economic impact of the Korean Wave phenomenon.",
                "978-89-11-00004-2", 21000, 70, business));

        books.add(createBook("From Crisis to Comeback", "Prof. Jang Ha-sung", "Economic Books",
                "Lessons from Korea's 1997 financial crisis recovery.",
                "978-89-11-00005-9", 23000, 45, business));

        // Self-Help
        books.add(createBook("The Courage to Be Yourself", "Kim Nan-do", "Wisdom House",
                "Finding your authentic self in a conformist society.",
                "978-89-12-00001-0", 15000, 200, selfHelp));

        books.add(createBook("Minimalist Life", "Jeong Hee-won", "Simple Living",
                "Decluttering your life for greater happiness.",
                "978-89-12-00002-7", 14500, 150, selfHelp));

        books.add(createBook("The Art of Slow Living", "Heo Tae-gyun", "Mindfulness Press",
                "Finding peace in Korea's fast-paced society.",
                "978-89-12-00003-4", 16000, 120, selfHelp));

        books.add(createBook("Career Reset", "Kim Mi-kyung", "Job Books",
                "Reinventing yourself in the age of automation.",
                "978-89-12-00004-1", 17000, 90, selfHelp));

        books.add(createBook("The Happiness Formula", "Dr. Choi In-cheol", "Psychology Press",
                "Science-based strategies for lasting well-being.",
                "978-89-12-00005-8", 18000, 110, selfHelp));

        // Health & Wellness
        books.add(createBook("Korean Temple Food", "Ven. Jeong Kwan", "Temple Publishing",
                "Ancient recipes for modern health and mindfulness.",
                "978-89-13-00001-9", 28000, 55, health));

        books.add(createBook("The Korean Skincare Bible", "Dr. Park Ji-yeon", "Beauty Press",
                "Secrets behind Korea's world-famous skincare routines.",
                "978-89-13-00002-6", 22000, 130, health));

        books.add(createBook("Mountain Hiking Korea", "Kim Young-mi", "Outdoor Books",
                "Guide to Korea's best hiking trails for fitness and wellness.",
                "978-89-13-00003-3", 19000, 75, health));

        books.add(createBook("Traditional Korean Medicine Today", "Dr. Kim Nam-il", "Oriental Medicine",
                "Integrating ancient wisdom with modern healthcare.",
                "978-89-13-00004-0", 24000, 45, health));

        // Cooking
        books.add(createBook("Authentic Korean Home Cooking", "Maangchi", "Cookbook House",
                "Classic recipes passed down through generations.",
                "978-89-14-00001-8", 32000, 95, cooking));

        books.add(createBook("Korean Street Food", "Hong Shin-ae", "Food Publishing",
                "Recreate your favorite street food at home.",
                "978-89-14-00002-5", 25000, 110, cooking));

        books.add(createBook("The Kimchi Cookbook", "Dr. Park Kun-young", "Fermentation Press",
                "Over 100 recipes featuring Korea's iconic dish.",
                "978-89-14-00003-2", 27000, 85, cooking));

        books.add(createBook("Modern Korean Cuisine", "Chef Yim Jung-sik", "Gastronomy Books",
                "Fine dining recipes with Korean flavors.",
                "978-89-14-00004-9", 35000, 40, cooking));

        books.add(createBook("Korean BBQ at Home", "Chef Paik Jong-won", "Grill Masters",
                "Master the art of Korean barbecue.",
                "978-89-14-00005-6", 28000, 150, cooking));

        // Travel
        books.add(createBook("Hidden Gems of Korea", "Lee Sun-hee", "Travel Books",
                "Off-the-beaten-path destinations across the peninsula.",
                "978-89-15-00001-7", 23000, 65, travel));

        books.add(createBook("Seoul: A Walking Guide", "Robert Koehler", "Seoul Selection",
                "Discover Seoul's neighborhoods on foot.",
                "978-89-15-00002-4", 19000, 90, travel));

        books.add(createBook("Jeju Island Complete Guide", "Jeju Tourism", "Island Press",
                "Everything you need to know about Korea's paradise island.",
                "978-89-15-00003-1", 21000, 120, travel));

        books.add(createBook("Temple Stay Korea", "Cultural Heritage Admin", "Temple Books",
                "Guide to Korea's best temple stay experiences.",
                "978-89-15-00004-8", 18000, 55, travel));

        // Children's Books
        books.add(createBook("The Little Tiger of Baekdu", "Kwon Yoon-duck", "Kids Publishing",
                "A brave little tiger's adventure in the mountains.",
                "978-89-16-00001-6", 12000, 200, children));

        books.add(createBook("Hanji and the Magic Paper", "Lee Young-gyeong", "Children's House",
                "A girl discovers magic in traditional Korean paper.",
                "978-89-16-00002-3", 13000, 180, children));

        books.add(createBook("Grandmother's Stories", "Hwang Sun-mi", "Story Books",
                "Traditional Korean folktales for young readers.",
                "978-89-16-00003-0", 14000, 150, children));

        books.add(createBook("The Adventures of Ddochi", "Animation Studio", "Cartoon Books",
                "Korea's beloved animated character in book form.",
                "978-89-16-00004-7", 11000, 220, children));

        books.add(createBook("Korean ABCs", "Kim Eun-jung", "Education Press",
                "Learning Hangul through fun illustrations.",
                "978-89-16-00005-4", 15000, 175, children));

        // Young Adult
        books.add(createBook("High School Dreams", "Park So-yeon", "Teen Books",
                "A coming-of-age story set in a Seoul high school.",
                "978-89-17-00001-5", 14000, 95, youngAdult));

        books.add(createBook("The K-Pop Trainee", "Kim Ha-na", "Youth Publishing",
                "Behind the scenes of the K-pop training system.",
                "978-89-17-00002-2", 15000, 140, youngAdult));

        books.add(createBook("Summer at Grandmother's", "Yoon Sung-hee", "Coming of Age",
                "A teenager discovers family secrets during summer vacation.",
                "978-89-17-00003-9", 13500, 85, youngAdult));

        books.add(createBook("The Gaming Champion", "Lee Jae-young", "Esports Books",
                "A high schooler's journey to become a pro gamer.",
                "978-89-17-00004-6", 14500, 110, youngAdult));

        // Poetry
        books.add(createBook("Moonlit Verses", "Yun Dong-ju", "Poetry House",
                "Collected poems of Korea's beloved poet.",
                "978-89-18-00001-4", 16000, 65, poetry));

        books.add(createBook("Han River Dreams", "Ko Un", "Verse Publishing",
                "Contemporary poems reflecting on Korean identity.",
                "978-89-18-00002-1", 18000, 45, poetry));

        books.add(createBook("Cherry Blossom Songs", "Kim So-wol", "Classic Poetry",
                "Timeless Korean poems about nature and love.",
                "978-89-18-00003-8", 15000, 80, poetry));

        books.add(createBook("Urban Rhythms", "Kim Hyesoon", "Modern Poetry",
                "Bold contemporary poetry about city life.",
                "978-89-18-00004-5", 17000, 55, poetry));

        // Add more view counts randomly
        for (Book book : books) {
            book.setViewCount((long) random.nextInt(500) + 10);
        }

        return bookRepository.saveAll(books);
    }

    private Book createBook(String title, String author, String publisher,
                            String summary, String isbn, int price, int stock, Category category) {
        Book book = Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher)
                .summary(summary)
                .isbn(isbn)
                .price(BigDecimal.valueOf(price))
                .stockQuantity(stock)
                .publicationDate(LocalDate.now().minusDays(random.nextInt(1000)))
                .viewCount(0L)
                .isActive(true)
                .categories(new ArrayList<>())
                .build();

        book.getCategories().add(category);
        return book;
    }

    private Category findCategory(List<Category> categories, String name) {
        return categories.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private void seedReviews(List<User> users, List<Book> books) {
        String[] comments = {
                "Absolutely loved this book! Couldn't put it down.",
                "A masterpiece of storytelling. Highly recommended.",
                "Good read, but the ending was a bit predictable.",
                "Beautiful prose and compelling characters.",
                "This book changed my perspective on life.",
                "Entertaining but not particularly deep.",
                "A must-read for anyone interested in this genre.",
                "Well-researched and thoughtfully written.",
                "The author has a unique voice that shines through.",
                "Interesting concept but execution could be better.",
                "Perfect for a weekend read.",
                "One of the best books I've read this year.",
                "Engaging from start to finish.",
                "A bit slow in the middle but worth finishing.",
                "Exceeded my expectations in every way."
        };

        List<Review> reviews = new ArrayList<>();
        List<User> regularUsers = users.stream()
                .filter(u -> u.getRole() == Role.USER)
                .toList();

        for (int i = 0; i < 60; i++) {
            User user = regularUsers.get(random.nextInt(regularUsers.size()));
            Book book = books.get(random.nextInt(books.size()));

            // Check if user already reviewed this book
            boolean alreadyReviewed = reviews.stream()
                    .anyMatch(r -> r.getUser().getId().equals(user.getId())
                            && r.getBook().getId().equals(book.getId()));

            if (!alreadyReviewed) {
                reviews.add(Review.builder()
                        .user(user)
                        .book(book)
                        .rating(random.nextInt(3) + 3) // 3-5 stars
                        .comment(comments[random.nextInt(comments.length)])
                        .build());
            }
        }

        reviewRepository.saveAll(reviews);
        logger.info("Created {} reviews", reviews.size());
    }

    private void seedFavorites(List<User> users, List<Book> books) {
        List<Favorite> favorites = new ArrayList<>();
        List<User> regularUsers = users.stream()
                .filter(u -> u.getRole() == Role.USER)
                .toList();

        for (User user : regularUsers) {
            int numFavorites = random.nextInt(5) + 2; // 2-6 favorites per user
            List<Book> userFavorites = new ArrayList<>();

            for (int i = 0; i < numFavorites; i++) {
                Book book = books.get(random.nextInt(books.size()));
                if (!userFavorites.contains(book)) {
                    userFavorites.add(book);
                    favorites.add(Favorite.builder()
                            .user(user)
                            .book(book)
                            .build());
                }
            }
        }

        favoriteRepository.saveAll(favorites);
        logger.info("Created {} favorites", favorites.size());
    }

    private void seedOrders(List<User> users, List<Book> books) {
        List<User> regularUsers = users.stream()
                .filter(u -> u.getRole() == Role.USER)
                .toList();

        int orderCount = 0;

        for (User user : regularUsers) {
            int numOrders = random.nextInt(3) + 1; // 1-3 orders per user

            for (int i = 0; i < numOrders; i++) {
                Order order = Order.builder()
                        .user(user)
                        .status(OrderStatus.values()[random.nextInt(4)]) // Random status except CANCELLED
                        .totalAmount(BigDecimal.ZERO)
                        .items(new ArrayList<>())
                        .build();

                BigDecimal total = BigDecimal.ZERO;
                int numItems = random.nextInt(3) + 1; // 1-3 items per order

                for (int j = 0; j < numItems; j++) {
                    Book book = books.get(random.nextInt(books.size()));
                    int quantity = random.nextInt(2) + 1;
                    BigDecimal unitPrice = book.getPrice();
                    BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

                    OrderItem item = OrderItem.builder()
                            .book(book)
                            .quantity(quantity)
                            .unitPrice(unitPrice)
                            .totalPrice(itemTotal)
                            .build();

                    order.addItem(item);
                    total = total.add(itemTotal);
                }

                order.setTotalAmount(total);
                orderRepository.save(order);
                orderCount++;
            }
        }

        logger.info("Created {} orders", orderCount);
    }
}