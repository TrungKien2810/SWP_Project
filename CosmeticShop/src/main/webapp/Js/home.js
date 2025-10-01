document.addEventListener('DOMContentLoaded', function () {
    var accountMenu = document.querySelector('.account-menu');
    if (!accountMenu) return;

    var dropdown = accountMenu.querySelector('.account-dropdown');
    if (!dropdown) return;

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
});
