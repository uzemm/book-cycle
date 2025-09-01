// axiosInstance.ts
import axios, { AxiosInstance } from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080", // 네 API 서버 주소
  withCredentials: true, // 필요하면
});

// 요청 인터셉터 (요청마다 accessToken 헤더 자동 세팅)
axiosInstance.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem("accessToken");
    if (accessToken && config.headers) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터 (401 → refresh 처리)
axiosInstance.interceptors.response.use(
  (response) => response, // 성공 응답은 그대로 반환
  async (error) => {
    const originalRequest = error.config;

    // accessToken 만료 (401 Unauthorized)
    if (
      error.response?.status === 401 &&
      !originalRequest._retry // 무한 루프 방지 플래그
    ) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) {
          // refreshToken 없으면 로그인으로 튕김
          window.location.href = "/login";
          return Promise.reject(error);
        }

        // refresh 요청
        const res = await axios.post("/auth/refresh", { refreshToken });
        const newAccessToken = res.data.accessToken;

        // 새 accessToken 저장
        localStorage.setItem("accessToken", newAccessToken);

        // axios 기본 헤더 업데이트
        axiosInstance.defaults.headers.common[
          "Authorization"
        ] = `Bearer ${newAccessToken}`;

        // 원래 요청 다시 실행
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return axiosInstance(originalRequest);
      } catch (err) {
        // refresh 실패 → 로그인 페이지로
        localStorage.clear();
        window.location.href = "/login";
        return Promise.reject(err);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
