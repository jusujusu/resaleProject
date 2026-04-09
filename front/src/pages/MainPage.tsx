import { useAppSelector } from '../store/hooks';


const MainPage = () => {

  // 로그인한 상태 
  const { user, isAuthenticated } = useAppSelector((state) => state.auth);
   
  return (
      <div className="p-8">
          <h1 className="text-2xl font-bold mb-4">중고 마켓 메인 페이지</h1>

          {isAuthenticated ? (
              <div className="bg-blue-50 p-4 rounded-lg">
                  <p className="text-blue-700 font-medium">
                      👋 안녕하세요,{' '}
                      <span className="font-bold text-blue-900">
                          {user?.nickname || user?.email}
                      </span>
                      님!
                  </p>
                  <p className="text-sm text-gray-500 mt-1">
                      오늘도 좋은 물건을 찾아보세요.
                  </p>
              </div>
          ) : (
              <div className="bg-gray-50 p-4 rounded-lg">
                  <p className="text-gray-600">로그인이 필요한 서비스입니다.</p>
              </div>
          )}
      </div>
  );
}

export default MainPage