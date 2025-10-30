package Controller;

import DAO.UserDB;
import Model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet(name = "AvatarUploadServlet", urlPatterns = {"/account/avatar"})
@MultipartConfig
public class AvatarUploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "C:\\CosmeticShop\\uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        user currentUser = (user) session.getAttribute("user");

        // Optional: update display name
        String newName = request.getParameter("username");
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(currentUser.getUsername())) {
            UserDB userDB = new UserDB();
            if (userDB.updateFullName(currentUser.getUser_id(), newName.trim())) {
                currentUser.setUsername(newName.trim());
                session.setAttribute("user", currentUser);
            }
        }

        Part avatarPart;
        try {
            avatarPart = request.getPart("avatar");
        } catch (Exception e) {
            avatarPart = null;
        }

        boolean hasAvatar = avatarPart != null && avatarPart.getSize() > 0;
        if (!hasAvatar) {
            response.sendRedirect(request.getContextPath() + "/View/account-management.jsp?msg=profile_updated");
            return;
        }

        String submittedFileName = Paths.get(avatarPart.getSubmittedFileName()).getFileName().toString();
        String ext = "";
        int dot = submittedFileName.lastIndexOf('.');
        if (dot >= 0) {
            ext = submittedFileName.substring(dot);
        }
        String newFileName = "avatar_" + currentUser.getUser_id() + "_" + UUID.randomUUID() + ext;

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, newFileName);
        try {
            avatarPart.write(dest.getAbsolutePath());
        } catch (IOException ex) {
            response.sendRedirect(request.getContextPath() + "/View/account-management.jsp?error=write_failed");
            return;
        }

        String relativePath = "/uploads/" + newFileName;

        UserDB userDB = new UserDB();
        boolean updated = userDB.updateAvatarUrl(currentUser.getUser_id(), relativePath);
        if (updated) {
            currentUser.setAvatarUrl(relativePath);
            session.setAttribute("user", currentUser);
            response.sendRedirect(request.getContextPath() + "/View/account-management.jsp?msg=avatar_updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/View/account-management.jsp?error=update_failed");
        }
    }
}


