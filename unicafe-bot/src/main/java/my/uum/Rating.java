package my.uum;

public class Rating {
    private String foodCode;
    private float rating;

    public Rating(String foodCode, float rating) {
        this.foodCode = foodCode;
        this.rating = rating;
    }

    public String getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(String foodCode) {
        this.foodCode = foodCode;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
