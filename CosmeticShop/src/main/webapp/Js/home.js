document.addEventListener('DOMContentLoaded', function () {
    // Account Menu
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

    // Category Carousel - Tách riêng để luôn chạy
    var categoryCarousel = document.getElementById('categoryCarousel');
    var categoryTrack = document.getElementById('categoryTrack');
    var categoryPrevBtn = document.getElementById('categoryPrevBtn');
    var categoryNextBtn = document.getElementById('categoryNextBtn');

    if (categoryCarousel && categoryTrack && categoryPrevBtn && categoryNextBtn) {
        var scrollPosition = 0;
        var scrollAmount = 170; // Khoảng cách scroll mỗi lần (150px card + 20px gap)
        var maxScroll = 0;
        var cardWidth = 150; // width của mỗi card
        var gap = 20; // gap giữa các card
        var itemsPerRow = 0; // số items hiển thị trong 1 hàng

        function calculateItemsPerRow() {
            // Lấy width thực tế của container (không tính padding)
            var containerWidth = categoryCarousel.offsetWidth;
            var computedStyle = window.getComputedStyle(categoryCarousel);
            var paddingLeft = parseFloat(computedStyle.paddingLeft) || 0;
            var paddingRight = parseFloat(computedStyle.paddingRight) || 0;
            var actualWidth = containerWidth - paddingLeft - paddingRight;
            
            // Tính số items có thể hiển thị trong 1 hàng
            itemsPerRow = Math.floor(actualWidth / (cardWidth + gap));
            if (itemsPerRow < 1) itemsPerRow = 1;
            // Scroll amount = số items trong 1 hàng
            scrollAmount = itemsPerRow * (cardWidth + gap);
        }

        function updateScrollButtons() {
            calculateItemsPerRow();
            // Tính maxScroll với width thực tế
            var containerWidth = categoryCarousel.offsetWidth;
            var computedStyle = window.getComputedStyle(categoryCarousel);
            var paddingLeft = parseFloat(computedStyle.paddingLeft) || 0;
            var paddingRight = parseFloat(computedStyle.paddingRight) || 0;
            var actualWidth = containerWidth - paddingLeft - paddingRight;
            
            // Với grid layout, cần tính scrollWidth khác
            var trackWidth = categoryTrack.offsetWidth || categoryTrack.scrollWidth;
            maxScroll = Math.max(0, trackWidth - actualWidth);
        }

        function scrollCategories(direction) {
            updateScrollButtons(); // Cập nhật maxScroll trước khi scroll
            
            var newPosition = scrollPosition + (direction * scrollAmount);
            
            // Giới hạn scroll trong phạm vi hợp lệ
            if (newPosition > maxScroll) {
                newPosition = maxScroll; // Không quay vòng, dừng ở cuối
            } else if (newPosition < 0) {
                newPosition = 0; // Không quay vòng, dừng ở đầu
            }

            scrollPosition = newPosition;
            categoryTrack.style.transform = 'translateX(-' + scrollPosition + 'px)';
            
            // Cập nhật trạng thái nút (ẩn nút khi đã đến đầu/cuối)
            updateButtonStates();
        }
        
        function updateButtonStates() {
            // Hiển thị/ẩn nút dựa trên vị trí scroll
            if (maxScroll <= 0) {
                // Không cần scroll
                categoryPrevBtn.style.opacity = '0.3';
                categoryPrevBtn.style.pointerEvents = 'none';
                categoryNextBtn.style.opacity = '0.3';
                categoryNextBtn.style.pointerEvents = 'none';
            } else {
                // Ở đầu danh sách
                if (scrollPosition <= 0) {
                    categoryPrevBtn.style.opacity = '0.3';
                    categoryPrevBtn.style.pointerEvents = 'none';
                    categoryNextBtn.style.opacity = '1';
                    categoryNextBtn.style.pointerEvents = 'auto';
                }
                // Ở cuối danh sách
                else if (scrollPosition >= maxScroll) {
                    categoryPrevBtn.style.opacity = '1';
                    categoryPrevBtn.style.pointerEvents = 'auto';
                    categoryNextBtn.style.opacity = '0.3';
                    categoryNextBtn.style.pointerEvents = 'none';
                }
                // Ở giữa danh sách
                else {
                    categoryPrevBtn.style.opacity = '1';
                    categoryPrevBtn.style.pointerEvents = 'auto';
                    categoryNextBtn.style.opacity = '1';
                    categoryNextBtn.style.pointerEvents = 'auto';
                }
            }
        }

        // Gắn event listener với cả onclick và addEventListener để đảm bảo hoạt động
        function handlePrevClick(e) {
            if (e) {
                e.preventDefault();
                e.stopPropagation();
            }
            scrollCategories(-1); // Prev = scroll sang trái (giảm position)
            return false;
        }

        function handleNextClick(e) {
            if (e) {
                e.preventDefault();
                e.stopPropagation();
            }
            scrollCategories(1); // Next = scroll sang phải (tăng position)
            return false;
        }

        categoryPrevBtn.onclick = handlePrevClick;
        categoryPrevBtn.addEventListener('click', handlePrevClick);

        categoryNextBtn.onclick = handleNextClick;
        categoryNextBtn.addEventListener('click', handleNextClick);

        // Cập nhật khi resize
        window.addEventListener('resize', function() {
            updateScrollButtons();
            updateButtonStates();
        });

        // Khởi tạo sau khi DOM load xong
        setTimeout(function() {
            updateScrollButtons();
            updateButtonStates();
        }, 200);

        // Touch/swipe support cho mobile
        var touchStartX = 0;
        var touchEndX = 0;

        categoryCarousel.addEventListener('touchstart', function(e) {
            touchStartX = e.changedTouches[0].screenX;
        }, { passive: true });

        categoryCarousel.addEventListener('touchend', function(e) {
            touchEndX = e.changedTouches[0].screenX;
            handleSwipe();
        }, { passive: true });

        function handleSwipe() {
            var swipeThreshold = 50;
            var diff = touchStartX - touchEndX;

            if (Math.abs(diff) > swipeThreshold) {
                if (diff > 0) {
                    // Swipe left - scroll right
                    scrollCategories(1);
                } else {
                    // Swipe right - scroll left
                    scrollCategories(-1);
                }
            }
        }
    }

    // Product Carousels - Xử lý cho 3 section sản phẩm
    var productCarousels = [
        { carousel: 'promotionalCarousel', track: 'promotionalTrack', prev: 'promotionalPrevBtn', next: 'promotionalNextBtn' },
        { carousel: 'featuredCarousel', track: 'featuredTrack', prev: 'featuredPrevBtn', next: 'featuredNextBtn' },
        { carousel: 'bestSellingCarousel', track: 'bestSellingTrack', prev: 'bestSellingPrevBtn', next: 'bestSellingNextBtn' }
    ];

    productCarousels.forEach(function(carouselConfig) {
        var carouselEl = document.getElementById(carouselConfig.carousel);
        var trackEl = document.getElementById(carouselConfig.track);
        var prevBtn = document.getElementById(carouselConfig.prev);
        var nextBtn = document.getElementById(carouselConfig.next);

        if (carouselEl && trackEl && prevBtn && nextBtn) {
            var scrollPosition = 0;
            var scrollAmount = 0;
            var maxScroll = 0;
            var cardWidth = 0;
            var gap = 20;

            function calculateScrollAmount() {
                // Tính width của 1 product card
                var firstCard = trackEl.querySelector('.product-card');
                if (firstCard) {
                    var cardStyle = window.getComputedStyle(firstCard);
                    cardWidth = firstCard.offsetWidth + parseFloat(cardStyle.marginLeft || 0) + parseFloat(cardStyle.marginRight || 0);
                } else {
                    cardWidth = 220; // fallback
                }
                
                // Tính số sản phẩm hiển thị trong 1 hàng dựa trên kích thước màn hình
                var containerWidth = carouselEl.offsetWidth;
                var computedStyle = window.getComputedStyle(carouselEl);
                var paddingLeft = parseFloat(computedStyle.paddingLeft) || 0;
                var paddingRight = parseFloat(computedStyle.paddingRight) || 0;
                var actualWidth = containerWidth - paddingLeft - paddingRight;
                
                // Tính số sản phẩm có thể hiển thị trong 1 hàng
                var itemsPerRow = Math.floor(actualWidth / (cardWidth + gap));
                if (itemsPerRow < 1) itemsPerRow = 1;
                
                // Scroll 1 hàng mỗi lần
                scrollAmount = itemsPerRow * (cardWidth + gap);
            }

            function updateMaxScroll() {
                calculateScrollAmount();
                var containerWidth = carouselEl.offsetWidth;
                var computedStyle = window.getComputedStyle(carouselEl);
                var paddingLeft = parseFloat(computedStyle.paddingLeft) || 0;
                var paddingRight = parseFloat(computedStyle.paddingRight) || 0;
                var actualWidth = containerWidth - paddingLeft - paddingRight;
                
                var trackWidth = trackEl.scrollWidth || trackEl.offsetWidth;
                maxScroll = Math.max(0, trackWidth - actualWidth);
            }

            function scrollProducts(direction) {
                updateMaxScroll();
                
                var newPosition = scrollPosition + (direction * scrollAmount);
                
                if (newPosition > maxScroll) {
                    newPosition = maxScroll;
                } else if (newPosition < 0) {
                    newPosition = 0;
                }

                scrollPosition = newPosition;
                trackEl.style.transform = 'translateX(-' + scrollPosition + 'px)';
                
                updateButtonStates();
            }
            
            function updateButtonStates() {
                if (maxScroll <= 0) {
                    prevBtn.style.opacity = '0.3';
                    prevBtn.style.pointerEvents = 'none';
                    nextBtn.style.opacity = '0.3';
                    nextBtn.style.pointerEvents = 'none';
                } else {
                    if (scrollPosition <= 0) {
                        prevBtn.style.opacity = '0.3';
                        prevBtn.style.pointerEvents = 'none';
                        nextBtn.style.opacity = '1';
                        nextBtn.style.pointerEvents = 'auto';
                    } else if (scrollPosition >= maxScroll) {
                        prevBtn.style.opacity = '1';
                        prevBtn.style.pointerEvents = 'auto';
                        nextBtn.style.opacity = '0.3';
                        nextBtn.style.pointerEvents = 'none';
                    } else {
                        prevBtn.style.opacity = '1';
                        prevBtn.style.pointerEvents = 'auto';
                        nextBtn.style.opacity = '1';
                        nextBtn.style.pointerEvents = 'auto';
                    }
                }
            }

            function handlePrevClick(e) {
                if (e) {
                    e.preventDefault();
                    e.stopPropagation();
                }
                scrollProducts(-1);
                return false;
            }

            function handleNextClick(e) {
                if (e) {
                    e.preventDefault();
                    e.stopPropagation();
                }
                scrollProducts(1);
                return false;
            }

            prevBtn.onclick = handlePrevClick;
            prevBtn.addEventListener('click', handlePrevClick);
            nextBtn.onclick = handleNextClick;
            nextBtn.addEventListener('click', handleNextClick);

            window.addEventListener('resize', function() {
                updateMaxScroll();
                updateButtonStates();
            });

            setTimeout(function() {
                updateMaxScroll();
                updateButtonStates();
            }, 200);

            // Touch/swipe support
            var touchStartX = 0;
            var touchEndX = 0;

            carouselEl.addEventListener('touchstart', function(e) {
                touchStartX = e.changedTouches[0].screenX;
            }, { passive: true });

            carouselEl.addEventListener('touchend', function(e) {
                touchEndX = e.changedTouches[0].screenX;
                var swipeThreshold = 50;
                var diff = touchStartX - touchEndX;

                if (Math.abs(diff) > swipeThreshold) {
                    if (diff > 0) {
                        scrollProducts(1);
                    } else {
                        scrollProducts(-1);
                    }
                }
            }, { passive: true });
        }
    });
});
