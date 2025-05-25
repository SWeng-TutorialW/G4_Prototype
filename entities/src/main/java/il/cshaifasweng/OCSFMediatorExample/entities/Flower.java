package il.cshaifasweng.OCSFMediatorExample.entities;

import jakarta.persistence.*;
import java.util.Arrays;
import java.io.Serializable;

@Entity
@Table(name ="flowers")

public class Flower implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private static final long serialVersionUID = -5912738471623457890L;
    private int id;
    private String name;
    private String type;
    private double price;

    @Lob
    private byte[] image;

    public Flower() { }

    public Flower(String name, String type, double price, byte[] image) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.image = image;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getId() {
        return id;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
}
