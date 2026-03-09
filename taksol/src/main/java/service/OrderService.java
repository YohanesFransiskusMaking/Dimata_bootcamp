package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import dto.request.CreateOrderRequest;
import dto.request.EstimateOrderRequest;
import dto.request.UpdateOrderStatusRequest;
import dto.response.EstimateOrderResponse;
import dto.response.OrderDetailResponse;
import dto.response.OrderResponse;
import entity.AppUser;
import entity.DriverAvailabilityStatus;
import entity.DriverStatus;
import entity.JenisKendaraan;
import entity.AppConfig;
import entity.Order;
import entity.OrderDetail;
import entity.OrderPaymentStatus;
import entity.OrderStatus;
import entity.OrderStatusHistory;
import entity.Payment;
import entity.PaymentStatus;
import entity.UserWallet;
import entity.WalletTransType;
import entity.WalletTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import mapper.OrderMapper;
import repository.AppUserRepository;
import repository.JenisKendaraanRepository;
import repository.KendaraanRepository;
import repository.OrderDetailRepository;
import repository.OrderRepository;
import repository.AppConfigRepository;
import repository.OrderStatusHistoryRepository;
import repository.PaymentRepository;
import repository.UserWalletRepository;
import repository.WalletTransactionRepository;
import security.SecurityUtil;
import websocket.OrderSocketService;

@ApplicationScoped
public class OrderService {

