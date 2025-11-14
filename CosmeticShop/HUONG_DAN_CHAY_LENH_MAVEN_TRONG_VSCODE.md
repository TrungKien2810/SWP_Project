# 💻 Hướng Dẫn Chạy Lệnh Maven Trong VS Code

## Cách Mở Terminal Trong VS Code

### Phương Pháp 1: Dùng Menu (Khuyến Nghị)

1. **Mở Terminal:**
   - Click menu: `Terminal` → `New Terminal`
   - Hoặc dùng phím tắt: `Ctrl + `` (dấu backtick, phím trên Tab)

2. **Terminal sẽ mở ở dưới cùng của VS Code**

3. **Đảm bảo đang ở thư mục project:**
   ```bash
   # Kiểm tra thư mục hiện tại
   pwd  # Linux/Mac
   cd   # Windows (PowerShell)
   
   # Nếu không đúng, chuyển đến thư mục project
   cd C:\Edu\Project\SWP_Project\CosmeticShop
   ```

### Phương Pháp 2: Dùng Command Palette

1. Nhấn `Ctrl + Shift + P` (hoặc `F1`)
2. Gõ: `Terminal: Create New Terminal`
3. Nhấn Enter

### Phương Pháp 3: Dùng Shortcut

- **Windows/Linux:** `Ctrl + `` (backtick)
- **Mac:** `Ctrl + `` hoặc `Cmd + ``

---

## Các Lệnh Maven Cơ Bản

### 1. Chạy Tất Cả Tests

```bash
mvn test
```

### 2. Chạy Test Cụ Thể

```bash
# Chạy một test class
mvn test -Dtest=LoginFlowTest

# Chạy E2E test
mvn test -Dtest=LoginE2ETest

# Chạy nhiều test classes
mvn test -Dtest=LoginFlowTest,AddToCartFlowTest
```

### 3. Chạy Tests với Coverage

```bash
mvn clean verify
```

### 4. Clean và Rebuild

```bash
mvn clean test
mvn clean package
```

---

## Kiểm Tra Terminal Đang Hoạt Động

### Dấu Hiệu Terminal Đang Mở:

1. **Có cửa sổ terminal ở dưới cùng VS Code**
2. **Có dòng prompt hiển thị:**
   ```
   PS C:\Edu\Project\SWP_Project\CosmeticShop>
   ```
   hoặc
   ```
   C:\Edu\Project\SWP_Project\CosmeticShop>
   ```

### Nếu Không Thấy Terminal:

1. **Kiểm tra panel dưới cùng:**
   - Click vào tab `TERMINAL` ở dưới cùng
   - Hoặc click vào icon terminal ở thanh status bar (dưới cùng bên trái)

2. **Mở lại terminal:**
   - `Terminal` → `New Terminal`
   - Hoặc `Ctrl + ``

---

## Ví Dụ Chạy Lệnh

### Bước 1: Mở Terminal

```
1. Click menu Terminal → New Terminal
2. Hoặc nhấn Ctrl + `
```

### Bước 2: Kiểm Tra Thư Mục

```bash
# Windows PowerShell
cd

# Kết quả mong đợi:
# C:\Edu\Project\SWP_Project\CosmeticShop
```

### Bước 3: Chạy Lệnh Maven

```bash
# Ví dụ: Chạy tất cả tests
mvn test

# Hoặc chạy test cụ thể
mvn test -Dtest=LoginFlowTest
```

---

## Troubleshooting

### Vấn Đề 1: "Command not found: mvn"

**Nguyên nhân:** Maven chưa được cài đặt hoặc chưa có trong PATH

**Giải pháp:**
1. Kiểm tra Maven đã cài:
   ```bash
   mvn -version
   ```

2. Nếu chưa cài, cài Maven:
   - Download từ: https://maven.apache.org/download.cgi
   - Thêm vào PATH environment variable

### Vấn Đề 2: Terminal Không Hiển Thị

**Giải pháp:**
1. Click vào tab `TERMINAL` ở panel dưới cùng
2. Hoặc: `View` → `Terminal`
3. Hoặc nhấn `Ctrl + ``

### Vấn Đề 3: Lệnh Bị Lỗi ".vscode"

**Nguyên nhân:** Copy-paste nhầm hoặc có ký tự đặc biệt

**Giải pháp:**
- Gõ lại lệnh thủ công
- Đảm bảo lệnh đúng format: `mvn test -Dtest=LoginFlowTest`

### Vấn Đề 4: Terminal Ở Sai Thư Mục

**Giải pháp:**
```bash
# Chuyển đến thư mục project
cd C:\Edu\Project\SWP_Project\CosmeticShop

# Kiểm tra có file pom.xml
dir pom.xml  # Windows
ls pom.xml   # Linux/Mac
```

---

## Tips

### 1. Dùng Multiple Terminals

- Click icon `+` bên cạnh tab terminal để mở terminal mới
- Hoặc: `Terminal` → `New Terminal`

### 2. Split Terminal

- Click icon split (hai hình chữ nhật) để chia terminal
- Hữu ích khi cần chạy nhiều lệnh cùng lúc

### 3. Clear Terminal

- Click icon trash hoặc gõ: `clear` (Linux/Mac) hoặc `cls` (Windows)

### 4. Copy/Paste

- **Copy:** Chọn text và `Ctrl + C`
- **Paste:** `Ctrl + V` hoặc click chuột phải

---

## Ví Dụ Hoàn Chỉnh

```
1. Mở VS Code
2. Mở project: File → Open Folder → Chọn CosmeticShop
3. Mở Terminal: Terminal → New Terminal (hoặc Ctrl + `)
4. Kiểm tra thư mục: cd (sẽ hiển thị đường dẫn)
5. Chạy lệnh: mvn test
6. Xem kết quả trong terminal
```

---

## Lưu Ý

- ✅ Terminal trong VS Code giống như Command Prompt hoặc PowerShell
- ✅ Có thể chạy bất kỳ lệnh nào như trong terminal thông thường
- ✅ Maven phải được cài đặt và có trong PATH
- ✅ Đảm bảo đang ở thư mục có file `pom.xml`

---

**Tóm lại:** Mở terminal trong VS Code bằng `Ctrl + `` hoặc `Terminal → New Terminal`, sau đó gõ lệnh Maven như bình thường!



