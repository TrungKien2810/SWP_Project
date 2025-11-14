# 📋 Maven Commands - Quick Reference

## Lỗi Thường Gặp

### ❌ Lỗi: "Unknown lifecycle phase"
```
[ERROR] Unknown lifecycle phase ".vscode"
```

**Nguyên nhân:** Chạy lệnh Maven sai format hoặc có ký tự đặc biệt

**Giải pháp:** Sử dụng các lệnh Maven hợp lệ bên dưới

---

## Các Lệnh Maven Cơ Bản

### 1. Compile và Build

```bash
# Compile code
mvn compile

# Compile test code
mvn test-compile

# Clean và compile
mvn clean compile

# Package thành WAR file
mvn clean package
```

### 2. Chạy Tests

```bash
# Chạy tất cả tests
mvn test

# Chạy tests với output chi tiết
mvn test -X

# Chạy tests theo package
mvn test -Dtest=Controller.*
mvn test -Dtest=DAO.*
mvn test -Dtest=Model.*

# Chạy test class cụ thể
mvn test -Dtest=LoginFlowTest
mvn test -Dtest=ProductDBTest

# Chạy E2E tests (tự động thao tác trên web)
# Lưu ý: Pattern E2E.* không hoạt động, dùng tên class cụ thể
mvn test -Dtest=LoginE2ETest
mvn test -Dtest=AddToCartE2ETest
mvn test -Dtest=AdminE2ETest

# Hoặc chạy tất cả E2E tests
mvn test -Dtest="E2E.**"

# Chạy tất cả tests TRỪ E2E
mvn test -Dtest=!E2E.*

# Chạy test method cụ thể
mvn test -Dtest=LoginFlowTest#shouldRejectInvalidEmailFormat
```

### 3. Coverage với JaCoCo

```bash
# Chạy tests và tạo coverage report
mvn clean verify

# Xem report (sau khi chạy verify)
# Mở file: target/site/jacoco/index.html
```

### 4. Skip Tests

```bash
# Compile và package mà không chạy tests
mvn clean package -DskipTests

# Skip tests hoàn toàn (không compile test code)
mvn clean package -Dmaven.test.skip=true
```

### 5. Clean và Rebuild

```bash
# Xóa target folder
mvn clean

# Clean và rebuild
mvn clean install

# Clean, compile, test, package
mvn clean package
```

---

## Các Lifecycle Phases Hợp Lệ

Maven có các lifecycle phases sau (theo thứ tự):

### Clean Lifecycle
- `pre-clean`
- `clean`
- `post-clean`

### Default Lifecycle
- `validate`
- `initialize`
- `generate-sources`
- `process-sources`
- `generate-resources`
- `process-resources`
- `compile`
- `process-classes`
- `generate-test-sources`
- `process-test-sources`
- `generate-test-resources`
- `process-test-resources`
- `test-compile`
- `process-test-classes`
- **`test`** ← Chạy tests
- `prepare-package`
- **`package`** ← Tạo WAR/JAR
- `pre-integration-test`
- `integration-test`
- `post-integration-test`
- **`verify`** ← Chạy tests + tạo reports
- **`install`** ← Install vào local repository
- **`deploy`** ← Deploy lên remote repository

### Site Lifecycle
- `pre-site`
- `site`
- `post-site`
- `site-deploy`

---

## Ví Dụ Lệnh Đúng

```bash
# ✅ ĐÚNG
mvn test
mvn clean test
mvn test -Dtest=LoginFlowTest
mvn clean verify

# ❌ SAI
mvn .vscode          # ".vscode" không phải lifecycle phase
mvn test.vscode      # Không hợp lệ
mvn "test"           # Có thể gây lỗi với quotes
```

---

## Troubleshooting

### Lỗi: "Unknown lifecycle phase"

**Nguyên nhân:**
- Gõ sai lệnh
- Copy-paste nhầm
- Có ký tự đặc biệt trong command

**Giải pháp:**
- Kiểm tra lại lệnh
- Sử dụng các lệnh trong danh sách trên
- Tránh copy-paste từ VS Code settings

### Lỗi: "No goals have been specified"

**Giải pháp:**
- Luôn chỉ định lifecycle phase hoặc goal
- Ví dụ: `mvn test`, `mvn compile`, `mvn clean`

---

## Tips

1. **Luôn dùng `clean` trước khi build:**
   ```bash
   mvn clean test
   mvn clean package
   ```

2. **Chạy test cụ thể khi develop:**
   ```bash
   mvn test -Dtest=*FeatureName*Test
   ```

3. **Xem coverage sau mỗi lần sửa code:**
   ```bash
   mvn clean verify
   # Mở: target/site/jacoco/index.html
   ```

4. **Skip E2E tests nếu không cần:**
   ```bash
   mvn test -Dtest=!E2E.*
   ```

---

**Lưu ý:** Luôn chạy lệnh Maven từ thư mục gốc của project (nơi có file `pom.xml`)

