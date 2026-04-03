import './App.css'
import { RouterProvider } from 'react-router-dom';
import root from './routers/root';


function App() {

  return (
      // RouterProvider에 만든 설정을 전달하여 앱에 라우터 적용
      <RouterProvider router={root} />
  );
}

export default App
