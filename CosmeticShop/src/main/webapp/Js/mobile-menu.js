// Mobile Menu Toggle JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Get elements
    const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');
    const mobileNav = document.querySelector('.mobile-nav');
    const mobileNavOverlay = document.querySelector('.mobile-nav-overlay');
    const mobileNavClose = document.querySelector('.mobile-nav-close');
    const body = document.body;
    
    // Toggle mobile menu
    function toggleMobileMenu() {
        const isOpen = mobileNav.classList.contains('active');
        
        if (isOpen) {
            closeMobileMenu();
        } else {
            openMobileMenu();
        }
    }
    
    // Open mobile menu
    function openMobileMenu() {
        if (!mobileNav || !mobileNavOverlay || !mobileMenuToggle) return;
        
        mobileNav.classList.add('active');
        mobileNavOverlay.classList.add('active');
        mobileMenuToggle.classList.add('active');
        body.classList.add('mobile-menu-open');
    }
    
    // Close mobile menu
    function closeMobileMenu() {
        if (!mobileNav || !mobileNavOverlay || !mobileMenuToggle) return;
        
        mobileNav.classList.remove('active');
        mobileNavOverlay.classList.remove('active');
        mobileMenuToggle.classList.remove('active');
        body.classList.remove('mobile-menu-open');
    }
    
    // Event listeners
    if (mobileMenuToggle) {
        mobileMenuToggle.addEventListener('click', toggleMobileMenu);
    }
    
    if (mobileNavClose) {
        mobileNavClose.addEventListener('click', closeMobileMenu);
    }
    
    if (mobileNavOverlay) {
        mobileNavOverlay.addEventListener('click', closeMobileMenu);
    }
    
    // Close menu when clicking on a link
    const mobileNavLinks = document.querySelectorAll('.mobile-nav-list a');
    mobileNavLinks.forEach(link => {
        link.addEventListener('click', function() {
            closeMobileMenu();
        });
    });
    
    // Handle mobile search
    const mobileSearchForm = document.querySelector('.mobile-nav-search');
    if (mobileSearchForm) {
        mobileSearchForm.addEventListener('submit', function(e) {
            const input = this.querySelector('input');
            if (!input || !input.value.trim()) {
                e.preventDefault();
                return;
            }
            closeMobileMenu();
        });
    }
    
    // Handle window resize
    window.addEventListener('resize', function() {
        if (window.innerWidth > 768) {
            closeMobileMenu();
        }
    });
    
    // Ensure mobile elements exist if not created by JSP
    function ensureMobileElements() {
        // Create mobile menu toggle if not exists
        if (!document.querySelector('.mobile-menu-toggle')) {
            const toggle = document.createElement('button');
            toggle.className = 'mobile-menu-toggle';
            toggle.setAttribute('aria-label', 'Toggle mobile menu');
            toggle.innerHTML = '<span></span><span></span><span></span>';
            document.body.appendChild(toggle);
            toggle.addEventListener('click', toggleMobileMenu);
        }
        
        // Create mobile nav overlay if not exists
        if (!document.querySelector('.mobile-nav-overlay')) {
            const overlay = document.createElement('div');
            overlay.className = 'mobile-nav-overlay';
            document.body.appendChild(overlay);
            overlay.addEventListener('click', closeMobileMenu);
        }
        
        // Create mobile nav if not exists
        if (!document.querySelector('.mobile-nav')) {
            const mobileNavHTML = createMobileNavHTML();
            const mobileNavEl = document.createElement('div');
            mobileNavEl.className = 'mobile-nav';
            mobileNavEl.innerHTML = mobileNavHTML;
            document.body.appendChild(mobileNavEl);
            
            // Add close button event
            const closeBtn = mobileNavEl.querySelector('.mobile-nav-close');
            if (closeBtn) {
                closeBtn.addEventListener('click', closeMobileMenu);
            }
            
            // Re-bind link events
            const links = mobileNavEl.querySelectorAll('.mobile-nav-list a');
            links.forEach(link => {
                link.addEventListener('click', function() {
                    closeMobileMenu();
                });
            });
        }
    }
    
    function createMobileNavHTML() {
        // Get current user info from page
        const accountMenu = document.querySelector('.account-menu');
        const isLoggedIn = accountMenu && accountMenu.querySelector('.account-dropdown p.welcome-text');
        const userName = isLoggedIn ? accountMenu.querySelector('.account-dropdown p.welcome-text').textContent.replace('Xin chào, ', '') : '';
        const contextPath = window.APP_CTX || '';
        
        let navHTML = `
            <div class="mobile-nav-header">
                <img src="${contextPath}/Image/logo.png" alt="Logo">
                <button class="mobile-nav-close" aria-label="Close menu">&times;</button>
            </div>
            <div class="mobile-nav-content">
                <form class="mobile-nav-search" method="get" action="${contextPath}/search">
                    <input type="text" name="q" placeholder="Tìm kiếm sản phẩm...">
                    <button type="submit"><i class="fas fa-search"></i></button>
                </form>
                
                <ul class="mobile-nav-list">
                    <li><a href="${contextPath}/home">Trang chủ</a></li>
                    <li><a href="${contextPath}/products">Tất cả sản phẩm</a></li>
                    <li><a href="${contextPath}/about">Giới thiệu</a></li>
                    <li><a href="${contextPath}/contact">Liên hệ</a></li>
        `;
        
        if (isLoggedIn) {
            navHTML += `
                    <div class="mobile-nav-divider">Tài khoản</div>
                    <li><a href="${contextPath}/profile">Hồ sơ của tôi</a></li>
                    <li><a href="${contextPath}/my-orders">Đơn hàng</a></li>
                    <li><a href="${contextPath}/wishlist">Yêu thích</a></li>
                    <li><a href="${contextPath}/shipping-address">Địa chỉ giao hàng</a></li>
                    <li><a href="${contextPath}/logout" class="mobile-logout">Đăng xuất</a></li>
            `;
        } else {
            navHTML += `
                    <div class="mobile-nav-divider">Tài khoản</div>
                    <li><a href="${contextPath}/login">Đăng nhập</a></li>
                    <li><a href="${contextPath}/register">Đăng ký</a></li>
            `;
        }
        
        navHTML += `
                </ul>
            </div>
        `;
        
        return navHTML;
    }
    
    // Initialize mobile elements on page load
    setTimeout(ensureMobileElements, 100);
});
