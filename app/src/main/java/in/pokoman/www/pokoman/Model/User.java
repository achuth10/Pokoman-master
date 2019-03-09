package in.pokoman.www.pokoman.Model;

public class User {
    private String uid;
    private String name;
    private String emailid;
    private int totalpt;
    private int lost;
    private int voucher;

    public User() {
    }

    public User(String uid, String name, String emailid, int totalpt, int voucher) {
        this.uid = uid;
        this.name = name;
        this.emailid = emailid;
        this.totalpt = totalpt;
        this.voucher = voucher;
    }
    public User(String uid, String name, String emailid, int totalpt, int voucher,int lost) {
        this.uid = uid;
        this.name = name;
        this.emailid = emailid;
        this.totalpt = totalpt;
        this.voucher = voucher;
        this.lost=lost;
    }

    public User( String name, String emailid, int totalpt, int voucher) {
        this.name = name;
        this.emailid = emailid;
        this.totalpt = totalpt;
        this.voucher = voucher;
    }
    public User(String name, String emailid) {
        this.name = name;
        this.emailid = emailid;

    }

    public int getTotalpt() {
        return totalpt;
    }

    public int getVoucher() {
        return voucher;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setTotalpt(int totalpt) {
        this.totalpt = totalpt;
    }

    public void setVoucher(int rank) {
        this.voucher = voucher;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }
}
