package dto.response;

import java.time.LocalDateTime;
import entity.VerificationStatus;

public class AdminVerificationItem {

    public Long userId;
    public String userName;
    public VerificationStatus status;
    public String documentType;
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime createdAt;
}
