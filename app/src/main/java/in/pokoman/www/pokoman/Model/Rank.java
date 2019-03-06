package in.pokoman.www.pokoman.Model;

public class Rank {
    private String uid;
    private String uimage;
    private String uname;
    private int totalpt;

    public Rank() {
    }

    public Rank(String uid, String uimage, String uname, int totalpt) {
        this.uid = uid;
        this.uimage = uimage;
        this.uname = uname;
        this.totalpt = totalpt;
    }

    public String getUid() {
        return uid;
    }

    public String getUimage() {
        return uimage;
    }

    public int getTotalpt() {
        return totalpt;
    }

    public String getUname() {
        return uname;
    }
}
