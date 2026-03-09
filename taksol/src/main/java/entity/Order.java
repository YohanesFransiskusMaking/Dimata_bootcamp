package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.persistence.Version;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lokasi_jemput", length = 200, nullable = false)
    private String lokasiJemput;

    @Column(name = "lokasi_tujuan", length = 200, nullable = false)
    private String lokasiTujuan;

    @Column(name = "origin_lat", nullable = false)
    private Double originLat;

    @Column(name = "origin_lng", nullable = false)
    private Double originLng;

    @Column(name = "destination_lat", nullable = false)
    private Double destinationLat;

    @Column(name = "destination_lng", nullable = false)
    private Double destinationLng;

    @Column(name = "distance_km")
    private BigDecimal distanceKm;

    @ManyToOne
    private AppUser customer;

    @ManyToOne
    @JoinColumn(name = "assigned_driver_id")
    private AppUser assignedDriver;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pembayaran")
    private OrderPaymentStatus statusPembayaran;

    @Column(name = "harga_total", nullable = false)
    private BigDecimal hargaTotal;

    @Version
    private Long version;

    public BigDecimal getHargaTotal() {
        return hargaTotal;
    }

    public void setHargaTotal(BigDecimal hargaTotal) {
        this.hargaTotal = hargaTotal;
    }

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    

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

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public AppUser getCustomer() {
        return customer;
    }

    public void setCustomer(AppUser customer) {
        this.customer = customer;
    }

    public AppUser getAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(AppUser assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderPaymentStatus getStatusPembayaran() {
        return statusPembayaran;
    }

    public void setStatusPembayaran(OrderPaymentStatus statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUserInvolved(Long userId) {
        if (userId == null)
            return false;

        if (customer != null && customer.getId().equals(userId)) {
            return true;
        }

        if (assignedDriver != null && assignedDriver.getId().equals(userId)) {
            return true;
        }

        return false;
    }

}
