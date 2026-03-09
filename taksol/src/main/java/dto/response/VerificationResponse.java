package dto.response;

import entity.VerificationStatus;
import java.time.LocalDateTime;

public class VerificationResponse {

    public Long userId;
    public VerificationStatus status;
    public String documentType;
    public String documentPath;
    public String rejectedReason;
    public LocalDateTime verifiedAt;
    public Long verifiedBy;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Long getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(Long verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static VerificationResponse fromEntity(entity.UserVerification entity) {
        VerificationResponse resp = new VerificationResponse();
        resp.setUserId(entity.getUser().getId());
        resp.setStatus(entity.getStatus());
        resp.setDocumentType(entity.getDocumentType());
        resp.setDocumentPath(entity.getDocumentPath());
        resp.setRejectedReason(entity.getRejectedReason());
        resp.setVerifiedAt(entity.getVerifiedAt());
        resp.setVerifiedBy(entity.getVerifiedBy());
        resp.setCreatedAt(entity.getCreatedAt());
        resp.setUpdatedAt(entity.getUpdatedAt());
        return resp;
    }

}
