package my.uum;

public class Comment {
    private String cafeCode;
    private String comment;

    public Comment() {
    }

    public Comment(String cafeCode, String comment) {
        this.cafeCode = cafeCode;
        this.comment = comment;
    }

    public String getCafeCode() {
        return cafeCode;
    }

    public void setCafeCode(String cafeCode) {
        this.cafeCode = cafeCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
