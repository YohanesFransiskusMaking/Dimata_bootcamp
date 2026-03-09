package dto.response;

import java.math.BigDecimal;

public class EstimateOrderResponse {

    public BigDecimal distanceKm;
    public BigDecimal estimatedPrice;

    public EstimateOrderResponse(BigDecimal distanceKm, BigDecimal estimatedPrice) {
        this.distanceKm = distanceKm;
        this.estimatedPrice = estimatedPrice;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public EstimateOrderResponse() {
    }

    


}