document.addEventListener('DOMContentLoaded', function () {
    // ========================================
    // ACCOUNT DROPDOWN MENU
    // ========================================
    var accountMenu = document.querySelector('.account-menu');
    if (accountMenu) {
        var dropdown = accountMenu.querySelector('.account-dropdown');
        if (dropdown) {
            function isDropdownVisible() {
                // dùng getComputedStyle để check chính xác hơn
                return window.getComputedStyle(dropdown).display === 'block';
            }

            function showDropdown() {
                dropdown.style.display = 'block';
            }

            function hideDropdown() {
                dropdown.style.display = 'none';
            }

            function toggleDropdown() {
                if (isDropdownVisible()) {
                    hideDropdown();
                } else {
                    showDropdown();
                }
            }

            // Toggle khi click vào accountMenu (trừ khi click bên trong dropdown)
            accountMenu.addEventListener('click', function (e) {
                var clickedInsideDropdown = dropdown.contains(e.target);
                if (clickedInsideDropdown) return; // cho link bên trong dropdown hoạt động bình thường

                e.stopPropagation(); // chặn click lan ra ngoài document
                toggleDropdown();
            });

            // Đóng khi click ra ngoài accountMenu
            document.addEventListener('click', function (e) {
                if (!accountMenu.contains(e.target)) {
                    hideDropdown();
                }
            });
        }
    }

    // ========================================
    // MOBILE MENU FUNCTIONALITY
    // ========================================
    var mobileMenuToggle = document.getElementById('mobileMenuToggle');
    var mobileNav = document.getElementById('mobileNav');
    var mobileMenuOverlay = document.getElementById('mobileMenuOverlay');
    var body = document.body;

    // Add scroll effect to mobile menu toggle
    if (mobileMenuToggle) {
        window.addEventListener('scroll', function() {
            if (window.scrollY > 50) {
                mobileMenuToggle.classList.add('scrolled');
            } else {
                mobileMenuToggle.classList.remove('scrolled');
            }
        }, { passive: true });
    }

    if (mobileMenuToggle && mobileNav && mobileMenuOverlay) {
        // Mark as initialized to prevent double initialization
        mobileMenuToggle.setAttribute('data-initialized', 'true');
        
        // Function to open mobile menu
        function openMobileMenu() {
            mobileMenuToggle.classList.add('active');
            mobileNav.classList.add('active');
            mobileMenuOverlay.classList.add('active');
            body.classList.add('mobile-menu-open');
        }

        // Function to close mobile menu
        function closeMobileMenu() {
            mobileMenuToggle.classList.remove('active');
            mobileNav.classList.remove('active');
            mobileMenuOverlay.classList.remove('active');
            body.classList.remove('mobile-menu-open');
        }

        // Toggle menu on hamburger button click
        mobileMenuToggle.addEventListener('click', function(e) {
            e.stopPropagation();
            if (mobileNav.classList.contains('active')) {
                closeMobileMenu();
            } else {
                openMobileMenu();
            }
        });

        // Close menu when clicking overlay
        mobileMenuOverlay.addEventListener('click', function() {
            closeMobileMenu();
        });

        // Close menu when clicking a link inside the menu
        var mobileNavLinks = mobileNav.querySelectorAll('.mobile-nav-links a');
        mobileNavLinks.forEach(function(link) {
            link.addEventListener('click', function() {
                closeMobileMenu();
            });
        });

        // Close menu on window resize if it gets too large
        var resizeTimer;
        window.addEventListener('resize', function() {
            clearTimeout(resizeTimer);
            resizeTimer = setTimeout(function() {
                if (window.innerWidth > 767 && mobileNav.classList.contains('active')) {
                    closeMobileMenu();
                }
            }, 250);
        });

        // Prevent body scrolling when menu is open
        mobileNav.addEventListener('touchmove', function(e) {
            e.stopPropagation();
        }, { passive: true });
    }

    // ========================================
    // SMOOTH SCROLL FOR ANCHOR LINKS
    // ========================================
    var anchorLinks = document.querySelectorAll('a[href^="#"]');
    anchorLinks.forEach(function(link) {
        link.addEventListener('click', function(e) {
            var href = this.getAttribute('href');
            if (href !== '#' && href !== '') {
                var target = document.querySelector(href);
                if (target) {
                    e.preventDefault();
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            }
        });
    });

    // ========================================
    // LAZY LOADING IMAGES (OPTIONAL)
    // ========================================
    if ('IntersectionObserver' in window) {
        var lazyImages = document.querySelectorAll('img[data-src]');
        var imageObserver = new IntersectionObserver(function(entries, observer) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    var img = entry.target;
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                    imageObserver.unobserve(img);
                }
            });
        });

        lazyImages.forEach(function(img) {
            imageObserver.observe(img);
        });
    }

    // ========================================
    // ADD TOUCH EVENTS FOR BETTER MOBILE UX
    // ========================================
    var touchStartX = 0;
    var touchEndX = 0;

    document.addEventListener('touchstart', function(e) {
        touchStartX = e.changedTouches[0].screenX;
    }, { passive: true });

    document.addEventListener('touchend', function(e) {
        touchEndX = e.changedTouches[0].screenX;
        handleSwipe();
    }, { passive: true });

    function handleSwipe() {
        // Swipe from left to right - Open menu
        if (touchEndX > touchStartX + 50 && touchStartX < 50) {
            if (mobileNav && !mobileNav.classList.contains('active') && window.innerWidth <= 767) {
                openMobileMenu();
            }
        }
        // Swipe from right to left - Close menu
        if (touchStartX > touchEndX + 50 && mobileNav && mobileNav.classList.contains('active')) {
            closeMobileMenu();
        }
    }

    // ========================================
    // PREVENT ZOOM ON DOUBLE TAP (iOS)
    // ========================================
    var lastTouchEnd = 0;
    document.addEventListener('touchend', function(e) {
        var now = (new Date()).getTime();
        if (now - lastTouchEnd <= 300) {
            e.preventDefault();
        }
        lastTouchEnd = now;
    }, false);
});
