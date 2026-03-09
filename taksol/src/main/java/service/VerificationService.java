package service;

import java.time.LocalDateTime;

import entity.AppUser;
import entity.UserVerification;
import entity.VerificationStatus;
import exception.ConflictException;
import exception.DomainException;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import repository.AppUserRepository;
import repository.UserVerificationRepository;

@ApplicationScoped
public class VerificationService {

    @Inject
    AppUserRepository userRepository;
    @Inject
    UserVerificationRepository verificationRepository;

    @Transactional
    public UserVerification submitVerification(Long userId,
            String documentType,
            String documentPath) {

        if (documentType == null || documentType.length() > 50)
            throw new DomainException("documentType tidak valid", 400);

        if (documentPath == null || documentPath.length() > 255)
            throw new DomainException("documentPath tidak valid", 400);

        AppUser user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new DomainException("User tidak ditemukan", 404));

        boolean isCustomer = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> "CUSTOMER".equals(r.getRole()));
        if (!isCustomer) {
            throw new DomainException("Hanya customer yang bisa verifikasi", 403);
        }

        if (user.isDeleted())
            throw new DomainException("User tidak ditemukan", 404);

        UserVerification verification = verificationRepository.findById(userId);

        if (verification == null) {
            verification = new UserVerification();
            verification.setUser(user);
            verification.setStatus(VerificationStatus.PENDING);
            verification.setDocumentType(documentType);
            verification.setDocumentPath(documentPath);
            verificationRepository.persist(verification);
            return verification;
        }

        switch (verification.getStatus()) {

            case PENDING ->
                throw new DomainException("Verification masih PENDING", 409);

            case APPROVED ->
                throw new DomainException("Verification sudah APPROVED", 409);

            case REJECTED -> {
                verification.setDocumentType(documentType);
                verification.setDocumentPath(documentPath);
                verification.setStatus(VerificationStatus.PENDING);
                verification.setRejectedReason(null);
                verification.setVerifiedAt(null);
                verification.setVerifiedBy(null);
                return verification;
            }
        }

        throw new DomainException("Status tidak valid", 409);
    }

    @Transactional
    public void approveVerification(Long userId, Long adminId) {

        UserVerification verification = verificationRepository.findById(userId);

        if (verification == null)
            throw new NotFoundException("Verification tidak ditemukan");

        if (verification.getStatus() != VerificationStatus.PENDING)
            throw new ConflictException("Status tidak dapat diproses");

        verification.setStatus(VerificationStatus.APPROVED);
        verification.setVerifiedAt(LocalDateTime.now());
        verification.setVerifiedBy(adminId);
    }

    public UserVerification getMyVerification(Long userId) {

        UserVerification verification = verificationRepository.findById(userId);

        if (verification == null)
            throw new NotFoundException("Verification tidak ditemukan");

        return verification;
    }

    @Transactional
    public void rejectVerification(Long userId,
            Long adminId,
            String reason) {

        if (reason == null || reason.isBlank())
            throw new BadRequestException("Reason wajib diisi");

        UserVerification verification = verificationRepository.findById(userId);

        if (verification == null)
            throw new NotFoundException("Verification tidak ditemukan");

        if (verification.getStatus() != VerificationStatus.PENDING)
            throw new ConflictException("Status tidak dapat diproses");

        verification.setStatus(VerificationStatus.REJECTED);
        verification.setRejectedReason(reason);
        verification.setVerifiedAt(LocalDateTime.now());
        verification.setVerifiedBy(adminId);
    }

    public PanacheQuery<UserVerification> listAll() {
        return verificationRepository.findAll(Sort.by("createdAt").descending());
    }

}
