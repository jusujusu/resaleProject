import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import App from './App.tsx';
import root from './routers/root.tsx';
import { Provider } from 'react-redux';
import { store } from './store/index.ts';

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <Provider store={store}>
        {/* RouterProvider에 만든 설정을 전달하여 앱에 라우터 적용 */}
        <RouterProvider router={root} />
        <App />
        </Provider>
    </StrictMode>,
);
