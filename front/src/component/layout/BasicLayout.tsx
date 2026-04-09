import { Outlet } from 'react-router-dom';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { reissueToken } from '../../store/slices/authSlice';
import type { RootState } from '../../store/index';


const BasicLayout = () => {
  const dispatch = useDispatch();
  const { isAuthenticated, isLoading } = useSelector((state: RootState) => state.auth);

  useEffect(() => {
    // accessToken이 없더라도 refreshToken cookie
    if (!isAuthenticated && !isLoading) {
      dispatch(reissueToken() as any);
    }
  }, [dispatch, isAuthenticated, isLoading]);
  return (
      <div className="min-h-screen flex flex-col">
          {/* 1. 공통 헤더 영역 */}
          <header className="bg-blue-500 text-white p-4">
              <nav>
                  <ul className="flex gap-4">
                      <li>공통 헤더</li>
                  </ul>
              </nav>
          </header>

          {/* 2. 실제 페이지 내용이 갈아 끼워지는 영역 */}
          <main className="flex-grow p-6">
              {/* Outlet은 자식 라우트(MainPage 등)가 렌더링되는 지점입니다. */}
              <Outlet />
          </main>

          {/* 3. 공통 푸터 영역 */}
          <footer className="bg-gray-200 p-4 text-center">
             공통 푸터
          </footer>
      </div>
  );
}

export default BasicLayout