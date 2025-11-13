// Notification center page script
(function() {
    document.addEventListener('DOMContentLoaded', function() {
        const listEl = document.getElementById('notificationPageList');
        if (!listEl) {
            return;
        }

        const loaderEl = document.getElementById('notificationPageLoader');
        const emptyEl = document.getElementById('notificationPageEmpty');
        const markAllBtn = document.getElementById('notificationPageMarkAllRead');
        const refreshBtn = document.getElementById('notificationPageRefresh');
        const contextPath = document.body.getAttribute('data-context-path') || '';

        let notifications = [];

        function setLoaderVisible(visible) {
            if (!loaderEl) return;
            loaderEl.classList.toggle('d-none', !visible);
            loaderEl.style.display = visible ? 'block' : 'none';
        }

        function showEmptyState(show) {
            if (!emptyEl) return;
            emptyEl.classList.toggle('show', show);
            emptyEl.classList.toggle('d-none', !show);
        }

        function syncBadge(count) {
            const badge = document.getElementById('notificationBadge');
            if (!badge) return;

            const safeCount = Math.max(0, count);
            if (safeCount > 0) {
                badge.textContent = safeCount > 99 ? '99+' : safeCount;
                badge.dataset.count = safeCount;
                badge.removeAttribute('hidden');
                badge.removeAttribute('aria-hidden');
                badge.style.display = 'flex';
            } else {
                badge.textContent = '0';
                badge.dataset.count = 0;
                badge.setAttribute('hidden', '');
                badge.setAttribute('aria-hidden', 'true');
                badge.style.display = 'none';
            }
        }

        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text ?? '';
            return div.innerHTML;
        }

        function formatType(type) {
            if (!type) return 'Khác';
            const map = {
                'NEW_ORDER': 'Đơn hàng mới',
                'CUSTOMER_FEEDBACK': 'Phản hồi khách hàng',
                'LOW_RATING': 'Đánh giá thấp',
                'DISCOUNT_ASSIGNED': 'Voucher mới',
                'SPECIAL_EVENT': 'Sự kiện'
            };
            return map[type] || type.replace(/_/g, ' ').toLowerCase()
                .replace(/(^|\s)\S/g, function(t) { return t.toUpperCase(); });
        }

        function formatTime(dateString) {
            if (!dateString) return '';
            const date = new Date(dateString);
            if (Number.isNaN(date.getTime())) return '';

            const now = new Date();
            const diffMs = now - date;
            const diffSec = Math.floor(diffMs / 1000);
            const diffMin = Math.floor(diffSec / 60);
            const diffHour = Math.floor(diffMin / 60);
            const diffDay = Math.floor(diffHour / 24);

            if (diffDay > 0) return `${diffDay} ngày trước`;
            if (diffHour > 0) return `${diffHour} giờ trước`;
            if (diffMin > 0) return `${diffMin} phút trước`;
            return 'Vừa xong';
        }

        function resolveLink(linkUrl) {
            if (!linkUrl) return null;
            if (linkUrl.startsWith('http://') || linkUrl.startsWith('https://')) {
                return linkUrl;
            }
            if (linkUrl.startsWith('/')) {
                return contextPath + linkUrl;
            }
            return contextPath + '/' + linkUrl;
        }

        function renderList(data) {
            notifications = Array.isArray(data) ? data : [];
            const unreadCount = notifications.filter(n => n && !n.read).length;
            syncBadge(unreadCount);

            if (notifications.length === 0) {
                listEl.innerHTML = '';
                showEmptyState(true);
                return;
            }

            showEmptyState(false);
            listEl.innerHTML = notifications.map(function(notif) {
                const classes = ['notification-card'];
                if (!notif.read) {
                    classes.push('unread');
                }

                const link = resolveLink(notif.linkUrl);
                const typeLabel = formatType(notif.notificationType);
                const timeLabel = formatTime(notif.createdAt);

                return `
                    <div class="${classes.join(' ')}" data-id="${notif.notificationId}">
                        <div class="d-flex flex-column gap-2">
                            <div class="d-flex justify-content-between align-items-start gap-3">
                                <div>
                                    <div class="notification-title">${escapeHtml(notif.title)}</div>
                                    <p class="notification-message">${escapeHtml(notif.message)}</p>
                                </div>
                                <span class="tag"><i class="fa-regular fa-bell notification-badge-icon"></i> ${escapeHtml(typeLabel)}</span>
                            </div>
                            <div class="notification-meta">
                                <span><i class="fa-regular fa-clock me-1"></i>${escapeHtml(timeLabel)}</span>
                                ${link ? `<a href="${link}" class="text-decoration-none"><i class="fa-solid fa-arrow-up-right-from-square me-1"></i>Xem chi tiết</a>` : ''}
                            </div>
                            <div class="notification-actions">
                                ${notif.read
                                    ? `<button type="button" class="btn btn-sm btn-outline-secondary" disabled><i class="fa-regular fa-circle-check me-1"></i>Đã đọc</button>`
                                    : `<button type="button" class="btn btn-sm btn-outline-primary" data-action="markRead" data-id="${notif.notificationId}"><i class="fa-regular fa-envelope-open me-1"></i>Đánh dấu đã đọc</button>`
                                }
                                <button type="button" class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${notif.notificationId}">
                                    <i class="fa-regular fa-trash-can me-1"></i>Xóa
                                </button>
                            </div>
                        </div>
                    </div>
                `;
            }).join('');
        }

        function fetchNotifications() {
            setLoaderVisible(true);
            fetch(contextPath + '/notifications?action=list', { cache: 'no-store' })
                .then(function(response) {
                    if (!response.ok) {
                        throw new Error('Failed to load notifications');
                    }
                    return response.json();
                })
                .then(function(data) {
                    renderList(data);
                })
                .catch(function(error) {
                    console.error('Không thể tải thông báo:', error);
                    listEl.innerHTML = '<div class="alert alert-danger">Không thể tải dữ liệu thông báo. Vui lòng thử lại sau.</div>';
                    syncBadge(0);
                })
                .finally(function() {
                    setLoaderVisible(false);
                });
        }

        function markNotificationAsRead(notificationId) {
            const body = 'action=' + encodeURIComponent('markRead') +
                         '&notificationId=' + encodeURIComponent(notificationId);
            return fetch(contextPath + '/notifications', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                body: body
            }).then(function(response) {
                if (!response.ok) {
                    throw new Error('Failed to mark notification as read');
                }
                return response.json();
            }).then(function(payload) {
                if (payload.success) {
                    const idx = notifications.findIndex(function(n) { return n.notificationId === notificationId; });
                    if (idx >= 0) {
                        notifications[idx].read = true;
                    }
                    renderList(notifications);
                    if (typeof payload.unreadCount === 'number') {
                        syncBadge(payload.unreadCount);
                    }
                }
            }).catch(function(error) {
                console.error('Lỗi đánh dấu đã đọc:', error);
            });
        }

        function deleteNotification(notificationId) {
            const body = 'action=' + encodeURIComponent('delete') +
                         '&notificationId=' + encodeURIComponent(notificationId);
            return fetch(contextPath + '/notifications', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                body: body
            }).then(function(response) {
                if (!response.ok) {
                    throw new Error('Failed to delete notification');
                }
                return response.json();
            }).then(function(payload) {
                if (payload.success) {
                    notifications = notifications.filter(function(n) { return n.notificationId !== notificationId; });
                    renderList(notifications);
                    if (typeof payload.unreadCount === 'number') {
                        syncBadge(payload.unreadCount);
                    }
                }
            }).catch(function(error) {
                console.error('Lỗi xóa thông báo:', error);
            });
        }

        function markAllNotificationsRead() {
            const body = 'action=' + encodeURIComponent('markAllRead');
            return fetch(contextPath + '/notifications', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                body: body,
                cache: 'no-store'
            }).then(function(response) {
                if (!response.ok) {
                    throw new Error('Failed to mark all as read');
                }
                return response.json();
            }).then(function(payload) {
                if (payload.success) {
                    notifications = notifications.map(function(n) {
                        return Object.assign({}, n, { read: true });
                    });
                    renderList(notifications);
                    if (typeof payload.unreadCount === 'number') {
                        syncBadge(payload.unreadCount);
                    } else {
                        syncBadge(0);
                    }
                }
            }).catch(function(error) {
                console.error('Lỗi khi đánh dấu tất cả đã đọc:', error);
            });
        }

        listEl.addEventListener('click', function(event) {
            const actionButton = event.target.closest('[data-action]');
            if (!actionButton) return;

            const action = actionButton.getAttribute('data-action');
            const id = parseInt(actionButton.getAttribute('data-id'), 10);

            if (!Number.isInteger(id)) return;

            if (action === 'markRead') {
                markNotificationAsRead(id);
            } else if (action === 'delete') {
                if (confirm('Bạn có chắc muốn xóa thông báo này?')) {
                    deleteNotification(id);
                }
            }
        });

        if (refreshBtn) {
            refreshBtn.addEventListener('click', function() {
                fetchNotifications();
            });
        }

        if (markAllBtn) {
            markAllBtn.addEventListener('click', function() {
                markAllNotificationsRead();
            });
        }

        fetchNotifications();
    });
})();


