package E2E;

import DAO.UserDB;
import DAO.ProductDB;
import DAO.CartDB;
import DAO.OrderDB;
import DAO.DiscountDB;
import Model.user;
import Model.Product;
import Model.Cart;
import Model.CartItems;
import Model.Order;
import Model.Discount;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Utility class để lấy test data từ database cho E2E tests và Unit tests.
 * 
 * Cung cấp các method để:
 * - Lấy random user theo role
 * - Lấy random product, cart, order từ database
 * - Lấy data cụ thể theo ID
 * - Kiểm tra data có tồn tại không
 */
public class TestDataHelper {
    
    private static final UserDB userDB = new UserDB();
    private static final ProductDB productDB = new ProductDB();
    private static final CartDB cartDB = new CartDB();
    private static final OrderDB orderDB = new OrderDB();
    private static final DiscountDB discountDB = new DiscountDB();
    private static final Random random = new Random();
    
    /**
     * Lấy một user ngẫu nhiên theo role từ database.
     * 
     * @param role Role cần lấy (USER, ADMIN, etc.)
     * @return User object hoặc null nếu không tìm thấy
     */
    public static user getRandomUserByRole(String role) {
        List<user> users = userDB.getUsersByRole(role);
        if (users == null || users.isEmpty()) {
            System.out.println("[TestDataHelper] Không tìm thấy user nào với role: " + role);
            return null;
        }
        user selectedUser = users.get(random.nextInt(users.size()));
        System.out.println("[TestDataHelper] Chọn random user: " + selectedUser.getEmail() + " (role: " + role + ")");
        return selectedUser;
    }
    
    /**
     * Lấy một user ngẫu nhiên với role USER.
     * 
     * @return User object hoặc null nếu không tìm thấy
     */
    public static user getRandomUser() {
        return getRandomUserByRole("USER");
    }
    
    /**
     * Lấy một admin user ngẫu nhiên.
     * 
     * @return User object hoặc null nếu không tìm thấy
     */
    public static user getRandomAdmin() {
        return getRandomUserByRole("ADMIN");
    }
    
    /**
     * Lấy user cụ thể theo email.
     * 
     * @param email Email của user
     * @return User object hoặc null nếu không tìm thấy
     */
    public static user getUserByEmail(String email) {
        user u = userDB.getUserByEmail(email);
        if (u != null) {
            System.out.println("[TestDataHelper] Tìm thấy user: " + email + " (role: " + u.getRole() + ")");
        } else {
            System.out.println("[TestDataHelper] Không tìm thấy user với email: " + email);
        }
        return u;
    }
    
    /**
     * Lấy user cụ thể theo ID.
     * 
     * @param userId ID của user
     * @return User object hoặc null nếu không tìm thấy
     */
    public static user getUserById(int userId) {
        user u = userDB.getUserById(userId);
        if (u != null) {
            System.out.println("[TestDataHelper] Tìm thấy user ID " + userId + ": " + u.getEmail());
        } else {
            System.out.println("[TestDataHelper] Không tìm thấy user với ID: " + userId);
        }
        return u;
    }
    
    /**
     * Lấy tất cả users theo role.
     * 
     * @param role Role cần lấy
     * @return List of users
     */
    public static List<user> getAllUsersByRole(String role) {
        List<user> users = userDB.getUsersByRole(role);
        System.out.println("[TestDataHelper] Tìm thấy " + (users != null ? users.size() : 0) + " users với role: " + role);
        return users;
    }
    
    /**
     * Kiểm tra có user nào với role cụ thể không.
     * 
     * @param role Role cần kiểm tra
     * @return true nếu có ít nhất 1 user
     */
    public static boolean hasUserWithRole(String role) {
        List<user> users = userDB.getUsersByRole(role);
        boolean hasUser = users != null && !users.isEmpty();
        System.out.println("[TestDataHelper] Có user với role " + role + ": " + hasUser);
        return hasUser;
    }
    
    /**
     * Lấy password của user (lưu ý: password trong DB có thể đã hash).
     * 
     * @param user User object
     * @return Password (có thể là hash)
     */
    public static String getUserPassword(user user) {
        if (user == null) {
            return null;
        }
        // Lưu ý: Password trong DB có thể đã được hash
        // Nếu cần password gốc, cần có cách lưu riêng cho test
        return user.getPassword();
    }
    
