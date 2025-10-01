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
        List<Product> productList = db().getAllProducts();
        request.setAttribute("productList", productList);
        request.getRequestDispatcher("/View/bosuutap.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String> categories = db().getAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/View/product-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Product existingProduct = db().getProductById(id);
        List<String> categories = db().getAllCategories();
        String currentCategoryName = db().getCategoryNameById(existingProduct.getCategoryId());
        request.setAttribute("product", existingProduct);
        request.setAttribute("categories", categories);
        request.setAttribute("currentCategoryName", currentCategoryName);
        request.getRequestDispatcher("/View/product-form.jsp").forward(request, response);
    }

    
    private void insertProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        String description = request.getParameter("description");
        // Resolve category by name, fallback to numeric legacy field or NULL
        String categoryName = request.getParameter("categoryName");
        Integer categoryIdObj = db().getCategoryIdByName(categoryName);
        int categoryId = 0;
        if (categoryIdObj != null) {
            categoryId = categoryIdObj;
        } else {
            String legacyCat = request.getParameter("categoryId");
            if (legacyCat != null && !legacyCat.isEmpty()) {
                try { categoryId = Integer.parseInt(legacyCat); } catch (NumberFormatException ignored) {}
            }
        }

        String imageUrl = handleImageUpload(request, null);

        Product newProduct = new Product(0, name, price, stock, description, imageUrl, categoryId);
        db().addProduct(newProduct);
        response.sendRedirect("products");
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        String description = request.getParameter("description");
        // Resolve category by name, fallback to current or legacy numeric
        String categoryName = request.getParameter("categoryName");
        Integer categoryIdObj = db().getCategoryIdByName(categoryName);
        int categoryId = 0;
        if (categoryIdObj != null) {
            categoryId = categoryIdObj;
        } else {
            String currentCat = request.getParameter("currentCategoryId");
            if (currentCat != null && !currentCat.isEmpty()) {
                try { categoryId = Integer.parseInt(currentCat); } catch (NumberFormatException ignored) {}
            } else {
                String legacyCat = request.getParameter("categoryId");
                if (legacyCat != null && !legacyCat.isEmpty()) {
                    try { categoryId = Integer.parseInt(legacyCat); } catch (NumberFormatException ignored) {}
                }
            }
        }

        String currentImageUrl = request.getParameter("currentImageUrl");
        String imageUrl = handleImageUpload(request, currentImageUrl);

        Product product = new Product(id, name, price, stock, description, imageUrl, categoryId);
        db().updateProduct(product);
        response.sendRedirect("products");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        db().deleteProduct(id);
        response.sendRedirect("products");
    }

    private String handleImageUpload(HttpServletRequest request, String fallbackUrl) throws IOException, ServletException {
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
        String newFileName = UUID.randomUUID().toString() + ext;

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
}
//test pull rq