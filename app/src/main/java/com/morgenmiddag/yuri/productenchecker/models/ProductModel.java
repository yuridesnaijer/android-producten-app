package com.morgenmiddag.yuri.productenchecker.models;

import java.util.List;

/**
 * Model for a Product object, so we can manipulate the data easier.
 */

public class ProductModel {

    private String name;
    private String description;
    private Float price;
    private String image;
    private Shop shop;

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

    public static class Shop{
        private String name;
        private String location;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
//    name": "Usb stick",
//            "description": "Even snel iets opslaan",
//            "price": 7.65,
//            "image": "https:\/\/docent.cmi.hro.nl\/bootb\/service\/images\/usbstick.png",
//            "shop": {
//        "name": "Boolclue",
//                "location": {
//            "latitude": 51.92583,
//                    "longitude": 4.512136
//        }
//}
