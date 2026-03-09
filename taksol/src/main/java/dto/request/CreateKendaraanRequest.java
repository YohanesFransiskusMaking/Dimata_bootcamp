package dto.request;


public class CreateKendaraanRequest {

    private String platNomor;
    private Long jenisKendaraanId;

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
}

