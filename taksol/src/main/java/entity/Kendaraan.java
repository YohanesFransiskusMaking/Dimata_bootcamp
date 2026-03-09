package entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "kendaraan", uniqueConstraints = @UniqueConstraint(columnNames = "plat_nomor"))
public class Kendaraan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plat_nomor", unique = true, nullable = false, length = 20)
    private String platNomor;

    @ManyToOne
    @JoinColumn(name = "jenis_kendaraan_id", nullable = false)
    private JenisKendaraan jenisKendaraan;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private AppUser driver;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "kendaraan_status")
    private KendaraanStatus status;

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

    public JenisKendaraan getJenisKendaraan() {
        return jenisKendaraan;
    }

    public void setJenisKendaraan(JenisKendaraan jenisKendaraan) {
        this.jenisKendaraan = jenisKendaraan;
    }

    public AppUser getDriver() {
        return driver;
    }

    public void setDriver(AppUser driver) {
        this.driver = driver;
    }

    public KendaraanStatus getStatus() {
        return status;
    }

    public void setStatus(KendaraanStatus status) {
        this.status = status;
    }

}
