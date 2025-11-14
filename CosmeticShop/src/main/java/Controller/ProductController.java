package Controller;

import DAO.ProductDB;
import Model.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;

@WebServlet(name = "ProductController", urlPatterns = {"/products"})
@MultipartConfig
public class ProductController extends HttpServlet {
    private ProductDB productDB;

    public void init() {
        // defer DB init to first use to speed up non-DB actions like showing new form
        // productDB = new ProductDB();
    }

    private ProductDB db() {
        if (productDB == null) {
            productDB = new ProductDB();
        }
        return productDB;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Mặc định là hiển thị danh sách
        }

        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteProduct(request, response);
                break;
            case "manage":
                showManagePage(request, response);
                break;
            default:
                listProducts(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
          String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("products");
            return;
        }
        
        switch (action) {
            case "insert":
                insertProduct(request, response);
                break;
            case "update":
                updateProduct(request, response);
                break;
            default:
                 response.sendRedirect("products");
                break;
        }
    }

      private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy tham số tìm kiếm và lọc
        String searchTerm = request.getParameter("search");
        String categoryName = request.getParameter("category");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String fixedPriceRange = request.getParameter("fixedPriceRange");
        String sortBy = request.getParameter("sortBy");
        
        // Paging params
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        int page = 1;
        int pageSize = 20;
        if (pageStr != null && !pageStr.isEmpty()) {
            try { page = Integer.parseInt(pageStr); if (page < 1) page = 1; } catch (NumberFormatException ignored) {}
        }
        if (pageSizeStr != null && !pageSizeStr.isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeStr);
                if (pageSize < 1) pageSize = 20;
                if (pageSize > 100) pageSize = 100;
            } catch (NumberFormatException ignored) {}
        }
        
        List<Product> productList;
        int totalProducts;
        
        // Xử lý tìm kiếm và lọc
        if (searchTerm != null || categoryName != null || minPriceStr != null || maxPriceStr != null || fixedPriceRange != null || sortBy != null) {
            double minPrice = (minPriceStr != null && !minPriceStr.isEmpty()) ? Double.parseDouble(minPriceStr) : -1;
            double maxPrice = (maxPriceStr != null && !maxPriceStr.isEmpty()) ? Double.parseDouble(maxPriceStr) : -1;
            
            productList = db().searchAndFilterProductsWithPaging(searchTerm, categoryName, minPrice, maxPrice, fixedPriceRange, sortBy, page, pageSize);
            totalProducts = db().getFilteredProductsCount(searchTerm, categoryName, minPrice, maxPrice, fixedPriceRange);
        } else {
            productList = db().getAllProductsWithPaging(page, pageSize);
            totalProducts = db().getTotalProductsCount();
        }
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        
        // Lấy danh sách categories để hiển thị trong dropdown
        List<String> categories = db().getAllCategories();
        
        // Lấy danh sách product IDs trong wishlist của user (nếu đã đăng nhập)
        java.util.Set<Integer> wishlistProductIds = new java.util.HashSet<>();
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            Model.user user = (Model.user) session.getAttribute("user");
            DAO.WishlistDB wishlistDB = new DAO.WishlistDB();
            java.util.List<Model.Wishlist> wishlistItems = wishlistDB.getWishlistByUserId(user.getUser_id());
            for (Model.Wishlist item : wishlistItems) {
                wishlistProductIds.add(item.getProductId());
            }
        }
        
        request.setAttribute("productList", productList);
        request.setAttribute("categories", categories);
        request.setAttribute("wishlistProductIds", wishlistProductIds);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("selectedCategory", categoryName);
        request.setAttribute("minPrice", minPriceStr);
        request.setAttribute("maxPrice", maxPriceStr);
        request.setAttribute("selectedFixedPriceRange", fixedPriceRange);
        request.setAttribute("selectedSortBy", sortBy);
        // Paging attributes
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/View/collection.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Load Category objects để có categoryId
        DAO.CategoryDB categoryDB = new DAO.CategoryDB();
        List<Model.Category> categories = categoryDB.listAll();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/View/product-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Product existingProduct = db().getProductById(id);
        // Load Category objects để có categoryId
        DAO.CategoryDB categoryDB = new DAO.CategoryDB();
        List<Model.Category> categories = categoryDB.listAll();
        // Lấy categoryIds hiện tại của product
        List<Integer> currentCategoryIds = existingProduct.getCategoryIds();
        
        // Lấy danh sách ảnh phụ hiện có
        List<ProductDB.ImageInfo> existingImages = db().getProductImagesWithDetails(id);
        
        // Phân tích mô tả thành các mục
        List<DescriptionSection> productDescriptionSections = parseDescriptionSections(existingProduct.getDescription());
        
        request.setAttribute("product", existingProduct);
        request.setAttribute("categories", categories);
        request.setAttribute("currentCategoryIds", currentCategoryIds);
        request.setAttribute("existingImages", existingImages);
        request.setAttribute("productDescriptionSections", productDescriptionSections);
        request.getRequestDispatcher("/View/product-form.jsp").forward(request, response);
    }

    private void showManagePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> productList = db().getAllProducts();
        List<String> categories = db().getAllCategories();
        request.setAttribute("productList", productList);
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/View/product-manager.jsp").forward(request, response);
    }
    
    private void insertProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        
        // Xử lý mô tả nhiều mục
        String description = buildDescriptionFromSections(request);
        
        // Resolve categories: có thể nhận nhiều categoryIds hoặc categoryNames
        List<Integer> categoryIds = new ArrayList<>();
        String[] categoryIdParams = request.getParameterValues("categoryIds");
        if (categoryIdParams != null && categoryIdParams.length > 0) {
            // Nhận từ multi-select categoryIds
            for (String catIdStr : categoryIdParams) {
                try {
                    int catId = Integer.parseInt(catIdStr);
                    if (catId > 0) categoryIds.add(catId);
                } catch (NumberFormatException ignored) {}
            }
        } else {
            // Fallback: nhận từ categoryName hoặc categoryId (backward compatibility)
            String categoryName = request.getParameter("categoryName");
            if (categoryName != null && !categoryName.isEmpty()) {
                Integer categoryIdObj = db().getCategoryIdByName(categoryName);
                if (categoryIdObj != null) {
                    categoryIds.add(categoryIdObj);
                }
            }
            if (categoryIds.isEmpty()) {
                String legacyCat = request.getParameter("categoryId");
                if (legacyCat != null && !legacyCat.isEmpty()) {
                    try {
                        int catId = Integer.parseInt(legacyCat);
                        if (catId > 0) categoryIds.add(catId);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        String imageUrl = handleImageUpload(request, null);

        Product newProduct = new Product(0, name, price, stock, description, imageUrl, 0);
        newProduct.setCategoryIds(categoryIds);
        boolean success = db().addProduct(newProduct);
        
        if (!success) {
            response.sendRedirect("products?action=manage&error=add_failed");
            return;
        }
        
        // Lấy ID của sản phẩm vừa tạo
        int productId = db().getLastInsertedProductId();
        
        // Xử lý ảnh phụ
        handleAdditionalImages(request, productId);
        
        response.sendRedirect(request.getContextPath() + "/admin?action=products");
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        
        // Xử lý mô tả nhiều mục
        String description = buildDescriptionFromSections(request);
        
        // Resolve categories: có thể nhận nhiều categoryIds hoặc categoryNames
        List<Integer> categoryIds = new ArrayList<>();
        String[] categoryIdParams = request.getParameterValues("categoryIds");
        if (categoryIdParams != null && categoryIdParams.length > 0) {
            // Nhận từ multi-select categoryIds
            for (String catIdStr : categoryIdParams) {
                try {
                    int catId = Integer.parseInt(catIdStr);
                    if (catId > 0) categoryIds.add(catId);
                } catch (NumberFormatException ignored) {}
            }
        } else {
            // Fallback: nhận từ categoryName hoặc categoryId (backward compatibility)
            String categoryName = request.getParameter("categoryName");
            if (categoryName != null && !categoryName.isEmpty()) {
                Integer categoryIdObj = db().getCategoryIdByName(categoryName);
                if (categoryIdObj != null) {
                    categoryIds.add(categoryIdObj);
                }
            }
            if (categoryIds.isEmpty()) {
                String currentCat = request.getParameter("currentCategoryId");
                if (currentCat != null && !currentCat.isEmpty()) {
                    try {
                        int catId = Integer.parseInt(currentCat);
                        if (catId > 0) categoryIds.add(catId);
                    } catch (NumberFormatException ignored) {}
                } else {
                    String legacyCat = request.getParameter("categoryId");
                    if (legacyCat != null && !legacyCat.isEmpty()) {
                        try {
                            int catId = Integer.parseInt(legacyCat);
                            if (catId > 0) categoryIds.add(catId);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }

        String currentImageUrl = request.getParameter("currentImageUrl");
        String imageUrl = handleImageUpload(request, currentImageUrl);

        Product product = new Product(id, name, price, stock, description, imageUrl, 0);
        product.setCategoryIds(categoryIds);
        db().updateProduct(product);
        
        // Xử lý ảnh phụ - chỉ xóa ảnh được đánh dấu xóa và thêm ảnh mới
        handleAdditionalImagesUpdate(request, id);
        
        response.sendRedirect(request.getContextPath() + "/admin?action=products");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        db().deleteProduct(id);
        response.sendRedirect(request.getContextPath() + "/admin?action=products");
    }

    private String handleImageUpload(HttpServletRequest request, String fallbackUrl) throws IOException, ServletException {
        return handleImageUpload(request, fallbackUrl, "main");
    }
    
    private String handleImageUpload(HttpServletRequest request, String fallbackUrl, String prefix) throws IOException, ServletException {
        Part imagePart = null;
        try {
            imagePart = request.getPart("imageFile");
        } catch (IllegalStateException | ServletException e) {
            imagePart = null;
        }

        if (imagePart == null || imagePart.getSize() == 0) {
            return fallbackUrl != null ? fallbackUrl : "";
        }

        String submittedFileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
        String ext = "";
        int dot = submittedFileName.lastIndexOf('.');
        if (dot >= 0) {
            ext = submittedFileName.substring(dot);
        }
        String newFileName = prefix + "_" + UUID.randomUUID().toString() + ext;

        // Lưu ảnh vào folder cố định trong ổ C
        String uploadPath = "C:\\CosmeticShop\\uploads";
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, newFileName);
        try {
            imagePart.write(dest.getAbsolutePath());
        } catch (IOException ex) {
            throw ex;
        }

        // Trả về đường dẫn tương đối để hiển thị ảnh
        String contextRelativeUrl = "/uploads/" + newFileName;
        return contextRelativeUrl.replace("\\", "/");
    }
    
    private void handleAdditionalImages(HttpServletRequest request, int productId) throws IOException, ServletException {
        List<Part> additionalImageParts = new ArrayList<>();
        try {
            for (Part part : request.getParts()) {
                if (part.getName() != null && part.getName().startsWith("additionalImageFiles")) {
                    additionalImageParts.add(part);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing additional image files: " + e.getMessage());
            return;
        }
        
        // Upload ảnh phụ và lưu vào database
        for (int i = 0; i < additionalImageParts.size(); i++) {
            Part imagePart = additionalImageParts.get(i);
            if (imagePart != null && imagePart.getSize() > 0) {
                String imageUrl = uploadAdditionalImage(imagePart, i + 1);
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    db().addProductImage(productId, imageUrl, i + 1);
                }
            }
        }
    }
    
    private void handleAdditionalImagesUpdate(HttpServletRequest request, int productId) throws IOException, ServletException {
        // Lấy danh sách ảnh phụ hiện có
        List<ProductDB.ImageInfo> existingImages = db().getProductImagesWithDetails(productId);
        
        // Xử lý xóa ảnh được đánh dấu xóa
        String[] deleteImageIds = request.getParameterValues("deleteImageIds");
        if (deleteImageIds != null) {
            for (String imageIdStr : deleteImageIds) {
                try {
                    int imageId = Integer.parseInt(imageIdStr);
                    db().deleteProductImage(imageId);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid image ID for deletion: " + imageIdStr);
                }
            }
        }
        
        // Xử lý ảnh phụ mới được upload
        List<Part> additionalImageParts = new ArrayList<>();
        try {
            for (Part part : request.getParts()) {
                if (part.getName() != null && part.getName().startsWith("additionalImageFiles")) {
                    additionalImageParts.add(part);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing additional image files: " + e.getMessage());
            return;
        }
        
        // Upload ảnh phụ mới và lưu vào database
        int nextOrder = existingImages.size() + 1; // Thứ tự tiếp theo
        for (Part imagePart : additionalImageParts) {
            if (imagePart != null && imagePart.getSize() > 0) {
                String imageUrl = uploadAdditionalImage(imagePart, nextOrder);
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    db().addProductImage(productId, imageUrl, nextOrder);
                    nextOrder++;
                }
            }
        }
    }
    
    private String uploadAdditionalImage(Part imagePart, int order) throws IOException {
        String submittedFileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
        String ext = "";
        int dot = submittedFileName.lastIndexOf('.');
        if (dot >= 0) {
            ext = submittedFileName.substring(dot);
        }
        String newFileName = "additional_" + order + "_" + UUID.randomUUID().toString() + ext;

        // Lưu ảnh vào folder cố định trong ổ C
        String uploadPath = "C:\\CosmeticShop\\uploads";
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, newFileName);
        try {
            imagePart.write(dest.getAbsolutePath());
        } catch (IOException ex) {
            throw ex;
        }

        // Trả về đường dẫn tương đối để hiển thị ảnh
        String contextRelativeUrl = "/uploads/" + newFileName;
        return contextRelativeUrl.replace("\\", "/");
    }
    
    /**
     * Xây dựng mô tả từ các mục mô tả được gửi từ form
     */
    private String buildDescriptionFromSections(HttpServletRequest request) {
        String[] titles = request.getParameterValues("descriptionSectionTitles[]");
        String[] contents = request.getParameterValues("descriptionSectionContents[]");
        
        if (titles == null || contents == null || titles.length == 0 || contents.length == 0) {
            return "";
        }
        
        StringBuilder description = new StringBuilder();
        int maxLength = Math.min(titles.length, contents.length);
        
        for (int i = 0; i < maxLength; i++) {
            String title = titles[i] != null ? titles[i].trim() : "";
            String content = contents[i] != null ? contents[i].trim() : "";
            
            // Chỉ thêm mục nếu có cả tên và nội dung
            if (!title.isEmpty() && !content.isEmpty()) {
                if (description.length() > 0) {
                    description.append("\n------------------\n");
                }
                description.append(title).append("\n").append(content);
            }
        }
        
        return description.toString();
    }
    
    /**
     * Phân tích mô tả thành các mục để hiển thị trong form chỉnh sửa
     */
    private List<DescriptionSection> parseDescriptionSections(String description) {
        List<DescriptionSection> sections = new ArrayList<>();
        
        if (description == null || description.trim().isEmpty()) {
            return sections;
        }
        
        String[] parts = description.split("------------------");
        
        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;
            
            String[] lines = part.split("\n", 2);
            if (lines.length >= 2) {
                String title = lines[0].trim();
                String content = lines[1].trim();
                if (!title.isEmpty() && !content.isEmpty()) {
                    sections.add(new DescriptionSection(title, content));
                }
            }
        }
        
        return sections;
    }
    
    /**
     * Lớp nội bộ để lưu trữ thông tin mục mô tả
     */
    public static class DescriptionSection {
        private String title;
        private String content;
        
        public DescriptionSection(String title, String content) {
            this.title = title;
            this.content = content;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
}
//test pull rq