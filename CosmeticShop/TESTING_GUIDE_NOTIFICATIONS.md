# 🧪 HƯỚNG DẪN KIỂM THỬ CHỨC NĂNG THÔNG BÁO

**Ngày tạo:** 11/11/2025  
**Phiên bản:** 1.0  

---

## 📋 Nội Dung

1. [Chuẩn Bị Môi Trường](#chuẩn-bị-môi-trường)
2. [Test Cases](#test-cases)
3. [Debug Logging](#debug-logging)
4. [Troubleshooting](#troubleshooting)

---

## ✅ Chuẩn Bị Môi Trường

### **Yêu Cầu:**
- JDK 11+
- Tomcat 10+
- SQL Server database
- Modern browser (Chrome, Firefox, Edge)
- Developer tools (F12)

### **Bước 1: Start Application**

```bash
# Từ folder project
mvn clean compile
mvn tomcat7:run
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
...
[INFO] Tomcat server started at http://localhost:8080
```

### **Bước 2: Tạo Test Data**

```sql
-- SQL Server - Tạo một số thông báo test
INSERT INTO Notifications (user_id, notification_type, title, message, is_read, created_at)
VALUES 
  (1, 'DISCOUNT_ASSIGNED', 'Nhận voucher giảm 10%', 'Bạn vừa nhận được voucher ', 0, GETDATE()),
  (1, 'ORDER_STATUS', 'Đơn hàng được xác nhận', 'Đơn hàng #123 đã được xác nhận', 0, GETDATE()),
  (1, 'PROMOTION', 'Flash sale hôm nay', 'Giảm tới 50% cho các sản phẩm được chọn', 0, GETDATE());
```

### **Bước 3: Đăng Nhập**

1. Truy cập http://localhost:8080/CosmeticShop
2. Click "ĐĂNG NHẬP & ĐĂNG KÝ"
3. Đăng nhập bằng tài khoản có `user_id = 1`
4. Kiểm tra bell icon 🔔 có badge không (phải có số 3)

---

## 🧪 Test Cases

### **Test Case 1: Hiển Thị Badge Notifications**

**Mục đích:** Verify rằng badge count hiển thị đúng

**Bước:**
1. ✅ Đăng nhập
2. ✅ Nhìn vào bell icon ở header
3. ✅ Xem badge count (số trên icon)

**Expected Result:**
```
Badge hiển thị số: 3 (hoặc số unread notifications)
Nếu > 99 hiển thị: "99+"
Nếu = 0: badge ẩn đi
```

**Verification:**
```javascript
// Mở Browser Console (F12 → Console)
// Copy-paste code dưới:
console.log('Unread count:', document.getElementById('notificationBadge').textContent);
console.log('Badge display:', window.getComputedStyle(document.getElementById('notificationBadge')).display);
```

---

### **Test Case 2: Mở Dropdown Thông Báo**

**Mục đích:** Verify rằng dropdown mở và load notifications đúng

**Bước:**
1. ✅ Click vào bell icon
2. ✅ Dropdown mở ra
3. ✅ Xem danh sách notifications

**Expected Result:**
```
Dropdown show với:
- Header "Thông báo"
- Button "Đánh dấu tất cả đã đọc"
- Danh sách thông báo (3 items)
- Footer với link "Xem tất cả"
```

**Verification:**
```javascript
// F12 → Console
console.log('Dropdown visible:', 
  document.getElementById('notificationDropdown').classList.contains('show'));
console.log('Notifications count:', 
  document.querySelectorAll('.notification-item').length);
```

---

### **Test Case 3: Single Notification Mark as Read**

**Mục đích:** Verify rằng đánh dấu một thông báo hoạt động

**Bước:**
1. ✅ Click vào dropdown để mở
2. ✅ Click vào notification thứ nhất
3. ✅ Notification biến mất unread class
4. ✅ Badge count giảm từ 3 → 2

**Expected Result:**
```
- Item notification không còn có class "unread"
- Badge count update: 3 → 2
- KHÔNG navigate (vì test notifications không có linkUrl)
```

**Verification:**
```javascript
// F12 → Console
// Sau khi click notification thứ nhất
console.log('First item has unread class:', 
  document.querySelectorAll('.notification-item')[0].classList.contains('unread'));
console.log('Badge count:', 
  document.getElementById('notificationBadge').textContent);
```

---

### **Test Case 4: Mark All as Read - PRIMARY TEST** ⭐

**Mục đích:** Verify rằng "Đánh dấu tất cả đã đọc" hoạt động chính xác

**Bước:**
1. ✅ Refresh page để reset test data
2. ✅ Đăng nhập lại
3. ✅ Click bell icon mở dropdown
4. ✅ Click button "Đánh dấu tất cả đã đọc"
5. ✅ Quan sát UI update

**Expected Result:**
```
Ngay lập tức:
- Tất cả items trong dropdown mất class "unread"
- Badge count → 0 → badge ẩn
- Danh sách notifications hiện "Không có thông báo mới"
- Không có lỗi trong console

Khi reload page:
- Badge vẫn ẩn
- Tất cả notifications vẫn là "read = 1"
```

**Server-side Verification:**

```sql
-- SQL Server - Check database
SELECT notification_id, user_id, title, is_read, created_at
FROM Notifications
WHERE user_id = 1
ORDER BY created_at DESC;

-- Expected: Tất cả is_read = 1
```

---

### **Test Case 5: Debug Logging - Console Output** 📋

**Mục đích:** Verify rằng debug logging hoạt động

**Bước:**
1. ✅ Mở F12 → Console tab
2. ✅ Click "Đánh dấu tất cả đã đọc"
3. ✅ Xem logs trong console

**Expected Console Output:**
```javascript
[DEBUG] markAllAsRead - starting request
[DEBUG] markAllAsRead - response received: Object {success: true, unreadCount: 0, ...}
[DEBUG] markAllAsRead - success! clearing notifications
[DEBUG] markAllAsRead - updated unreadCount: 0
```

**Expected Server Log Output:**
```
[DEBUG] markAllRead action - userId: 1
[DEBUG] markAllAsRead - userId: 1, includeAdminGlobal: false
[DEBUG] markAllAsRead - SQL: UPDATE Notifications SET is_read = 1 WHERE user_id = 1 AND ...
[DEBUG] markAllAsRead - rows updated: 3
[DEBUG] markAllRead result - success: true, unreadCount: 0
```

---

### **Test Case 6: Race Condition Handling** 🏃

**Mục đích:** Verify rằng race condition không gây vấn đề

**Bước:**
1. ✅ Mở F12 → Network tab
2. ✅ Click bell icon
3. ✅ Ngay lập tức click "Đánh dấu tất cả đã đọc" (TRƯỚC khi list load xong)
4. ✅ Xem Network requests
5. ✅ Kiểm tra version tracking

**Expected Result:**
```
Network tab shows:
- GET /notifications?action=list (pending)
- POST /notifications (thực hiện)
- Kết quả: UI cập nhật từ POST response (mới nhất)
- Khi GET list response về: BỎ QUA (vì version cũ hơn)

Console:
[DEBUG] markAllAsRead - ignoring stale response
(hoặc load completed đúng)
```

---

### **Test Case 7: Error Handling - Network Error** 🔴

**Mục đích:** Verify rằng error handling hoạt động khi network lỗi

**Bước:**
1. ✅ Mở F12 → Network tab
2. ✅ Throttle network (Slow 3G)
3. ✅ Click dropdown
4. ✅ Ngay lập tức close dev tools Network tab (offline)
5. ✅ Click "Đánh dấu tất cả đã đọc"
6. ✅ Kiểm tra error handling

**Expected Result:**
```
Console:
Error marking all as read: NetworkError...

Fallback behavior:
- Call loadNotifications() để reload list
- Call loadNotificationCount() để reload badge
- UI eventually updates từ fallback API calls
```

**Verification:**
```javascript
// F12 → Network tab
// Check requests được retry
// Should see: loadNotifications call
// Should see: loadNotificationCount call
```

---

### **Test Case 8: Multi-User Isolation** 👥

**Mục đích:** Verify rằng mark all read của user 1 không ảnh hưởng user 2

**Bước:**
1. ✅ Mở 2 browser tabs (hoặc private windows)
2. ✅ Tab 1: Đăng nhập user 1
3. ✅ Tab 2: Đăng nhập user 2
4. ✅ Tab 1: Click "Đánh dấu tất cả đã đọc"
5. ✅ Tab 2: Kiểm tra notifications vẫn là unread

**Expected Result:**
```
Tab 1 (User 1):
- Badge ẩn → unreadCount = 0
- Notifications all marked as read

Tab 2 (User 2):
- Badge vẫn hiển thị → unreadCount > 0
- Notifications vẫn unread
```

**SQL Verification:**
```sql
-- Check database
SELECT user_id, COUNT(*) as read_count
FROM Notifications
WHERE is_read = 1 AND user_id IN (1, 2)
GROUP BY user_id;

-- Expected:
-- user_id: 1, read_count: 3 (or all notifications of user 1)
-- user_id: 2, read_count: 0 or previous state
```

---

## 🔍 Debug Logging

### **Location của Debug Logs:**

**Frontend (Browser Console):**
```javascript
// F12 → Console tab
// Logs từ notifications.js
[DEBUG] markAllAsRead - starting request
[DEBUG] markAllAsRead - response received
[DEBUG] markAllAsRead - success! clearing notifications
```

**Backend (Server Logs):**
```
// Tomcat console hoặc catalina.out
[DEBUG] markAllRead action - userId: ...
[DEBUG] markAllAsRead - userId: ..., includeAdminGlobal: ...
[DEBUG] markAllAsRead - rows updated: ...
```

### **Cách Enable Detailed Logging:**

**Browser DevTools Settings:**
```
F12 → Console settings
- Preserve log (checkbox)
- Verbose (if available)
- Enable All Messages
```

**Server Logs:**
```
Tomcat folder:
- logs/catalina.out (all System.out)
- logs/catalina.err (all System.err)
```

---

## ❌ Troubleshooting

### **Problem: Badge shows but doesn't update**

**Nguyên nhân có thể:**
1. Session expired
2. JavaScript error
3. Database connection lost

**Solution:**
```bash
# 1. Check F12 → Console cho errors
# 2. Kiểm tra server logs
# 3. Verify database connection
# 4. Clear browser cache: Ctrl+Shift+Delete
# 5. Restart Tomcat: mvn tomcat7:run (kill + restart)
```

---

### **Problem: "Mark all read" button không work**

**Nguyên nhân có thể:**
1. Button selector sai
2. Event listener not attached
3. Controller endpoint không return

**Solution:**
```javascript
// F12 → Console
// Verify button exists:
console.log(document.getElementById('markAllReadBtn'));

// Verify element is clickable:
document.getElementById('markAllReadBtn').click();

// Check network request:
// F12 → Network tab → POST /notifications
```

---

### **Problem: Notifications list không load**

**Nguyên nhân có thể:**
1. User không đăng nhập
2. Controller error
3. Database error

**Solution:**
```
# 1. Check login:
   - Kiểm tra session trong header
   - sessionScope.user !== null

# 2. Check server logs:
   - mvn tomcat7:run console output
   - Tìm [ERROR] logs

# 3. Check database:
   - SELECT * FROM Notifications WHERE user_id = 1
   - Kiểm tra table có data không

# 4. Check network response:
   - F12 → Network → GET /notifications?action=list
   - Response payload
```

---

### **Problem: Race condition gây UI weird**

**Nguyên nhân:**
1. listVersion tracking không work
2. Multiple concurrent requests

**Solution:**
```javascript
// F12 → Console
// Check version numbers:
console.log('listVersion:', listVersion);  // access nó từ closure
console.log('countVersion:', countVersion);

// Add more debug logs trong notifications.js:
// - Line 149: log localListVersion = ++listVersion
// - Line 169: log comparison if (localListVersion !== listVersion)
```

---

### **Problem: Database commit không thành công**

**Nguyên nhân:**
1. SQL error
2. PreparedStatement sai
3. Transaction rollback

**Solution:**
```
# 1. Check server error logs:
   - [ERROR] markAllAsRead failed: ...

# 2. Verify SQL query:
   - Copy SQL từ log
   - Run trực tiếp trong SQL Server Management Studio
   - Check syntax

# 3. Verify user_id parameter:
   - Check [DEBUG] log: userId: ?
   - Kiểm tra user_id hợp lệ
```

---

## 📊 Performance Testing

### **Load Test - Multiple Rapid Clicks:**

```javascript
// F12 → Console
// Simulate rapid clicks:
for (let i = 0; i < 5; i++) {
    setTimeout(() => {
        document.getElementById('markAllReadBtn').click();
    }, i * 100);
}
```

**Expected Result:**
- ✅ Race condition handling works
- ✅ UI không bị corrupt
- ✅ Final state correct
- ✅ Last request wins (versioning)

---

### **Memory Leak Check:**

```javascript
// F12 → Performance tab
// 1. Record timeline
// 2. Click mark all read 10 times
// 3. Stop recording
// 4. Check memory usage
// Expected: stable, không tăng liên tục
```

---

## ✔️ Test Summary Checklist

```
[ ] Test Case 1: Badge Display - PASS/FAIL
[ ] Test Case 2: Dropdown Open - PASS/FAIL
[ ] Test Case 3: Single Mark Read - PASS/FAIL
[ ] Test Case 4: Mark All Read - PASS/FAIL ⭐
[ ] Test Case 5: Debug Logging - PASS/FAIL
[ ] Test Case 6: Race Condition - PASS/FAIL
[ ] Test Case 7: Error Handling - PASS/FAIL
[ ] Test Case 8: Multi-User - PASS/FAIL

Overall Status: [ ] PASS [ ] FAIL
```

---

**Thời gian test dự kiến:** 30-45 phút  
**Người test:** _______________  
**Ngày test:** _______________  
**Ghi chú:**

```
_________________________________________________________________
_________________________________________________________________
_________________________________________________________________
```

---

**Document Version**: 1.0  
**Last Updated**: 11/11/2025  
**Status**: ✅ Ready for Testing

