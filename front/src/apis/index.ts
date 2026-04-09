import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

/* 
로그인이 필요없는 일반 요청용
*/
export const client = axios.create({
    baseURL: BASE_URL,
});

/* 
로그인이 필요한 요청용
*/
export const authApi = axios.create({
    baseURL: BASE_URL,
    withCredentials: true, // 쿠키(refreshToken)를 함께 보내기 위해 필수
});


// 응답 인터셉터 : 401 에러 발생시 자동으로 재발급 시도
authApi.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;

        // 401 에러이고 재시도한 적이 없을 때만 실행
        if (error.response?.status == 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                // 재발급 요청
                const res = await axios.post(
                    `${BASE_URL}/v1/auth/reissue`,
                    {},
                    { withCredentials: true },
                );
                const { accessToken } = res.data;

                // 전역 헤더 업데이트 및 현재 요청 헤더 수정
                authApi.defaults.headers.common['Authorization'] =
                    `Bearer ${accessToken}`;
                originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;

                // 실패했던 원래 요청 재시도
                return authApi(originalRequest);
            } catch (reissueError) {
                // 재발급 실패 시(리프레시 토큰 만료) 로그인 페이지로 이동
                window.location.href = '/login?message=expired';
                return Promise.reject(reissueError);
            }
        }
        return Promise.reject(error);
    },
);