    /**
     * Lấy user với password đã biết (dùng cho test).
     * Tìm user có email và password match.
     * 
     * @param email Email của user
     * @param expectedPassword Password mong đợi (có thể là hash hoặc plain text tùy hệ thống)
     * @return User object hoặc null
     */
    public static user getUserWithPassword(String email, String expectedPassword) {
        user u = userDB.getUserByEmail(email);
        if (u != null && u.getPassword() != null) {
            // So sánh password (có thể cần hash nếu DB lưu hash)
            if (u.getPassword().equals(expectedPassword)) {
                return u;
            }
        }
        return null;
    }
    
    /**
     * Tạo test credentials từ user.
     * 
     * @param user User object
     * @return TestCredentials object chứa email và password
     */
    public static TestCredentials getTestCredentials(user user) {
        if (user == null) {
            return null;
        }
        return new TestCredentials(user.getEmail(), user.getPassword());
    }
    
    /**
     * Inner class để lưu test credentials.
     */
    public static class TestCredentials {
        private final String email;
        private final String password;
        
        public TestCredentials(String email, String password) {
            this.email = email;
            this.password = password;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getPassword() {
            return password;
        }
        
        @Override
        public String toString() {
            return "TestCredentials{email='" + email + "', password='***'}";
        }
    }
    
    /**
     * Lấy user đầu tiên với role cụ thể (không random).
     * 
     * @param role Role cần lấy
     * @return User object hoặc null
     */
    public static user getFirstUserByRole(String role) {
        List<user> users = userDB.getUsersByRole(role);
        if (users == null || users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }
    
    /**
     * In thông tin tất cả users (để debug).
     */
    public static void printAllUsers() {
        List<user> allUsers = userDB.getAllUsers();
        System.out.println("[TestDataHelper] Tổng số users trong DB: " + (allUsers != null ? allUsers.size() : 0));
        if (allUsers != null) {
            allUsers.forEach(u -> 
                System.out.println("  - " + u.getEmail() + " (ID: " + u.getUser_id() + ", Role: " + u.getRole() + ")")
            );
        }
    }
    
    /**
     * In thông tin users theo role (để debug).
     */
    public static void printUsersByRole(String role) {
        List<user> users = getAllUsersByRole(role);
        System.out.println("[TestDataHelper] Users với role '" + role + "':");
        if (users != null) {
            users.forEach(u -> 
                System.out.println("  - " + u.getEmail() + " (ID: " + u.getUser_id() + ")")
            );
        }
    }
    
    // ========== PRODUCT METHODS ==========
    
    /**
     * Lấy một product ngẫu nhiên từ database.
     * 
     * @return Product object hoặc null nếu không tìm thấy
     */
    public static Product getRandomProduct() {
        List<Product> products = productDB.getAllProducts();
        if (products == null || products.isEmpty()) {
            System.out.println("[TestDataHelper] Không tìm thấy product nào trong database");
            return null;
        }
        Product selected = products.get(random.nextInt(products.size()));
        System.out.println("[TestDataHelper] Chọn random product: " + selected.getName() + " (ID: " + selected.getProductId() + ")");
        return selected;
    }
    
    /**
     * Lấy product cụ thể theo ID.
     * 
     * @param productId ID của product
     * @return Product object hoặc null nếu không tìm thấy
     */
    public static Product getProductById(int productId) {
        Product product = productDB.getProductById(productId);
        if (product != null) {
            System.out.println("[TestDataHelper] Tìm thấy product ID " + productId + ": " + product.getName());
        } else {
            System.out.println("[TestDataHelper] Không tìm thấy product với ID: " + productId);
        }
        return product;
    }
    
    /**
     * Lấy tất cả products từ database.
     * 
     * @return List of products
     */
    public static List<Product> getAllProducts() {
        List<Product> products = productDB.getAllProducts();
        System.out.println("[TestDataHelper] Tìm thấy " + (products != null ? products.size() : 0) + " products");
        return products;
    }
    
    /**
     * Lấy một product ID ngẫu nhiên.
     * 
     * @return Product ID hoặc null nếu không có product nào
     */
    public static Integer getRandomProductId() {
        List<Integer> productIds = productDB.getAllProductIds();
        if (productIds == null || productIds.isEmpty()) {
            return null;
        }
        return productIds.get(random.nextInt(productIds.size()));
    }
    
    /**
     * Tìm kiếm products theo keyword.
     * 
     * @param keyword Từ khóa tìm kiếm
     * @return List of products
     */
    public static List<Product> searchProducts(String keyword) {
        List<Product> products = productDB.searchProducts(keyword);
        System.out.println("[TestDataHelper] Tìm thấy " + (products != null ? products.size() : 0) + " products với keyword: " + keyword);
        return products;
    }
    
    /**
     * Lấy một product có stock > 0 (còn hàng).
     * 
     * @return Product object hoặc null nếu không tìm thấy
     */
    public static Product getRandomProductInStock() {
        List<Product> products = productDB.getAllProducts();
        if (products == null || products.isEmpty()) {
            return null;
        }
        List<Product> inStock = products.stream()
            .filter(p -> p.getStock() > 0)
            .collect(Collectors.toList());
        
        if (inStock.isEmpty()) {
            System.out.println("[TestDataHelper] Không có product nào còn hàng");
            return null;
        }
        
        Product selected = inStock.get(random.nextInt(inStock.size()));
        System.out.println("[TestDataHelper] Chọn random product còn hàng: " + selected.getName() + " (stock: " + selected.getStock() + ")");
        return selected;
    }
    
    /**
     * Kiểm tra có product nào trong database không.
     * 
     * @return true nếu có ít nhất 1 product
     */
    public static boolean hasProducts() {
        List<Product> products = productDB.getAllProducts();
        boolean hasProducts = products != null && !products.isEmpty();
        System.out.println("[TestDataHelper] Có products trong DB: " + hasProducts);
        return hasProducts;
    }
    
    // ========== CART METHODS ==========
    
    /**
     * Lấy cart của user cụ thể.
     * 
     * @param userId ID của user
     * @return Cart object hoặc null nếu không tìm thấy
     */
    public static Cart getCartByUserId(int userId) {
        Cart cart = cartDB.getCartByUserId(userId);
        if (cart != null) {
            System.out.println("[TestDataHelper] Tìm thấy cart cho user ID " + userId + ": cart_id=" + cart.getCart_id());
        } else {
            System.out.println("[TestDataHelper] Không tìm thấy cart cho user ID: " + userId);
        }
        return cart;
    }
    
    /**
     * Lấy cart items của cart cụ thể.
     * 
     * @param cartId ID của cart
     * @return List of CartItems
     */
    public static List<CartItems> getCartItemsByCartId(int cartId) {
        List<CartItems> items = cartDB.getCartItemsByCartId(cartId);
        System.out.println("[TestDataHelper] Tìm thấy " + (items != null ? items.size() : 0) + " items trong cart ID: " + cartId);
        return items;
    }
    
    /**
     * Lấy cart và items của user.
     * 
     * @param userId ID của user
     * @return CartWithItems object chứa cart và items
     */
    public static CartWithItems getCartWithItems(int userId) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            return null;
        }
        List<CartItems> items = getCartItemsByCartId(cart.getCart_id());
        return new CartWithItems(cart, items);
    }
    
