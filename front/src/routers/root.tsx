/**
 * root 컴포넌트
 *
 * 목적:
 * - 전체 라우팅 구조를 정의
 * - 모든 하위 라우터들을 한 곳에서 통합 관리
 *
 * 주요 기능:
 * - React Router v6의 createBrowserRouter 함수를 사용해 라우팅 구조 정의
 */

import { createBrowserRouter } from "react-router-dom"
import BasicLayout from "../component/layout/BasicLayout"
import MainPage from "../pages/MainPage";
import Test from "../pages/Test";
import LoginPage from "../pages/Login";


const root = createBrowserRouter([
    {
        path: '',
        element: <BasicLayout />,
        children: [
            {
                // "" 로 접속시 메인 페이지로 이동
                index: true,
                element: <MainPage />,
            },
            {
                path: 'login',
                element: <LoginPage />,
            },
            {
                // TODO 예시 페이지 이므로 나중에 삭제
                path: 'test',
                element: <Test />,
            },
        ],
    },
]);

export default root