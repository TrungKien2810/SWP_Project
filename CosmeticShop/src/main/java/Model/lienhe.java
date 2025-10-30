package Model;

public class lienhe {
    private int id;
    private String name;
    private String phone;
    private String address;
    private String email;
    private String subject;
    private String message;
    private boolean status; // ✅ Thêm trạng thái xử lý (0 = chưa xử lý, 1 = đã xử lý)

    public lienhe() {}

    // Constructor cũ (để tương thích khi thêm mới liên hệ)
    public lienhe(String name, String phone, String address, String email, String subject, String message) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.status = false; // Mặc định là chưa xử lý
    }

    // Constructor mới (dùng khi lấy dữ liệu từ SQL)
    public lienhe(int id, String name, String phone, String address, String email, String subject, String message, boolean status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.status = status;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
