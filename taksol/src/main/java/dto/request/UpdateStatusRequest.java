package dto.request;

import entity.KendaraanStatus;

public class UpdateStatusRequest {
    private KendaraanStatus status;

    public KendaraanStatus getStatus() {
        return status;
    }

    public void setStatus(KendaraanStatus status) {
        this.status = status;
    }
}

