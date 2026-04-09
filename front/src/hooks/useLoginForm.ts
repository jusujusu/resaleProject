import { useState } from 'react';
import type { LoginData } from '../types/auth';


export const useLoginForm = () => {
    // 이메일, 비밀번호 담은 상태
    const [form, setForm] = useState<LoginData>({
        email: '',
        password: '',
    });

    // 입력값이 변경될 때 핸들러
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        // 기존 form 객체 복사, 변경된 필드(name)만 새 값(value)으로 덮어씌움
        setForm({
            ...form,
            [name]: value,
        });
    };

    // 컨테이너에서 사용할 데이터, 함수 반환
    return { form, handleChange };
};
