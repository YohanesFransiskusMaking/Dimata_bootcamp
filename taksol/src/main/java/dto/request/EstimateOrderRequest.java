package dto.request;

public class EstimateOrderRequest {

    public Double originLat;
    public Double originLng;
    public Double destinationLat;
    public Double destinationLng;
    public Long jenisKendaraanId;
    public Double getOriginLat() {
        return originLat;
    }
    public void setOriginLat(Double originLat) {
        this.originLat = originLat;
    }
    public Double getOriginLng() {
        return originLng;
    }
    public void setOriginLng(Double originLng) {
        this.originLng = originLng;
    }
    public Double getDestinationLat() {
        return destinationLat;
    }
    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }
    public Double getDestinationLng() {
        return destinationLng;
    }
    public void setDestinationLng(Double destinationLng) {
        this.destinationLng = destinationLng;
    }
    public Long getJenisKendaraanId() {
        return jenisKendaraanId;
    }
    public void setJenisKendaraanId(Long jenisKendaraanId) {
        this.jenisKendaraanId = jenisKendaraanId;
    }

}