    @Inject
    SecurityUtil securityUtil;
    @Inject
    AppUserRepository userRepo;
    @Inject
    OrderRepository orderRepository;
    @Inject
    OrderMapper mapper;
    @Inject
    JenisKendaraanRepository jenisKendaraanRepository;
    @Inject
    KendaraanRepository kendaraanRepository;
    @Inject
    OrderDetailRepository orderDetailRepository;
    @Inject
    OrderStatusHistoryRepository orderStatusHistoryRepository;
    @Inject
    AppConfigRepository appConfigRepository;
    @Inject
    GoogleMapsService googleMapsService;
    @Inject
    PaymentRepository paymentRepo;
    @Inject
    UserWalletRepository userWalletRepository;
    @Inject
    WalletTransactionRepository walletTransactionRepository;
    @Inject
    OrderSocketService orderSocketService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, AppUser customer) {

        validateCustomerNoActiveOrder(customer.getId());

        JenisKendaraan jenis = jenisKendaraanRepository
                .findById(request.jenisKendaraanId);

        if (jenis == null) {
            throw new WebApplicationException("Jenis kendaraan tidak ditemukan", 404);
        }

        BigDecimal distance = googleMapsService.calculateDistanceKm(
                request.originLat,
                request.originLng,
                request.destinationLat,
                request.destinationLng);

        BigDecimal subtotal = distance.multiply(jenis.getTarifPerKm());

        Order order = new Order();
        order.setLokasiJemput(request.lokasiJemput);
        order.setLokasiTujuan(request.lokasiTujuan);

        order.setOriginLat(request.originLat);
        order.setOriginLng(request.originLng);
        order.setDestinationLat(request.destinationLat);
        order.setDestinationLng(request.destinationLng);

        order.setDistanceKm(distance);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setStatusPembayaran(OrderPaymentStatus.UNPAID);
        order.setHargaTotal(subtotal);

        orderRepository.persist(order);

        // simpan detail
        OrderDetail detail = new OrderDetail();
        detail.setOrder(order);
        detail.setJenisKendaraan(jenis);
        detail.setTarifPerKm(jenis.getTarifPerKm());
        detail.setSubtotal(subtotal);

        orderDetailRepository.persist(detail);

        orderSocketService.broadcastNewOrder(
                order.getId(),
                order.getLokasiJemput(),
                order.getLokasiTujuan());

        return mapper.toResponse(order);
    }

    private OrderDetailResponse mapDetailToResponse(OrderDetail detail) {
        OrderDetailResponse res = new OrderDetailResponse();
        res.id = detail.getId();
        res.orderId = detail.getOrder().getId();
        res.jenisKendaraanId = detail.getJenisKendaraan().getId();
        res.kendaraanId = detail.getKendaraan().getId();
        res.jarakKm = detail.getOrder().getDistanceKm();
        res.tarifPerKm = detail.getTarifPerKm();
        res.subtotal = detail.getSubtotal();
        res.createdAt = detail.getCreatedAt();
        return res;
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest req) {

        try {
            Order order = orderRepository.findById(id);

            if (order == null)
                throw new WebApplicationException("Order tidak ditemukan", 404);

            OrderStatus current = order.getStatus();
            OrderStatus next = req.getStatus();

            Long userId = securityUtil.getCurrentUserId();
            AppUser user = userRepo.findById(userId);

            if (user == null)
                throw new WebApplicationException("User tidak ditemukan", 404);

            // ================= MULTI ROLE SAFETY =================

            boolean isDriver = user.hasRole("DRIVER");
            boolean isCustomer = user.hasRole("CUSTOMER");
            boolean isAdmin = user.hasRole("ADMIN");

            // Driver tidak boleh memproses order miliknya sendiri
            if (isDriver && order.getCustomer().getId().equals(userId)) {
                throw new WebApplicationException(
                        "Driver tidak bisa memproses order miliknya sendiri", 400);
            }

            // User harus terlibat dalam order (kecuali admin)
            if (!isAdmin && !order.isUserInvolved(userId)) {
                throw new WebApplicationException(
                        "Anda tidak memiliki akses ke order ini", 403);
            }

            // Validasi permission berdasarkan role
            validateRolePermission(order, user, next);

            // Validasi transisi status (kecuali admin)
            if (!isAdmin && !isValidTransition(current, next)) {
                throw new WebApplicationException("Transisi status tidak valid", 400);
            }

            // Jika selesai atau batal, set driver kembali ONLINE
            if (next == OrderStatus.COMPLETED || next == OrderStatus.CANCELLED) {
                AppUser driver = order.getAssignedDriver();
                if (driver != null) {
                    driver.getDriverProfile()
                            .setAvailabilityStatus(DriverAvailabilityStatus.ONLINE);
                }
            }

            order.setStatus(next);

            orderSocketService.broadcastStatus(
                    order.getId(),
                    next.name());

            // ================= SIMPAN HISTORY =================

            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            history.setStatus(next);
            history.setChangedBy(userId);
            history.setChangedAt(LocalDateTime.now());

            orderStatusHistoryRepository.persist(history);

            return mapper.toResponse(order);

        } catch (OptimisticLockException e) {
            throw new WebApplicationException(
                    "Order sudah diubah oleh proses lain. Silakan refresh.", 409);
        }
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {

        return switch (current) {
            case PENDING ->
                next == OrderStatus.CANCELLED;

            case ACCEPTED ->
                next == OrderStatus.ON_PROGRESS ||
                        next == OrderStatus.CANCELLED;

            case ON_PROGRESS ->
                next == OrderStatus.COMPLETED;

            default -> false;
        };
    }

    private void validateRolePermission(Order order,
            AppUser user,
            OrderStatus next) {

        boolean isAdmin = user.hasRole("ADMIN");
        boolean isCustomer = user.hasRole("CUSTOMER");
        boolean isDriver = user.hasRole("DRIVER");

        // ===== ADMIN =====
        if (isAdmin) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                throw new WebApplicationException("Order sudah selesai", 400);
            }
            return;
        }

        // ===== DRIVER =====
        if (isDriver) {

            if (order.getAssignedDriver() == null ||
                    !order.getAssignedDriver().getId().equals(user.getId())) {
                throw new WebApplicationException("Driver bukan pemilik order", 403);
            }

            if (next != OrderStatus.ON_PROGRESS &&
                    next != OrderStatus.COMPLETED) {
                throw new WebApplicationException("Driver tidak boleh aksi ini", 403);
            }

            return;
        }

        // ===== CUSTOMER =====
        if (isCustomer) {

            if (!order.getCustomer().getId().equals(user.getId())) {
                throw new WebApplicationException("Bukan order milik anda", 403);
            }

            if (next != OrderStatus.CANCELLED) {
                throw new WebApplicationException("Customer hanya boleh cancel", 403);
            }

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new WebApplicationException("Order tidak bisa dibatalkan", 400);
            }

            return;
        }

        throw new WebApplicationException("Role tidak dikenali", 403);
    }

    private BigDecimal getTarifPerKm() {

        AppConfig config = appConfigRepository
                .find("configKey", "tarif_per_km")
                .firstResult();

        if (config == null) {
            throw new WebApplicationException("Config tarif_per_km tidak ditemukan", 500);
        }

        try {
            return new BigDecimal(config.getConfigValue());
        } catch (NumberFormatException e) {
            throw new WebApplicationException("Format tarif_per_km tidak valid", 500);
        }
    }

    public OrderResponse toResponse(Order order) {
        return mapper.toResponse(order);
    }

    public AppUser getCurrentUser() {

        Long userId = securityUtil.getCurrentUserId();

        AppUser user = userRepo.findById(userId);

        if (user == null) {
            throw new WebApplicationException("User tidak ditemukan", 404);
        }

        return user;
    }

    @Transactional
    public OrderResponse acceptOrder(Long orderId) {

        // Load order dengan assignedDriver dan customer
        Order order = orderRepository.find("id", orderId)
                .firstResult(); // pastikan managed entity

        if (order == null) {
            throw new WebApplicationException("Order tidak ditemukan", 404);
        }

        Long driverId = securityUtil.getCurrentUserId();
        AppUser driver = userRepo.findById(driverId); // pastikan managed

        if (driver == null) {
            throw new WebApplicationException("Driver tidak ditemukan", 404);
        }

        if (driver.getDriverProfile() == null) {
            throw new WebApplicationException("User bukan driver", 400);
        }

        if (driver.getDriverProfile().getStatus() != DriverStatus.APPROVED) {
            throw new WebApplicationException("Driver belum APPROVED", 400);
        }

        if (driver.getDriverProfile().getAvailabilityStatus() != DriverAvailabilityStatus.ONLINE) {
            throw new WebApplicationException("Driver tidak dalam kondisi ONLINE", 400);
        }

        if (order.getCustomer() == null) {
            throw new WebApplicationException("Customer tidak ditemukan di order", 500);
        }

        // Driver tidak boleh ambil order sendiri
        if (order.getCustomer().getId().equals(driverId)) {
            throw new WebApplicationException("Driver tidak bisa mengambil order miliknya sendiri", 400);
        }

        // Cek status order
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new WebApplicationException("Order sudah tidak bisa diterima", 400);
        }

        // Cek apakah order sudah diambil driver lain
        if (order.getAssignedDriver() != null) {
            throw new WebApplicationException("Order sudah diambil driver lain", 409);
        }

        // Cek driver punya order aktif
        Order activeOrder = orderRepository.find(
                "assignedDriver.id = ?1 AND status IN (?2, ?3)",
                driverId,
                OrderStatus.ACCEPTED,
                OrderStatus.ON_PROGRESS).firstResult();

        if (activeOrder != null) {
            throw new WebApplicationException("Driver masih memiliki order aktif", 400);
        }

        // Assign driver & update status
        order.setAssignedDriver(driver);
        order.setStatus(OrderStatus.ACCEPTED);

        // Safe update driver status
        try {
            if (driver.getDriverProfile() != null) {
                driver.getDriverProfile().setAvailabilityStatus(DriverAvailabilityStatus.BUSY);
            }
        } catch (Exception e) {
            throw new WebApplicationException("Error update status driver: " + e.getMessage(), 500);
        }

        // Simpan history
        try {
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            history.setStatus(OrderStatus.ACCEPTED);
            history.setChangedBy(driverId);
            history.setChangedAt(LocalDateTime.now());
            orderStatusHistoryRepository.persist(history);
        } catch (Exception e) {
            throw new WebApplicationException("Error simpan history: " + e.getMessage(), 500);
        }

        return mapper.toResponse(order);
    }

    public OrderDetailResponse toDetailResponse(OrderDetail detail) {
        return mapper.toDetailResponse(detail);
    }

    public void distributeCommission(Order order) {

        Payment payment = paymentRepo.find("order", order).firstResult();

        if (payment == null)
            return;
        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS)
            return;

        BigDecimal total = order.getHargaTotal();

        AppConfig percentConfig = appConfigRepository
                .find("configKey", "commission_percentage")
                .firstResult();

        BigDecimal percent = new BigDecimal(percentConfig.getConfigValue());

        BigDecimal commission = total
                .multiply(percent)
                .divide(BigDecimal.valueOf(100));

        BigDecimal driverIncome = total.subtract(commission);

        // ===== DRIVER WALLET =====
        UserWallet driverWallet = userWalletRepository.findByIdForUpdate(
                order.getAssignedDriver().getId());

        driverWallet.setBalance(
                driverWallet.getBalance().add(driverIncome));

        WalletTransaction driverTrx = new WalletTransaction();
        driverTrx.setWallet(driverWallet);
        driverTrx.setType(WalletTransType.DRIVER_EARNING);
        driverTrx.setAmount(driverIncome);
        driverTrx.setOrder(order);

        walletTransactionRepository.persist(driverTrx);

        // ===== PLATFORM WALLET =====
        AppConfig platformConfig = appConfigRepository
                .find("configKey", "PLATFORM_USER_ID")
                .firstResult();

        Long platformUserId = Long.valueOf(platformConfig.getConfigValue());

        UserWallet platformWallet = userWalletRepository.findByIdForUpdate(platformUserId);

        platformWallet.setBalance(
                platformWallet.getBalance().add(commission));

        WalletTransaction platformTrx = new WalletTransaction();
        platformTrx.setWallet(platformWallet);
        platformTrx.setType(WalletTransType.PLATFORM_COMMISSION);
        platformTrx.setAmount(commission);
        platformTrx.setOrder(order);

        walletTransactionRepository.persist(platformTrx);
    }

    public List<OrderResponse> getAvailableOrders() {
        return orderRepository.findAvailableOrders()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public OrderResponse getMyActiveOrder() {

        Long driverId = securityUtil.getCurrentUserId();

        Order order = orderRepository.find(
                "assignedDriver.id = ?1 AND status IN (?2, ?3)",
                driverId,
                OrderStatus.ACCEPTED,
                OrderStatus.ON_PROGRESS).firstResult();

        if (order == null) {
            return null;
        }

        return mapper.toResponse(order);
    }

    @Transactional
    public List<OrderResponse> getCustomerOrders() {
        Long userId = securityUtil.getCurrentUserId();

        return orderRepository.find("customer.id", userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    private void validateCustomerNoActiveOrder(Long customerId) {

        Order activeOrder = orderRepository.find(
                "customer.id = ?1 AND status IN (?2, ?3, ?4)",
                customerId,
                OrderStatus.PENDING,
                OrderStatus.ACCEPTED,
                OrderStatus.ON_PROGRESS).firstResult();

        if (activeOrder != null) {
            throw new WebApplicationException(
                    "Anda masih memiliki order aktif. Selesaikan atau batalkan terlebih dahulu.",
                    400);
        }
    }

    public OrderResponse getCustomerActiveOrder() {

        Long userId = securityUtil.getCurrentUserId();

        Order order = orderRepository.find(
                "customer.id = ?1 AND status IN (?2, ?3, ?4)",
                userId,
                OrderStatus.PENDING,
                OrderStatus.ACCEPTED,
                OrderStatus.ON_PROGRESS).firstResult();

        if (order == null) {
            return null;
        }

        return mapper.toResponse(order);
    }

    private void validateDriverNoActiveOrder(Long driverId) {

        Order activeOrder = orderRepository.find(
                "assignedDriver.id = ?1 AND status IN (?2, ?3)",
                driverId,
                OrderStatus.ACCEPTED,
                OrderStatus.ON_PROGRESS).firstResult();

        if (activeOrder != null) {
            throw new WebApplicationException(
                    "Driver masih memiliki order aktif",
                    400);
        }
    }

    @Transactional
    public EstimateOrderResponse estimateOrder(EstimateOrderRequest request) {

        JenisKendaraan jenis = jenisKendaraanRepository.findById(request.getJenisKendaraanId());
        if (jenis == null) {
            throw new WebApplicationException("Jenis kendaraan tidak ditemukan", 404);
        }

        BigDecimal distanceKm = googleMapsService.calculateDistanceKm(
                request.getOriginLat(),
                request.getOriginLng(),
                request.getDestinationLat(),
                request.getDestinationLng());

        BigDecimal price = distanceKm.multiply(jenis.getTarifPerKm());

        price = price.setScale(0, RoundingMode.UP);

        EstimateOrderResponse response = new EstimateOrderResponse();
        response.setDistanceKm(distanceKm);
        response.setEstimatedPrice(price);

        return response;
    }

    private double calculateDistance(
            double lat1, double lon1,
            double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public List<OrderResponse> getDriverOrders() {

        Long driverId = securityUtil.getCurrentUserId();

        return orderRepository
                .find("assignedDriver.id", driverId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public OrderDetailResponse getOrderDetail(Long orderId) {

        OrderDetail detail = orderDetailRepository
                .find("order.id", orderId)
                .firstResult();

        if (detail == null) {
            throw new WebApplicationException("Order detail tidak ditemukan", 404);
        }

        return mapper.toDetailResponse(detail);
    }

}
