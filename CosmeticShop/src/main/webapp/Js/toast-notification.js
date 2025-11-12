/**
 * Toast Notification System
 * Hiển thị thông báo đẹp mắt với animation
 */

// Tạo container cho toast nếu chưa có
function ensureToastContainer() {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    return container;
}

/**
 * Hiển thị toast notification
 * @param {string} message - Nội dung thông báo
 * @param {string} type - Loại thông báo: 'success', 'error', 'warning', 'info'
 * @param {number} duration - Thời gian hiển thị (ms), mặc định 3000ms
 */
function showToast(message, type = 'success', duration = 3000) {
    const container = ensureToastContainer();
    
    // Icon theo loại thông báo
    const icons = {
        success: '<i class="fas fa-check-circle"></i>',
        error: '<i class="fas fa-times-circle"></i>',
        warning: '<i class="fas fa-exclamation-triangle"></i>',
        info: '<i class="fas fa-info-circle"></i>'
    };
    
    // Tiêu đề theo loại thông báo
    const titles = {
        success: 'Thành công',
        error: 'Lỗi',
        warning: 'Cảnh báo',
        info: 'Thông tin'
    };
    
    // Tạo toast element
    const toast = document.createElement('div');
    toast.className = `toast-notification ${type}`;
    toast.innerHTML = `
        <div class="toast-icon">
            ${icons[type] || icons.success}
        </div>
        <div class="toast-content">
            <p class="toast-title">${titles[type] || titles.success}</p>
            <p class="toast-message">${message}</p>
        </div>
        <button class="toast-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
        <div class="toast-progress"></div>
    `;
    
    // Thêm vào container
    container.appendChild(toast);
    
    // Tự động ẩn sau duration
    setTimeout(() => {
        toast.classList.add('hiding');
        setTimeout(() => {
            if (toast.parentElement) {
                toast.remove();
            }
        }, 300);
    }, duration);
}

/**
 * Hiển thị toast thành công
 */
function showSuccessToast(message, duration = 3000) {
    showToast(message, 'success', duration);
}

/**
 * Hiển thị toast lỗi
 */
function showErrorToast(message, duration = 4000) {
    showToast(message, 'error', duration);
}

/**
 * Hiển thị toast cảnh báo
 */
function showWarningToast(message, duration = 3500) {
    showToast(message, 'warning', duration);
}

/**
 * Hiển thị toast thông tin
 */
function showInfoToast(message, duration = 3000) {
    showToast(message, 'info', duration);
}

// Kiểm tra session message khi trang load
document.addEventListener('DOMContentLoaded', function() {
    // Lấy message từ session (được set bởi servlet)
    const successMsg = document.body.getAttribute('data-success-msg');
    const errorMsg = document.body.getAttribute('data-error-msg');
    const warningMsg = document.body.getAttribute('data-warning-msg');
    const infoMsg = document.body.getAttribute('data-info-msg');
    
    if (successMsg) {
        showSuccessToast(successMsg);
        document.body.removeAttribute('data-success-msg');
    }
    if (errorMsg) {
        showErrorToast(errorMsg);
        document.body.removeAttribute('data-error-msg');
    }
    if (warningMsg) {
        showWarningToast(warningMsg);
        document.body.removeAttribute('data-warning-msg');
    }
    if (infoMsg) {
        showInfoToast(infoMsg);
        document.body.removeAttribute('data-info-msg');
    }
});

