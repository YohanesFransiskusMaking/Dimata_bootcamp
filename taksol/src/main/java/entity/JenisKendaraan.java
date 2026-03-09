package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "jenis_kendaraan",
       uniqueConstraints = @UniqueConstraint(columnNames = "nama_jenis"))
public class JenisKendaraan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_jenis", nullable = false, length = 100)
    private String namaJenis;

    @Column(nullable = false)
    private Integer kapasitas;

    @Column(name = "tarif_per_km", nullable = false)
    private BigDecimal tarifPerKm;

    @Column(length = 255)
    private String deskripsi;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaJenis() {
        return namaJenis;
    }

    public void setNamaJenis(String namaJenis) {
        this.namaJenis = namaJenis;
    }

    public Integer getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(Integer kapasitas) {
        this.kapasitas = kapasitas;
    }

    public BigDecimal getTarifPerKm() {
        return tarifPerKm;
    }

    public void setTarifPerKm(BigDecimal tarifPerKm) {
        this.tarifPerKm = tarifPerKm;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

   
}

