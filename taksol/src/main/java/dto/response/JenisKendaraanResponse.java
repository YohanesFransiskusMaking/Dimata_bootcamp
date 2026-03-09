package dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JenisKendaraanResponse {
    public Long id;
    public String namaJenis;
    public Integer kapasitas;
    public BigDecimal tarifPerKm;
    public String deskripsi;
    public LocalDateTime createdAt;
}

