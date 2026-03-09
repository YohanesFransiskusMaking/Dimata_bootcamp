package dto.response;

import java.time.LocalDateTime;

public class KendaraanResponse {

    public Long id;
    public String platNomor;
    public Long jenisKendaraanId;
    public Long driverId;
    public String status;
    public LocalDateTime createdAt;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPlatNomor() {
        return platNomor;
    }
    public void setPlatNomor(String platNomor) {
        this.platNomor = platNomor;
    }
    public Long getJenisKendaraanId() {
        return jenisKendaraanId;
    }
    public void setJenisKendaraanId(Long jenisKendaraanId) {
        this.jenisKendaraanId = jenisKendaraanId;
    }
    public Long getDriverId() {
        return driverId;
    }
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
}

