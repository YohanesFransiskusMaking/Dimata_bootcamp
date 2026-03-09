let socket: WebSocket | null = null;

export function connectSocket(onMessage: (data: any) => void) {

  socket = new WebSocket("ws://localhost:8080/ws/orders");

  socket.onopen = () => {
    console.log("WebSocket connected");
  };

  socket.onmessage = (event) => {

    const data = JSON.parse(event.data);

    onMessage(data);

  };

  socket.onerror = (error) => {
    console.error("WebSocket error", error);
  };

  socket.onclose = () => {
    console.log("WebSocket closed");
  };

}