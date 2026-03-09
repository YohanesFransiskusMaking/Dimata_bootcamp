package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dto.request.CreatePaymentRequest;
import dto.request.RefundPaymentRequest;
import dto.response.PaymentResponse;
import dto.response.RefundPaymentResponse;
import entity.AppUser;
import entity.Order;
import entity.OrderStatus;
import entity.Payment;
import entity.PaymentMethod;
import entity.OrderPaymentStatus;
import entity.PaymentStatus;
import entity.UserWallet;
import entity.WalletTransType;
import entity.WalletTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import mapper.PaymentMapper;
import repository.AppUserRepository;
import repository.OrderRepository;
import repository.PaymentRepository;
import repository.UserWalletRepository;
import repository.WalletTransactionRepository;
import security.SecurityUtil;

@ApplicationScoped
public class PaymentService {
    @Inject
    SecurityUtil securityUtil;
    @Inject
    AppUserRepository userRepo;
    @Inject
    OrderRepository orderRepo;
    @Inject
    PaymentRepository paymentRepo;
    @Inject
    UserWalletRepository userWalletRepository;
    @Inject
    WalletTransactionRepository walletTransactionRepository;
    @Inject
    PaymentMapper paymentMapper;
    @Inject
    OrderService orderService;

    @Transactional
    public PaymentResponse create(CreatePaymentRequest req) {

        Long userId = securityUtil.getCurrentUserId();
        AppUser user = userRepo.findById(userId);

        Order order = orderRepo.findById(req.getOrderId());

        if (order == null)
            throw new WebApplicationException("Order tidak ditemukan", 404);

        // Authorization
        if (user.hasRole("CUSTOMER")) {
            if (!order.getCustomer().getId().equals(userId))
                throw new WebApplicationException("Bukan order milik anda", 403);
        } else if (!user.hasRole("ADMIN")) {
            throw new WebApplicationException("Role tidak diizinkan membuat payment", 403);
        }

        // Business rules
        if (order.getStatus() != OrderStatus.COMPLETED)
            throw new WebApplicationException("Order belum selesai", 400);

        if (order.getStatusPembayaran() == OrderPaymentStatus.PAID)
            throw new WebApplicationException("Order sudah PAID", 409);

        if (req.getJumlah().compareTo(order.getHargaTotal()) != 0)
            throw new WebApplicationException("Jumlah tidak sesuai harga total", 400);

        if (paymentRepo.find("order", order).firstResultOptional().isPresent())
            throw new WebApplicationException("Payment sudah ada", 409);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setJumlah(req.getJumlah());
        payment.setMetode(req.getMetode());

        if (req.getMetode() == PaymentMethod.WALLET) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setWaktuPembayaran(LocalDateTime.now());
        } else if (req.getMetode() == PaymentMethod.CASH) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        } else {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setWaktuPembayaran(LocalDateTime.now());
        }

        // WALLET
        if (req.getMetode() == PaymentMethod.WALLET) {

            UserWallet wallet = userWalletRepository.findByIdForUpdate(userId);

            if (wallet == null)
                throw new WebApplicationException("Wallet tidak ditemukan", 404);

            if (wallet.getBalance().compareTo(req.getJumlah()) < 0)
                throw new WebApplicationException("Saldo tidak cukup", 409);

            wallet.setBalance(wallet.getBalance().subtract(req.getJumlah()));

            WalletTransaction trx = new WalletTransaction();
            trx.setWallet(wallet);
            trx.setType(WalletTransType.PAYMENT);
            trx.setAmount(req.getJumlah());
            trx.setOrder(order);

            walletTransactionRepository.persist(trx);
        }

        paymentRepo.persist(payment);

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            order.setStatusPembayaran(OrderPaymentStatus.PAID);
            orderService.distributeCommission(order);
        }

        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public RefundPaymentResponse refund(Long paymentId, RefundPaymentRequest req) {

        Long userId = securityUtil.getCurrentUserId();
        AppUser user = userRepo.findById(userId);

        Payment payment = paymentRepo.findById(paymentId);

        if (payment == null)
            throw new WebApplicationException("Payment tidak ditemukan", 404);

        Order order = payment.getOrder();

        // ===== AUTHORIZATION =====
        if (user.hasRole("CUSTOMER")) {
            if (!order.getCustomer().getId().equals(userId))
                throw new WebApplicationException("Bukan order milik anda", 403);
        } else if (!user.hasRole("ADMIN")) {
            throw new WebApplicationException("Role tidak diizinkan refund", 403);
        }

        // ===== VALIDATION =====

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS)
            throw new WebApplicationException("Payment tidak dapat direfund", 400);

        if (req.getReason() == null || req.getReason().isBlank())
            throw new WebApplicationException("Reason wajib diisi", 400);

        BigDecimal refundable = payment.getJumlah().subtract(payment.getRefundAmount());

        if (refundable.compareTo(BigDecimal.ZERO) <= 0)
            throw new WebApplicationException("Payment sudah direfund penuh", 409);

        BigDecimal refundAmount;

        if (req.getAmount() == null) {
            refundAmount = refundable; // full refund
        } else {
            if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
                throw new WebApplicationException("Amount tidak valid", 400);

            if (req.getAmount().compareTo(refundable) > 0)
                throw new WebApplicationException("Jumlah refund melebihi sisa yang dapat direfund", 400);

            refundAmount = req.getAmount();
        }

        // ===== UPDATE PAYMENT =====

        payment.setRefundAmount(payment.getRefundAmount().add(refundAmount));

        boolean fullRefund = payment.getRefundAmount()
                .compareTo(payment.getJumlah()) == 0;

        if (fullRefund) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            payment.setRefundedAt(LocalDateTime.now());
            order.setStatusPembayaran(OrderPaymentStatus.REFUNDED);
        }

        // ===== WALLET REFUND =====

        if (payment.getMetode() == PaymentMethod.WALLET) {

            UserWallet wallet = userWalletRepository.findById(order.getCustomer().getId());

            if (wallet == null)
                throw new WebApplicationException("Wallet tidak ditemukan", 404);

            wallet.setBalance(wallet.getBalance().add(refundAmount));

            WalletTransaction trx = new WalletTransaction();
            trx.setWallet(wallet);
            trx.setType(WalletTransType.REFUND);
            trx.setAmount(refundAmount);
            trx.setOrder(order);
            trx.setCreatedAt(LocalDateTime.now());

            walletTransactionRepository.persist(trx);
        }

        // CASH / CARD → hanya record (simulasi manual process)

        RefundPaymentResponse res = new RefundPaymentResponse();
        res.setPaymentId(payment.getId());
        res.setOriginalAmount(payment.getJumlah());
        res.setRefundedAmount(payment.getRefundAmount());
        res.setRefundStatus(fullRefund ? "FULL" : "PARTIAL");
        res.setMessage("Refund berhasil diproses");

        return res;
    }

    @Transactional
    public void confirmCash(Long paymentId) {

        Payment payment = paymentRepo.findById(paymentId);

        if (payment == null)
            throw new WebApplicationException("Payment tidak ditemukan", 404);

        if (payment.getMetode() != PaymentMethod.CASH)
            throw new WebApplicationException("Bukan metode CASH", 400);

        if (payment.getPaymentStatus() != PaymentStatus.PENDING)
            throw new WebApplicationException("Status tidak valid", 400);

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setWaktuPembayaran(LocalDateTime.now());

        Order order = payment.getOrder();
        order.setStatusPembayaran(OrderPaymentStatus.PAID);

        orderService.distributeCommission(order);
    }

    public PaymentResponse getPaymentByOrder(Long orderId) {

        Payment payment = paymentRepo
                .find("order.id", orderId)
                .firstResult();

        if (payment == null) {
            throw new WebApplicationException("Payment belum ada", 404);
        }

        return paymentMapper.toResponse(payment);
    }

}
