import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

/* 
주입 받을 store 변수
*/
let store: any;

export const injectStore = (_store: any) => {
    store = _store;
};


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



// 요청 인터셉터 : 요청 직전에 스토어에서 최신 토큰을 꺼내 헤더에 삽입
authApi.interceptors.request.use(
    (config) => {
        const token = store?.getState()?.auth.accessToken;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);


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

                // 주입받은 store를 통해 updateToken 액션을 디스패치
                store.dispatch({ type: 'auth/updateToken', payload: accessToken });

                // 현재 실패했던 요청의 헤더만 새 토큰으로 교체
                originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;

                // 실패했던 원래 요청 재시도
                return authApi(originalRequest);
            } catch (reissueError) {
                // 재발급 실패 시(리프레시 토큰 만료) 로그인 페이지로 이동
                store.dispatch({ type: 'auth/logout' });
                window.location.href = '/login?message=expired';
                return Promise.reject(reissueError);
            }
        }
        return Promise.reject(error);
    },
);
