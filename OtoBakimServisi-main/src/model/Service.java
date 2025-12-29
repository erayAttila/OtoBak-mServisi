package model;


public class Service {

    // SERVICE tablosundaki kolonlar
    private int serviceId;
    private String name;
    private int duration;   // dakika 
    private double price;

    // Boş constructor
    public Service() {
    }

    // Tüm alanları alan constructor
    public Service(int serviceId, String name, int duration, double price) {
        this.serviceId = serviceId;
        this.name = name;
        this.duration = duration;
        this.price = price;
    }

    // Getter - Setter'lar
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    @Override
    public String toString() {
        return name;
    }

}
