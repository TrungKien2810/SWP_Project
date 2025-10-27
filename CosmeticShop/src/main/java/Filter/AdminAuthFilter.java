package Filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/admin"})
public class AdminAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        Object userObj = (session != null) ? session.getAttribute("user") : null;
        boolean isAdmin = false;
        if (userObj instanceof Model.user) {
            Model.user u = (Model.user) userObj;
            String role = u.getRole();
            isAdmin = (role != null && role.equalsIgnoreCase("ADMIN"));
        }

        if (userObj == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (!isAdmin) {
            resp.sendRedirect(req.getContextPath() + "/View/home.jsp?error=" + java.net.URLEncoder.encode("Không có quyền truy cập", java.nio.charset.StandardCharsets.UTF_8));
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

