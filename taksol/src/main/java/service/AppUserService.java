package service;

import java.time.LocalDateTime;
import java.util.List;

import dto.request.DriverApplyRequest;
import dto.request.UserRequest;
import dto.request.UserRequestUpdate;
import dto.response.DriverAdminResponse;
import dto.response.UserResponse;
import entity.AppUser;
import entity.DriverProfile;
import entity.DriverStatus;
import entity.EmailVerificationToken;
import entity.PasswordResetToken;
import entity.Role;
import entity.UserVerification;
import entity.VerificationStatus;
import exception.ConflictException;
import exception.DomainException;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import mapper.AppUserMapper;
import mapper.DriverProfileMapper;
import repository.AppUserRepository;
import repository.DriverProfileRepository;
import repository.EmailVerificationTokenRepository;
import repository.PasswordResetTokenRepository;
import repository.RoleRepository;
import repository.UserVerificationRepository;
import security.SecurityUtil;
import security.TokenUtil;

@ApplicationScoped
public class AppUserService {

    @Inject
    SecurityUtil securityUtil;
    @Inject
    EmailVerificationTokenRepository emailTokenRepository;
    @Inject
    TokenUtil tokenUtil;
    @Inject
    EmailService emailService;
    @Inject
    AppUserRepository repository;
    @Inject
    AppUserMapper mapper;
    @Inject
    RoleRepository roleRepository;
    @Inject
    DriverProfileRepository driverRepository;
    @Inject
    DriverProfileMapper driverMapper;
    @Inject
    WalletService walletService;
    @Inject
    UserVerificationRepository verificationRepository;
    @Inject
    VerificationService verificationService;
    @Inject
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public UserResponse createUser(UserRequest request) {

        if (repository.existsByEmail(request.email)) {
            throw new ConflictException("Email sudah terdaftar");
        }

        if (repository.existsByNoHp(request.noHp)) {
            throw new ConflictException("No HP sudah terdaftar");
        }

        Role role = roleRepository.findByRoleName("CUSTOMER");

        if (role == null) {
            throw new RuntimeException("Role tidak ditemukan");
        }

        AppUser user = mapper.toEntity(request);
        user.setPasswordHash(BcryptUtil.bcryptHash(request.password));
        user.setEmailVerified(false);
        user.setRoles(List.of(role));

        repository.persist(user);
        walletService.getOrCreateWallet(user.getId());

        String rawToken = tokenUtil.generateRefreshToken();
        String hashedToken = tokenUtil.hash(rawToken);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setTokenHash(hashedToken);
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUsed(false);

        emailTokenRepository.persist(token);

        emailService.sendVerificationEmail(user.getEmail(), rawToken);

        return mapper.toResponse(user);
    }

    @Transactional
    public void applyDriver(Long userId, DriverApplyRequest request) {

        AppUser user = repository.findById(userId);

        if (user == null) {
            throw new NotFoundException("User tidak ditemukan");
        }

        Role customerRole = roleRepository.findByRoleName("CUSTOMER");

        if (!user.getRoles().contains(customerRole)) {
            throw new ConflictException("Hanya CUSTOMER yang bisa apply driver");
        }

        DriverProfile existing = driverRepository.findById(userId);

        if (existing != null) {

            switch (existing.getStatus()) {

                case PENDING ->
                    throw new ConflictException("Application masih PENDING");

                case APPROVED ->
                    throw new ConflictException("Sudah menjadi DRIVER");

                case REJECTED -> {
                    // Allow reapply
                    existing.setLicenseNumber(request.licenseNumber);
                    existing.setExperienceYears(request.experienceYears);
                    existing.setStatus(DriverStatus.PENDING);
                    existing.setRejectReason(null);
                    return;
                }

                case SUSPENDED ->
                    throw new ConflictException("Driver sedang SUSPENDED");
            }
        }

        DriverProfile profile = new DriverProfile();
        profile.setUser(user);
        profile.setLicenseNumber(request.licenseNumber);
        profile.setExperienceYears(request.experienceYears);
        profile.setStatus(DriverStatus.PENDING);

        driverRepository.persist(profile);
    }

    @Transactional
    public void approveDriver(Long userId) {

        DriverProfile profile = driverRepository.findById(userId);

        if (!verificationService.getMyVerification(userId).getStatus().equals(VerificationStatus.APPROVED)) {
            throw new DomainException("User belum KYC approved", 400);
        }

        if (profile == null) {
            throw new NotFoundException("Driver profile tidak ditemukan");
        }

        if (profile.getStatus() != DriverStatus.PENDING) {
            throw new ConflictException("Driver bukan dalam status PENDING");
        }

        profile.setStatus(DriverStatus.APPROVED);
        profile.setRejectReason(null);

        AppUser user = profile.getUser();

        Role driverRole = roleRepository.findByRoleName("DRIVER");

        if (driverRole == null) {
            throw new RuntimeException("Role DRIVER tidak ditemukan");
        }

        // Cek agar tidak duplicate role
        if (!user.getRoles().contains(driverRole)) {
            user.getRoles().add(driverRole);
        }

        UserVerification verification = verificationRepository.findById(userId);

        if (verification == null ||
                verification.getStatus() != VerificationStatus.APPROVED) {
            throw new DomainException("User belum terverifikasi", 403);
        }

    }

    public List<DriverAdminResponse> findAllDrivers() {
        List<DriverProfile> profiles = driverRepository.listAll();
        return driverMapper.toAdminResponseList(profiles);
    }

    @Transactional
    public void rejectDriver(Long userId, String reason) {

        DriverProfile profile = driverRepository.findById(userId);

        if (profile == null) {
            throw new NotFoundException("Driver profile tidak ditemukan");
        }

        if (profile.getStatus() != DriverStatus.PENDING) {
            throw new ConflictException("Hanya driver dengan status PENDING yang bisa direject");
        }

        profile.setStatus(DriverStatus.REJECTED);
        profile.setRejectReason(reason);
    }

