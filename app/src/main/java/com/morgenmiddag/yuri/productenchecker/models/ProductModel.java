package com.morgenmiddag.yuri.productenchecker.models;

/**
 * Model for a Product object, so we can manipulate the data easier.
 */

public class ProductModel {

    // Fields for the data from the Product.
    private String name;
    private String description;
    private Float price;
    private String image;
    private Shop shop;

    /**
     * Getters and Setters for adding and getting the data.
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    /**
     * Inner class shop, to have the Shop data represented in an Object.
     */
    public static class Shop{
        // Fields for the data from the Shop
        private String name;
        private Double latitude;
        private Double longitude;

        /**
         * Getters and Setters for adding and getting the data.
         * @return
         */
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }
    }
}
