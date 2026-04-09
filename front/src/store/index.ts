import { configureStore } from '@reduxjs/toolkit';
import { logger } from 'redux-logger';
import authReducer from './slices/authSlice';
import { injectStore } from '../apis';




export const store = configureStore({
    reducer: {
        // 등록된 slice의 리듀서들
        // state.~~ 로 접근 가능하게 됨
        auth: authReducer,
    },

    // 기본 미들웨어 설정 및 사용자 정의 미들웨어(logger) 연결
    middleware: (getDefaultMiddleware) => {
        // 기본 미들웨어 설정
        const middleware = getDefaultMiddleware({
            serializableCheck: false,
        });

        // 환경 변수 체크 (Vite 전용 및 일반 Node 환경 모두 체크)
        const isDev = import.meta.env?.DEV || process.env.NODE_ENV === 'development';

        // 개발 환경일 때만 logger를 추가한 배열을 반환
        if (isDev) {
            return middleware.concat(logger as any);
        }

        // 개발 환경이 아닐 때도 반드시 기본 미들웨어 배열을 반환해야 함!
        return middleware;
    },
});

// 스토어 생성 직후 주입
injectStore(store);

// RootState: 스토어의 전체 상태 타입을 추출 (useAppSelector에서 사용)
export type RootState = ReturnType<typeof store.getState>;

// AppDispatch: 스토어의 디스패치 함수 타입을 추출 (useAppDispatch에서 사용)
export type AppDispatch = typeof store.dispatch;

