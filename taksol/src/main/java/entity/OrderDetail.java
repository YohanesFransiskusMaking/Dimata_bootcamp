package entity;

import entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details", uniqueConstraints = @UniqueConstraint(columnNames = "order_id"))
public class OrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "jenis_kendaraan_id", nullable = false)
    private JenisKendaraan jenisKendaraan;

    @ManyToOne
    @JoinColumn(name = "kendaraan_id")
    private Kendaraan kendaraan;

    @Column(name = "tarif_per_km", nullable = false)
    private BigDecimal tarifPerKm;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public JenisKendaraan getJenisKendaraan() {
        return jenisKendaraan;
    }

    public void setJenisKendaraan(JenisKendaraan jenisKendaraan) {
        this.jenisKendaraan = jenisKendaraan;
    }

    public Kendaraan getKendaraan() {
        return kendaraan;
    }

    public void setKendaraan(Kendaraan kendaraan) {
        this.kendaraan = kendaraan;
    }


    public BigDecimal getTarifPerKm() {
        return tarifPerKm;
    }

    public void setTarifPerKm(BigDecimal tarifPerKm) {
        this.tarifPerKm = tarifPerKm;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
