
import { useLoginForm } from '../../hooks/useLoginForm';
import { loginUser } from '../../store/slices/authSlice';
import LoginPresenter from './LoginPresenter';
import { useAppDispatch, useAppSelector } from '../../store/hooks';
import { useNavigate } from 'react-router-dom';

const LoginContainer = () => {
    // 리덕스를 실행하기 위한 dispatch
    const dispatch = useAppDispatch();

    // 이동을 위한 함수
    const navigate = useNavigate();

    // 리덕스 스토어에서 로그인 관련 상태를 가져옴
    const { isLoading, error } = useAppSelector((state) => state.auth);

    // 입력 폼 관리를 위한 커스텀 훅 호출
    const { form, handleChange } = useLoginForm();

    // 폼 제출 버튼을 눌렀을 때 실행될 비즈니스 로직
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault(); // 브라우저 기본 동작(페이지 새로고침) 방지

        // 로그인 결과
        const resultAction = await dispatch(loginUser(form)); // 슬라이스에서 만든 비동기 Thunk 실행

        if (loginUser.fulfilled.match(resultAction)) {
            // 로그인 성공 시 메인 페이지로 이동
            navigate('/');
        }
    };

    // 최종적으로 Presenter에 모든 것을 주입
    return (
        <LoginPresenter
            form={form}
            onChange={handleChange}
            onSubmit={handleSubmit}
            isLoading={isLoading}
            error={error}
        />
    );
};

export default LoginContainer;
