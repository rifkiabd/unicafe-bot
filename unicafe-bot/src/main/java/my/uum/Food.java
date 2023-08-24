package my.uum;

public class Food {
    private String foodCode;
    private String cafeCoded;
    private String foodType;
    private String foodName;
    private double foodPrice;
    private byte[] foodImage;
    private String status;

    public Food(String foodCode, String cafeCoded, String foodType, String foodName, double foodPrice, byte[] foodImage, String status) {
        this.foodCode = foodCode;
        this.cafeCoded = cafeCoded;
        this.foodType = foodType;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodImage = foodImage;
        this.status = status;
    }

    public String getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(String foodCode) {
        this.foodCode = foodCode;
    }

    public String getCafeCoded() {
        return cafeCoded;
    }

    public void setCafeCoded(String cafeCoded) {
        this.cafeCoded = cafeCoded;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public byte[] getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(byte[] foodImage) {
        this.foodImage = foodImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
