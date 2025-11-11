// Notification system JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const notificationBell = document.getElementById('notificationBell');
    const notificationBadge = document.getElementById('notificationBadge');
    const notificationDropdown = document.getElementById('notificationDropdown');
    const notificationList = document.getElementById('notificationList');
    const markAllReadBtn = document.getElementById('markAllReadBtn');
    const viewAllNotifications = document.getElementById('viewAllNotifications');
    
    if (!notificationBell) return; // Exit if user is not logged in
    
    // Get context path from a data attribute or use relative path
    const contextPath = notificationBell.getAttribute('data-context-path') || '';
    
    let notifications = [];
    let unreadCount = 0;
    // Versioning tách biệt cho count và list để tránh can nhiễu lẫn nhau
    let countVersion = 0;
    let listVersion = 0;
    
    // Load notifications count
    function updateBadge() {
        const badgeValue = unreadCount > 99 ? '99+' : unreadCount;
        notificationBadge.textContent = badgeValue;
        notificationBadge.dataset.count = unreadCount;
        // Control both hidden attribute and inline style for compatibility with existing CSS
        if (unreadCount > 0) {
            notificationBadge.removeAttribute('hidden');
            notificationBadge.removeAttribute('aria-hidden');
            notificationBadge.style.display = 'flex';
        } else {
            notificationBadge.textContent = '0';
            notificationBadge.setAttribute('hidden', '');
            notificationBadge.setAttribute('aria-hidden', 'true');
            notificationBadge.style.display = 'none';
        }
    }

    function loadNotificationCount() {
        const localVersion = ++countVersion;
        fetch(contextPath + '/notifications?action=count', {
            cache: 'no-store'
        })
            .then(response => response.json())
            .then(data => {
                if (localVersion !== countVersion) return; // bỏ qua response cũ
                unreadCount = typeof data.count === 'number' ? data.count : 0;
                updateBadge();
            })
            .catch(error => {
                console.error('Error loading notification count:', error);
            });
    }
    
    // Load notifications list
    function loadNotifications() {
        const localVersion = ++listVersion;
        notificationList.innerHTML = '<div class="notification-loading">Đang tải...</div>';
        
        // Lấy danh sách đầy đủ (bao gồm global cho admin). Badge sẽ được cập nhật từ kênh count riêng.
        fetch(contextPath + '/notifications?action=list', {
            cache: 'no-store'
        })
            .then(response => response.json())
            .then(data => {
                if (localVersion !== listVersion) return; // bỏ qua response cũ
                notifications = data;
                renderNotifications(data);
            })
            .catch(error => {
                console.error('Error loading notifications:', error);
                notificationList.innerHTML = '<div class="notification-empty">Không thể tải thông báo</div>';
            });
    }
    
    // Render notifications
    function renderNotifications(notifications) {
        if (notifications.length === 0) {
            notificationList.innerHTML = '<div class="notification-empty">Không có thông báo mới</div>';
            return;
        }
        
        notificationList.innerHTML = notifications.map(notif => {
            const timeAgo = getTimeAgo(notif.createdAt);
            const unreadClass = !notif.read ? 'unread' : '';
            return `
                <div class="notification-item ${unreadClass}" data-id="${notif.notificationId}" data-read="${notif.read}">
                    <div class="notification-item-title">${escapeHtml(notif.title)}</div>
                    <div class="notification-item-message">${escapeHtml(notif.message)}</div>
                    <div class="notification-item-time">${timeAgo}</div>
                </div>
            `;
        }).join('');
        
        // Add click handlers
        notificationList.querySelectorAll('.notification-item').forEach(item => {
            item.addEventListener('click', function() {
                const notificationId = parseInt(this.dataset.id);
                const isRead = this.dataset.read === 'true';
                const linkUrl = notifications.find(n => n.notificationId === notificationId)?.linkUrl;
                
                // Mark as read if not read
                if (!isRead) {
                    markAsRead(notificationId);
                }
                
                // Navigate to link if available
                if (linkUrl) {
                    window.location.href = linkUrl;
                }
            });
        });
    }
    
    // Mark notification as read
    function markAsRead(notificationId) {
        const localListVersion = ++listVersion; // đảm bảo phản hồi cũ không override list
        // Sử dụng x-www-form-urlencoded để Servlet đọc được bằng getParameter
        const body = 'action=' + encodeURIComponent('markRead') +
                     '&notificationId=' + encodeURIComponent(notificationId);
        
        fetch(contextPath + '/notifications', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
            body: body
        })
        .then(response => response.json())
        .then(data => {
            if (localListVersion !== listVersion) return;
            if (data.success) {
                // Update UI
                const item = notificationList.querySelector(`[data-id="${notificationId}"]`);
                if (item) {
                    item.classList.remove('unread');
                    item.dataset.read = 'true';
                }
                if (typeof data.unreadCount === 'number') {
                    unreadCount = data.unreadCount;
                } else {
                    unreadCount = Math.max(0, unreadCount - 1);
                }
                updateBadge();
                // Reload count to sync with server in background
                loadNotificationCount(); // tăng countVersion riêng, không ảnh hưởng list
            }
        })
        .catch(error => {
            console.error('Error marking notification as read:', error);
        });
    }
    
    // Mark all as read
    function markAllAsRead() {
        const localListVersion = ++listVersion; // cô lập với count
        // Sử dụng x-www-form-urlencoded để Servlet đọc được bằng getParameter
        const body = 'action=' + encodeURIComponent('markAllRead');
        
        console.log('[DEBUG] markAllAsRead - starting request');
        
        fetch(contextPath + '/notifications', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
            body: body,
            cache: 'no-store'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('[DEBUG] markAllAsRead - response received:', data);
            
            if (localListVersion !== listVersion) {
                console.log('[DEBUG] markAllAsRead - ignoring stale response');
                return;
            }
            
            if (data.success) {
                console.log('[DEBUG] markAllAsRead - success! clearing notifications');
                // Server trả về danh sách thông báo đã cập nhật
                if (data.notifications && Array.isArray(data.notifications)) {
                    notifications = data.notifications;
                    renderNotifications(notifications);
                } else {
                    notifications = [];
                    notificationList.innerHTML = '<div class="notification-empty">Không có thông báo mới</div>';
                }
            } else {
                console.warn('[DEBUG] markAllAsRead - failed:', data.error);
                // Reload từ server để cập nhật
                loadNotifications();
            }
            
            // Cập nhật unreadCount từ response
            if (typeof data.unreadCount === 'number') {
                unreadCount = data.unreadCount;
                console.log('[DEBUG] markAllAsRead - updated unreadCount:', unreadCount);
            } else {
                unreadCount = 0;
            }
            
            updateBadge();
            // Đồng bộ lại count từ server (kênh riêng)
            loadNotificationCount();
        })
        .catch(error => {
            console.error('Error marking all as read:', error);
            // Fallback: reload từ server
            loadNotifications();
            loadNotificationCount();
        });
    }
    
    // Toggle dropdown
    notificationBell.addEventListener('click', function(e) {
        e.stopPropagation();
        notificationDropdown.classList.toggle('show');
        if (notificationDropdown.classList.contains('show')) {
            loadNotifications();
        }
    });
    
    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        if (!notificationDropdown.contains(e.target) && !notificationBell.contains(e.target)) {
            notificationDropdown.classList.remove('show');
        }
    });
    
    // Mark all as read button
    if (markAllReadBtn) {
        markAllReadBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            // Optimistic UI: hide badge immediately
            unreadCount = 0;
            updateBadge();
            // Clear list visually
            if (notificationList) {
                notificationList.innerHTML = '<div class="notification-empty">Không có thông báo mới</div>';
            }
            markAllAsRead();
        });
    }
    
    // View all notifications link
    if (viewAllNotifications) {
        viewAllNotifications.addEventListener('click', function(e) {
            e.preventDefault();
            // You can implement a full notifications page here
            window.location.href = contextPath + '/notifications?action=list';
        });
    }
    
    // Helper functions
    function getTimeAgo(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        const now = new Date();
        const diff = now - date;
        const seconds = Math.floor(diff / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);
        
        if (days > 0) return `${days} ngày trước`;
        if (hours > 0) return `${hours} giờ trước`;
        if (minutes > 0) return `${minutes} phút trước`;
        return 'Vừa xong';
    }
    
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
    
    // Load initial count
    loadNotificationCount();
    
    // Refresh count every 30 seconds
    setInterval(loadNotificationCount, 30000);
});

