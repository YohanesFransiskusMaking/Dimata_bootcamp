package dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateJenisKendaraanRequest {

    @NotBlank
    @Size(max = 100)
    public String namaJenis;

    @NotNull
    @Min(1)
    public Integer kapasitas;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    public BigDecimal tarifPerKm;

    @Size(max = 255)
    public String deskripsi;
}

