import { createAsyncThunk, createSlice, type PayloadAction } from '@reduxjs/toolkit';
import { authApi } from '../../apis/index';
import axios from 'axios';
import type { LoginData } from './../../types/auth';


/* 
[Interface] AuthState
리덕스 스토어의 auth 슬라이스에서 관리할 데이터의 '설계도'
*/
interface AuthState {
    // 서버에서 발급받은 JWT accessToken
    accessToken: string | null;

    // 로그인한 사용자 상세 정보
    user: {
        email: string;
        nickname: string;
        name: string;
        address: string;
        detailAddress: string;
    } | null;

    // 현재 로그인 여부
    isAuthenticated: boolean;

    // API 요청이 진행 중인지 나타내는 상태
    isLoading: boolean;

    // 로그인 실패 시 에러 메세지
    error: string | null;
}

/* 
앱 처음 실행시, 로그아웃 시 가지게 될 '기본값'
위의 AuthState 인터페이스 형식과 동일
*/
const initialState: AuthState = {
    accessToken: null,
    user: null,
    isAuthenticated: false,
    isLoading: false,
    error: null,
};


/**
   [Thunk] reissueToken
   새로고침 시 HttpOnly 쿠키에 담긴 RefreshToken을 이용해 AccessToken을 복구
 */
export const reissueToken = createAsyncThunk(
    'auth/reissueToken',
    async (_, { dispatch, rejectWithValue }) => {
        try {
            const response = await authApi.post('/v1/auth/reissue');
            const { accessToken } = response.data;
            
            // 유저 정보 가져오기 호출
            dispatch(fetchUserInfo());
            
            return accessToken;
        } catch (error) {
            return rejectWithValue('세션 만료');
        }
    }
);


/* 
[비동기 Thunk] 로그인한 사용자의 정보
*/
export const fetchUserInfo = createAsyncThunk(
    'auth/fetchUserInfo',
    async (_, { rejectWithValue }) => {
        try {
            const response = await authApi.get('/v1/users/me');
            return response.data;
        } catch (error: any) {
            return rejectWithValue(
                error.response?.data?.message || '유저 정보 로드 실패',
            );
        }
    },
);



/* 
[비동기 Thunk] 로그인
서버에 로그인 요청, 응답 (토큰, 유저 정보)을 받아오는 비동기 로직
*/
export const loginUser = createAsyncThunk(
    'auth/loginUser',
    async (loginData: LoginData, thunkAPI) => {
        try {
            // 서버의 /login 엔드포인트로 POST 요청 전송
            const response = await authApi.post('/v1/auth/login', loginData);

            // 로그인 성공 후 사용자 정보 가져오기
            thunkAPI.dispatch(fetchUserInfo());

            return response.data;
        } catch (error: unknown) {
            if (axios.isAxiosError(error)) {
                // 이제 여기서 error는 자동으로 AxiosError 타입이 됩니다.
                const message = error.response?.data?.message || '로그인에 실패했습니다.';
                return thunkAPI.rejectWithValue(message);
            }
            // Axios 에러가 아닌 일반 에러 처리
            return thunkAPI.rejectWithValue('네트워크 연결이 원활하지 않습니다.');
        }
    },
);

/* 
[Slice] authSlice
인증과 관련된 상태 변경 로직들을 하나로 모은 슬라이스
*/
const authSlice = createSlice({
    name: 'auth',
    initialState,

    /* 
    [동기 Reducers] 
    서버 통신 없이 즉각적인 상태 변경이 필요한 액션들
    */
    reducers: {
        /* 
        로그아웃
        리덕스 내의 인증 정보를 초기화
        사용자를 로그아웃 상태로 만듦
        */
        logout: (state) => {
            state.accessToken = null;
            state.user = null;
            state.isAuthenticated = false;
            state.error = null;
        },

        /* 
        토큰 재발급
        인터셉터 등을 통해 재발급받은 새로운 accessToken으로 상태를 업데이트
        */
        updateToken: (state, action: PayloadAction<string>) => {
            state.accessToken = action.payload;
        },
    },

    /*
    비동기 ExtraReducers]
    loginUser Thunk의 생명주기(대기, 성공, 실패)에 따른 상태 처리
    */
    extraReducers: (builder) => {
        builder
            /* 
            loginUser.pending
            로그인 요청
            */
            .addCase(loginUser.pending, (state) => {
                state.isLoading = true;
                state.error = null;
            })
            /*
            loginUser.fulfilled
            로그인 성공
            */
            .addCase(loginUser.fulfilled, (state, action) => {
                state.isLoading = false;
                state.accessToken = action.payload.accessToken;
                state.isAuthenticated = true;
            })
            /* 
            loginUser.rejected
            로그인 실패
            */
            .addCase(loginUser.rejected, (state, action) => {
                state.isLoading = false;
                state.error = action.payload as string;
                state.isAuthenticated = false;
            })

            // --- reissueToken (새로고침 시 실행) ---
            .addCase(reissueToken.fulfilled, (state, action) => {
                state.accessToken = action.payload;
                state.isAuthenticated = true;
                state.isLoading = false; // 복구 완료
            })
            .addCase(reissueToken.rejected, (state) => {
                state.accessToken = null;
                state.isAuthenticated = false;
                state.isLoading = false; // 복구 시도 끝 (로그인 안 된 상태)
            })

            /* 
            사용자 정보 가져오기 성공
            */
            .addCase(fetchUserInfo.fulfilled, (state, action) => {
                state.user = action.payload;
            })
            /* 
            사용자 정보 가져오기 실패
            */
            .addCase(fetchUserInfo.rejected, (state, action) => {
                state.error = action.payload as string;
                state.isAuthenticated = false;
                state.isLoading = false;
            });
    },
});

// 컴포넌트에서 사용할 동기 액션들을 내보냄
export const { logout, updateToken } = authSlice.actions;

// 리듀서를 내보냄
export default authSlice.reducer;
