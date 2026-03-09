import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

// Request interceptor
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  const deviceId = localStorage.getItem("deviceId");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  if (deviceId) {
    config.headers["X-Device-Id"] = deviceId;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Jika 401 dan belum pernah retry
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem("refreshToken");
        const deviceId = localStorage.getItem("deviceId");

        const res = await axios.post(
          "http://localhost:8080/auth/refresh",
          {
            refreshToken,
            deviceId,
          }
        );

        const newAccessToken = res.data.accessToken;
        const newRefreshToken = res.data.refreshToken;

        localStorage.setItem("token", newAccessToken);
        localStorage.setItem("refreshToken", newRefreshToken);

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        return api(originalRequest);
      } catch (err) {
        localStorage.clear();
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default api;