document.addEventListener('DOMContentLoaded', function () {
    // Account menu dropdown
    var accountMenu = document.querySelector('.account-menu');
    if (accountMenu) {
        var dropdown = accountMenu.querySelector('.account-dropdown');
        if (dropdown) {
            function isDropdownVisible() {
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
                if (clickedInsideDropdown) return;

                e.stopPropagation();
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

    // Admin dropdown menu - sử dụng Bootstrap dropdown
    var adminDropdown = document.querySelector('#adminDropdown');
    if (adminDropdown) {
        // Bootstrap sẽ tự động xử lý dropdown
        // Chỉ cần đảm bảo dropdown hoạt động đúng
        console.log('Admin dropdown initialized');
    }
});
