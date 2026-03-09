package websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class OrderSocketService {

    ObjectMapper mapper = new ObjectMapper();

    public void broadcastNewOrder(Long orderId, String pickup, String destination) {

        try {

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "NEW_ORDER");
            payload.put("orderId", orderId);
            payload.put("pickup", pickup);
            payload.put("destination", destination);

            String json = mapper.writeValueAsString(payload);

            OrderSocket.broadcast(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcastStatus(Long orderId, String status) {

        try {

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "ORDER_STATUS");
            payload.put("orderId", orderId);
            payload.put("status", status);

            String json = mapper.writeValueAsString(payload);

            OrderSocket.broadcast(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}