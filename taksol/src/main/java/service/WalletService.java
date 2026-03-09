package service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;
import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;

import dto.response.WalletResponse;
import entity.AppUser;
import entity.UserWallet;
import entity.WalletTransType;
import entity.WalletTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import jakarta.transaction.Transactional;
import repository.AppUserRepository;
import repository.UserWalletRepository;
import repository.WalletTransactionRepository;

@ApplicationScoped
public class WalletService {

    @Inject
    UserWalletRepository userWalletRepository;
    @Inject
    WalletTransactionRepository walletTransactionRepository;
    @Inject
    AppUserRepository userRepository;

    @ConfigProperty(name = "midtrans.server-key")
    String serverKey;

    @ConfigProperty(name = "midtrans.is-production")
    boolean isProduction;

    @Transactional
    public BigDecimal topUp(Long userId, BigDecimal amount) {

        UserWallet wallet = getOrCreateWallet(userId);

        wallet.setBalance(wallet.getBalance().add(amount));

        WalletTransaction trx = new WalletTransaction();
        trx.setWallet(wallet);
        trx.setType(WalletTransType.TOPUP);
        trx.setAmount(amount);

        walletTransactionRepository.persist(trx);

        return wallet.getBalance();
    }

    @Transactional
    public String createMidtransTransaction(Long userId, BigDecimal amount) throws Exception {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount tidak valid");
        }

        String orderId = "TOPUP-" + userId + "-" + System.currentTimeMillis();

        UserWallet wallet = getOrCreateWallet(userId);

        WalletTransaction trx = new WalletTransaction();
        trx.setWallet(wallet);
        trx.setType(WalletTransType.TOPUP);
        trx.setAmount(amount);
        trx.setPaymentStatus("PENDING");
        trx.setMidtransOrderId(orderId);

        walletTransactionRepository.persist(trx);

        Midtrans.serverKey = serverKey;
        Midtrans.isProduction = isProduction;

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", amount);

        Map<String, Object> params = new HashMap<>();
        params.put("transaction_details", transactionDetails);

        JSONObject response = SnapApi.createTransaction(params);

        return response.getString("token");
    }

    @Transactional
    public void processMidtransNotification(String payload) {

        JSONObject json = new JSONObject(payload);

        String orderId = json.getString("order_id");
        String transactionStatus = json.getString("transaction_status");
        String fraudStatus = json.optString("fraud_status");
        String statusCode = json.getString("status_code");
        String grossAmount = json.getString("gross_amount");
        String signatureKey = json.getString("signature_key");

        // 🔐 1️⃣ VERIFY SIGNATURE
        String expectedSignature = generateSignature(orderId, statusCode, grossAmount);

        if (!expectedSignature.equals(signatureKey)) {
            throw new SecurityException("Invalid Midtrans signature");
        }

        WalletTransaction trx = walletTransactionRepository
                .find("midtransOrderId = ?1", orderId)
                .firstResult();

        if (trx == null) {
            return;
        }

        // 🔁 2️⃣ IDEMPOTENCY CHECK
        if ("SUCCESS".equals(trx.getPaymentStatus())) {
            return;
        }

        // 💳 3️⃣ HANDLE STATUS
        if (("capture".equals(transactionStatus) && "accept".equals(fraudStatus))
                || "settlement".equals(transactionStatus)) {

            trx.setPaymentStatus("SUCCESS");

            UserWallet wallet = trx.getWallet();
            wallet.setBalance(wallet.getBalance().add(trx.getAmount()));

        } else if ("cancel".equals(transactionStatus)
                || "expire".equals(transactionStatus)
                || "deny".equals(transactionStatus)) {

            trx.setPaymentStatus("FAILED");
        }
    }

    private String generateSignature(String orderId, String statusCode, String grossAmount) {
        try {
            String input = orderId + statusCode + grossAmount + serverKey;

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Gagal generate signature", e);
        }
    }

    @Transactional
    public UserWallet getOrCreateWallet(Long userId) {

        return userWalletRepository.findByIdOptional(userId)
                .orElseGet(() -> {

                    AppUser user = userRepository.findByIdOptional(userId)
                            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

                    UserWallet wallet = new UserWallet();
                    wallet.setUser(user);
                    wallet.setBalance(BigDecimal.ZERO);

                    userWalletRepository.persist(wallet);

                    return wallet;
                });
    }

    public WalletResponse getWallet(Long userId) {

        UserWallet wallet = getOrCreateWallet(userId);

        return new WalletResponse(
                wallet.getBalance(),
                "Wallet retrieved successfully");
    }

    public List<WalletTransaction> getTransactions(Long userId) {

        return walletTransactionRepository
                .find("wallet.user.id", userId)
                .list();
    }

}
