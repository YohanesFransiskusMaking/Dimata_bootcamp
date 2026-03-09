package dto.request;


public class CreateOrderRequest {
   public String lokasiJemput;
    public String lokasiTujuan;

    public Double originLat;
    public Double originLng;

    public Double destinationLat;
    public Double destinationLng;

    public Long jenisKendaraanId;

    public Long getJenisKendaraanId() {
        return jenisKendaraanId;
    }
    public void setJenisKendaraanId(Long jenisKendaraanId) {
        this.jenisKendaraanId = jenisKendaraanId;
    }
    public String getLokasiJemput() {
        return lokasiJemput;
    }
    public void setLokasiJemput(String lokasiJemput) {
        this.lokasiJemput = lokasiJemput;
    }
    public String getLokasiTujuan() {
        return lokasiTujuan;
    }
    public void setLokasiTujuan(String lokasiTujuan) {
        this.lokasiTujuan = lokasiTujuan;
    }
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

    
}