    @Transactional
    public void reactivateDriver(Long userId) {

        DriverProfile profile = driverRepository.findById(userId);

        if (profile == null) {
            throw new NotFoundException("Driver profile tidak ditemukan");
        }

        if (profile.getStatus() != DriverStatus.SUSPENDED) {
            throw new ConflictException("Hanya driver SUSPENDED yang bisa direactivate");
        }

        profile.setStatus(DriverStatus.APPROVED);
        profile.setRejectReason(null);

    }

    public List<DriverAdminResponse> findPendingDrivers() {
        List<DriverProfile> drivers = driverRepository
                .find("status", DriverStatus.PENDING)
                .list();

        return driverMapper.toAdminResponseList(drivers);
    }

    @Transactional
    public void suspendDriver(Long userId) {

        DriverProfile profile = driverRepository.findById(userId);

        if (profile == null) {
            throw new NotFoundException("Driver profile tidak ditemukan");
        }

        if (profile.getStatus() != DriverStatus.APPROVED) {
            throw new ConflictException("Hanya driver APPROVED yang bisa disuspend");
        }

        profile.setStatus(DriverStatus.SUSPENDED);
    }

    public List<UserResponse> findAllUsers() {
        return repository.listAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public UserResponse findUserById(Long id) {
        AppUser user = repository.findById(id);

        if (user == null) {
            throw new NotFoundException("User tidak ditemukan");
        }

        return mapper.toResponse(user);
    }

    @Transactional
    public void deleteSelf(Long userId) {
        AppUser user = repository.findById(userId);

        if (user == null) {
            throw new NotFoundException("User tidak ditemukan");
        }

        repository.delete(user);
    }

    @Transactional
    public UserResponse updateSelf(Long userId, UserRequestUpdate requestUpdate) {

        AppUser user = repository.findById(userId);

        if (user == null) {
            throw new NotFoundException("User tidak ditemukan");
        }

        user.setPasswordHash(BcryptUtil.bcryptHash(requestUpdate.password));
        user.setNama(requestUpdate.nama);
        user.setNoHp(requestUpdate.noHp);

        return mapper.toResponse(user);
    }

    @Transactional
    public void deleteUserByAdmin(Long id, Long currentAdminId) {

        AppUser user = repository.findById(id);

        if (user == null) {
            throw new NotFoundException("User tidak ditemukan");
        }

        if (currentAdminId.equals(id)) {
            throw new ConflictException("Admin tidak bisa menghapus dirinya sendiri");
        }

        emailTokenRepository.delete("user.id", id);

        repository.delete(user);
    }

    public DriverProfile getMyDriverProfile(Long userId) {
        DriverProfile profile = driverRepository.findById(userId);

        if (profile == null) {
            throw new NotFoundException("Belum pernah apply driver");
        }

        return profile;
    }

    @Transactional
    public void forgotPassword(String email) {

        AppUser user = repository.find("email", email).firstResult();

        // IMPORTANT: jangan kasih tahu user ada atau tidak
        if (user == null)
            return;

        String rawToken = tokenUtil.generateRefreshToken();
        String hashed = tokenUtil.hash(rawToken);

        PasswordResetToken token = new PasswordResetToken();
        token.setTokenHash(hashed);
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        token.setUsed(false);

        passwordResetTokenRepository.persist(token);

        emailService.sendResetPasswordEmail(user.getEmail(), rawToken);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {

        String hashed = tokenUtil.hash(rawToken);

        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hashed);

        if (token == null)
            throw new NotFoundException("Token tidak valid");

        if (token.isUsed())
            throw new ConflictException("Token sudah digunakan");

        if (token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ConflictException("Token sudah expired");

        AppUser user = token.getUser();

        user.setPasswordHash(BcryptUtil.bcryptHash(newPassword));

        // revoke semua refresh token
        user.getRefreshTokens()
                .forEach(rt -> rt.setRevoked(true));

        token.setUsed(true);
    }

    public AppUser getCurrentUser() {

        Long userId = securityUtil.getCurrentUserId();

        AppUser user = repository.findById(userId);

        if (user == null)
            throw new WebApplicationException("User tidak ditemukan", 404);

        return user;
    }

    @Transactional
    public void assignRole(Long userId, String roleName) {


        AppUser user = repository.findById(userId);
        if (user == null) {
            throw new WebApplicationException("User tidak ditemukan", 404);
        }

        Role role = roleRepository.find("role", roleName.toUpperCase())
                .firstResult();

        if (role == null) {
            throw new WebApplicationException("Role tidak ditemukan", 404);
        }

        String normalizedRole = roleName.toUpperCase();

         if (normalizedRole.equals("SUPER_ADMIN")) {
            throw new WebApplicationException("Tidak bisa assign SUPER_ADMIN", 403);
        }

        // ROLE INTERNAL (single role only)
        if (isInternalRole(normalizedRole)) {
            user.getRoles().clear();
            user.getRoles().add(role);
            return;
        }

        // ROLE DRIVER (multi-role allowed)
        if (normalizedRole.equals("DRIVER")) {

            boolean alreadyDriver = user.getRoles().stream()
                    .anyMatch(r -> r.getRole().equalsIgnoreCase("DRIVER"));

            if (alreadyDriver) {
                throw new WebApplicationException("Sudah menjadi driver", 409);
            }

            user.getRoles().add(role);
        }
    }

    private boolean isInternalRole(String roleName) {
        return roleName.equals("SUPER_ADMIN")
                || roleName.equals("OPS_ADMIN")
                || roleName.equals("FINANCE_ADMIN");
    }

}
