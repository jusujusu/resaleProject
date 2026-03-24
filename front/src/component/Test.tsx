import React, { useEffect, useState } from 'react';

// 테스트 데이터 타입 정의
interface User {
    id: number;
    email: string;
    nickName: string;
}

const Test = () => {
    // 상태값 타입 지정
    const [users, setUsers] = useState<User[]>([]);
    const [error, setError] = useState<string | null>(null);

    // fetch를 이용한 API 호출
    const fetchUsers = async () => {
        try {
            // 1. fetch 호출 (기본값은 GET입니다)
            const response = await fetch('http://localhost:8080/api/v1/user/all');

            // 2. HTTP 상태 코드가 200번대가 아닐 경우 에러 처리
            if (!response.ok) {
                throw new Error(`서버 응답 에러: ${response.status}`);
            }

            // 3. JSON 데이터로 파싱
            const data: User[] = await response.json();
            setUsers(data);
            console.log('fetch 성공:', data);
        } catch (err) {
            setError(err instanceof Error ? err.message : '네트워크 에러 발생');
            console.error('CORS 또는 연결 에러:', err);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

  return (
      <div className="App">
          <h1>CORS 테스트 (Native fetch)</h1>
          <p>Vite Port: 5173 ↔ Spring Port: 8080</p>

          {error && (
              <div style={{ color: 'red', border: '1px solid red', padding: '10px' }}>
                  <strong>에러:</strong> {error}
              </div>
          )}

          <ul style={{ textAlign: 'left', display: 'inline-block' }}>
              {users.map((user) => (
                  <li key={user.id}>
                      {user.nickName} ({user.email})
                  </li>
              ))}
              {users.length === 0 && !error && <li>데이터를 불러오는 중...</li>}
          </ul>
      </div>
  );
};

export default Test;
