package mapper;

import dto.response.OrderDetailResponse;
import dto.response.OrderResponse;
import entity.Order;
import entity.OrderDetail;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        OrderResponse res = new OrderResponse();
        res.id = order.getId();
        res.lokasiJemput = order.getLokasiJemput();
        res.lokasiTujuan = order.getLokasiTujuan();
        res.assignedDriverId = order.getAssignedDriver() != null
                ? order.getAssignedDriver().getId()
                : null;
        res.status = order.getStatus().name();
        res.statusPembayaran = order.getStatusPembayaran().name();
        res.hargaTotal = order.getHargaTotal();
        res.createdAt = order.getCreatedAt();

        return res;
    }

    public OrderDetailResponse toDetailResponse(OrderDetail detail) {

        OrderDetailResponse res = new OrderDetailResponse();

        res.id = detail.getId();
        res.orderId = detail.getOrder().getId();
        res.jenisKendaraanId = detail.getJenisKendaraan().getId();

        res.jarakKm = detail.getOrder().getDistanceKm(); 
        res.tarifPerKm = detail.getTarifPerKm();
        res.subtotal = detail.getSubtotal();
        res.createdAt = detail.getCreatedAt();

        return res;
    }

}