    /**
     * Inner class để lưu cart và items.
     */
    public static class CartWithItems {
        private final Cart cart;
        private final List<CartItems> items;
        
        public CartWithItems(Cart cart, List<CartItems> items) {
            this.cart = cart;
            this.items = items;
        }
        
        public Cart getCart() {
            return cart;
        }
        
        public List<CartItems> getItems() {
            return items;
        }
    }
    
    // ========== ORDER METHODS ==========
    
    /**
     * Lấy orders của user cụ thể.
     * 
     * @param userId ID của user
     * @return List of Orders
     */
    public static List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = orderDB.listByUserId(userId);
        System.out.println("[TestDataHelper] Tìm thấy " + (orders != null ? orders.size() : 0) + " orders cho user ID: " + userId);
        return orders;
    }
    
    /**
     * Lấy một order ngẫu nhiên của user.
     * 
     * @param userId ID của user
     * @return Order object hoặc null nếu không tìm thấy
     */
    public static Order getRandomOrderByUserId(int userId) {
        List<Order> orders = getOrdersByUserId(userId);
        if (orders == null || orders.isEmpty()) {
            return null;
        }
        return orders.get(random.nextInt(orders.size()));
    }
    
    /**
     * Lấy order cụ thể theo ID.
     * 
     * @param orderId ID của order
     * @return Order object hoặc null nếu không tìm thấy
     */
    public static Order getOrderById(int orderId) {
        // Tìm trong tất cả users
        List<user> allUsers = userDB.getAllUsers();
        for (user u : allUsers) {
            List<Order> orders = orderDB.listByUserId(u.getUser_id());
            if (orders != null) {
                for (Order order : orders) {
                    if (order.getOrderId() == orderId) {
                        System.out.println("[TestDataHelper] Tìm thấy order ID " + orderId);
                        return order;
                    }
                }
            }
        }
        System.out.println("[TestDataHelper] Không tìm thấy order với ID: " + orderId);
        return null;
    }
    
    // ========== DISCOUNT METHODS ==========
    
    /**
     * Lấy một discount ngẫu nhiên từ database.
     * 
     * @return Discount object hoặc null nếu không tìm thấy
     */
    public static Discount getRandomDiscount() {
        // Lấy từ user (nếu có assigned discounts)
        user testUser = getRandomUser();
        if (testUser == null) {
            return null;
        }
        // Sử dụng listAssignedDiscountsForUser và convert sang Discount
        List<Model.UserDiscountAssign> userDiscounts = discountDB.listAssignedDiscountsForUser(testUser.getUser_id());
        if (userDiscounts == null || userDiscounts.isEmpty()) {
            System.out.println("[TestDataHelper] Không tìm thấy discount nào cho user");
            return null;
        }
        // Convert UserDiscountAssign sang Discount
        Model.UserDiscountAssign selected = userDiscounts.get(random.nextInt(userDiscounts.size()));
        Discount discount = new Discount();
        discount.setDiscountId(selected.getDiscountId());
        discount.setCode(selected.getCode());
        discount.setName(selected.getName());
        discount.setType(selected.getType());
        discount.setValue(selected.getValue());
        discount.setMinOrderAmount(selected.getMinOrderAmount());
        discount.setMaxDiscountAmount(selected.getMaxDiscountAmount() != null ? selected.getMaxDiscountAmount().doubleValue() : null);
        discount.setStartDate(selected.getStartDate());
        discount.setEndDate(selected.getEndDate());
        System.out.println("[TestDataHelper] Chọn random discount: " + discount.getCode() + " (ID: " + discount.getDiscountId() + ")");
        return discount;
    }
    
    /**
     * Lấy discount cụ thể theo code.
     * 
     * @param code Discount code
     * @return Discount object hoặc null nếu không tìm thấy
     */
    public static Discount getDiscountByCode(String code) {
        Discount discount = discountDB.validateAndGetDiscount(code);
        if (discount != null) {
            System.out.println("[TestDataHelper] Tìm thấy discount code: " + code);
        } else {
            System.out.println("[TestDataHelper] Không tìm thấy discount với code: " + code);
        }
        return discount;
    }
    
    /**
     * Lấy discounts của user cụ thể.
     * 
     * @param userId ID của user
     * @return List of Discounts
     */
    public static List<Discount> getUserDiscounts(int userId) {
        // Sử dụng listAssignedDiscountsForUser và convert sang Discount
        List<Model.UserDiscountAssign> userDiscounts = discountDB.listAssignedDiscountsForUser(userId);
        List<Discount> discounts = new ArrayList<>();
        if (userDiscounts != null) {
            for (Model.UserDiscountAssign uda : userDiscounts) {
                Discount discount = new Discount();
                discount.setDiscountId(uda.getDiscountId());
                discount.setCode(uda.getCode());
                discount.setName(uda.getName());
                discount.setType(uda.getType());
                discount.setValue(uda.getValue());
                discount.setMinOrderAmount(uda.getMinOrderAmount());
                discount.setMaxDiscountAmount(uda.getMaxDiscountAmount() != null ? uda.getMaxDiscountAmount().doubleValue() : null);
                discount.setStartDate(uda.getStartDate());
                discount.setEndDate(uda.getEndDate());
                discounts.add(discount);
            }
        }
        System.out.println("[TestDataHelper] Tìm thấy " + discounts.size() + " discounts cho user ID: " + userId);
        return discounts;
    }
    
    // ========== DEBUG METHODS ==========
    
    /**
     * In thông tin tất cả products (để debug).
     */
    public static void printAllProducts() {
        List<Product> products = getAllProducts();
        System.out.println("[TestDataHelper] Tổng số products trong DB: " + (products != null ? products.size() : 0));
        if (products != null && !products.isEmpty()) {
            products.forEach(p -> 
                System.out.println("  - " + p.getName() + " (ID: " + p.getProductId() + ", Stock: " + p.getStock() + ", Price: " + p.getPrice() + ")")
            );
        }
    }
    
    /**
     * In thông tin products còn hàng (để debug).
     */
    public static void printProductsInStock() {
        List<Product> products = productDB.getAllProducts();
        if (products != null) {
            List<Product> inStock = products.stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
            System.out.println("[TestDataHelper] Products còn hàng: " + inStock.size());
            inStock.forEach(p -> 
                System.out.println("  - " + p.getName() + " (ID: " + p.getProductId() + ", Stock: " + p.getStock() + ")")
            );
        }
    }
}

