package my.uum;

public class CafeAdmin {
    private String emailAddress;
    private String name;
    private byte[] passwordSalt;
    private String passwordHash;

    public CafeAdmin(String emailAddress, String name, byte[] passwordSalt, String passwordHash) {
        this.emailAddress = emailAddress;
        this.name = name;
        this.passwordSalt = passwordSalt;
        this.passwordHash = passwordHash;
    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public String getName() {
        return name;
    }

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    
}
