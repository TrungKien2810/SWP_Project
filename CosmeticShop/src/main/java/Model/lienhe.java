package Model;

public class lienhe {
    private int id;
    private String name;
    private String phone;
    private String address;
    private String email;
    private String subject;
    private String message;

    public lienhe() {}

    public lienhe(String name, String phone, String address, String email, String subject, String message) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    // Getter & Setter
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
}
