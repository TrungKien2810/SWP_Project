# 🚀 Hướng Dẫn Build và Deploy (Skip Tests)

## ⚠️ Vấn Đề

Khi build để deploy, Maven mặc định sẽ chạy test trước. Nếu test fail hoặc web server chưa chạy, build sẽ bị lỗi.

## ✅ Giải Pháp: Skip Tests Khi Build

### Cách 1: Dùng Flag `-DskipTests` (Khuyến nghị)

```bash
# Build WAR file mà không chạy test
mvn clean package -DskipTests

# Hoặc
mvn clean install -DskipTests
```

**Lưu ý:** `-DskipTests` sẽ:
- ✅ Compile test code
- ❌ Không chạy test
- ✅ Build WAR file thành công

### Cách 2: Dùng Profile `deploy`

```bash
# Build với profile deploy (skip tests)
mvn clean package -Pdeploy

# Hoặc
mvn clean install -Pdeploy
```

### Cách 3: Dùng Flag `-Dmaven.test.skip=true` (Skip hoàn toàn)

```bash
# Skip cả compile và run tests
mvn clean package -Dmaven.test.skip=true
```

**Lưu ý:** `-Dmaven.test.skip=true` sẽ:
- ❌ Không compile test code
- ❌ Không chạy test
- ✅ Build nhanh hơn

## 📋 Các Lệnh Build Thường Dùng

### Build WAR File (Skip Tests)

```bash
# PowerShell
mvn clean package -DskipTests

# Kết quả: target/CosmeticShop-1.0-SNAPSHOT.war
```

### Build và Install (Skip Tests)

```bash
mvn clean install -DskipTests
```

### Build với Test (Khi cần)

```bash
# Chạy tất cả tests
mvn test

# Chạy test cụ thể
mvn test -Dtest=LoginE2ETest
```

### Clean Build (Xóa target và build lại)

```bash
mvn clean package -DskipTests
```

## 🔧 Sửa Lỗi Encoding

Nếu gặp lỗi encoding khi build:

1. **Đảm bảo file Java được lưu với UTF-8:**
   - Trong IDE: File → Settings → Editor → File Encodings
   - Set "Project Encoding" = UTF-8
   - Set "Default encoding for properties files" = UTF-8

2. **Đã cấu hình trong pom.xml:**
   ```xml
   <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
   ```
   Và trong maven-compiler-plugin:
   ```xml
   <encoding>UTF-8</encoding>
   ```

3. **Nếu vẫn lỗi, convert file:**
   - Mở file bị lỗi trong IDE
   - File → Reload from Disk → Chọn UTF-8
   - Save lại

## 📦 Deploy WAR File

Sau khi build thành công:

1. **Tìm WAR file:**
   ```
   target/CosmeticShop-1.0-SNAPSHOT.war
   ```

2. **Deploy vào Tomcat:**
   - Copy WAR file vào thư mục `webapps` của Tomcat
   - Hoặc dùng Tomcat Manager để deploy

3. **Kiểm tra:**
   ```
   http://localhost:8080/CosmeticShop
   ```

## 🎯 Tóm Tắt

### Build để Deploy (Không chạy test):
```bash
mvn clean package -DskipTests
```

### Chạy Test riêng (Khi cần):
```bash
mvn test
```

### Build với Test (Khi muốn):
```bash
mvn clean package
```

## ⚡ Quick Reference

| Mục đích | Lệnh |
|----------|------|
| Build WAR (skip tests) | `mvn clean package -DskipTests` |
| Build WAR (có tests) | `mvn clean package` |
| Chạy test riêng | `mvn test` |
| Clean build | `mvn clean package -DskipTests` |
| Install vào local repo | `mvn clean install -DskipTests` |

---

**Lưu ý:** Khi deploy, luôn dùng `-DskipTests` để tránh lỗi test!

