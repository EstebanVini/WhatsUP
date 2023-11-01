public class User {
    private String username;
    private String password;
    private String phone;

    public User(String username, String password, String phoneText) {
        this.username = username;
        this.password = password;
        this.phone = phoneText;
    }


    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {return password; }

}