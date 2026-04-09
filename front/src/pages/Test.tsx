import React, { useEffect, useState } from 'react';

// 상품 데이터 타입 정의 (백엔드 ProductListResponse 구조 반영)
interface Product {
    id: number;
    title: string;
    price: number;
    representativeImageUrl: string | null;
}

// 페이지 응답 타입 정의
interface ProductPageResponse {
    dtoList: Product[];
    first: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
    last: boolean;
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    totalPages: number;
}

const Test = () => {
    // 상태값 타입 지정
    const [products, setProducts] = useState<Product[]>([]);
    const [error, setError] = useState<string | null>(null);

    // fetch를 이용한 API 호출
    const fetchProducts = async () => {
        try {
            // 1. 상품 목록 API 호출
            const response = await fetch('http://localhost:8080/api/v1/product/list');

            // 2. HTTP 상태 코드가 200번대가 아닐 경우 에러 처리
            if (!response.ok) {
                throw new Error(`서버 응답 에러: ${response.status}`);
            }

            // 3. JSON 데이터로 파싱
            const data: ProductPageResponse = await response.json();
            setProducts(data.dtoList);
            console.log('fetch 성공:', data);
        } catch (err) {
            setError(err instanceof Error ? err.message : '네트워크 에러 발생');
            console.error('CORS 또는 연결 에러:', err);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, []);

    return (
        <div className="App">
            <p>Vite Port: 5173 ↔ Spring Port: 8080</p>
            <h1>상품 목록 테스트</h1>

            {error && (
                <div style={{ color: 'red', border: '1px solid red', padding: '10px' }}>
                    <strong>에러:</strong> {error}
                </div>
            )}

            <ul style={{ textAlign: 'left', listStyle: 'none', padding: 0 }}>
                {products.map((product) => (
                    <li
                        key={product.id}
                        style={{
                            marginBottom: '15px',
                            display: 'flex',
                            alignItems: 'center',
                            borderBottom: '1px solid #eee',
                            paddingBottom: '10px',
                        }}
                    >
                        {product.representativeImageUrl && (
                            <img
                                src={`http://localhost:8080${product.representativeImageUrl}`}
                                alt={product.title}
                                style={{
                                    width: '60px',
                                    height: '60px',
                                    marginRight: '15px',
                                    objectFit: 'cover',
                                    borderRadius: '4px',
                                }}
                            />
                        )}
                        <div>
                            <div style={{ fontWeight: 'bold', fontSize: '1.1em' }}>
                                {product.title}
                            </div>
                            <div style={{ color: '#666' }}>
                                {product.price.toLocaleString()}원
                            </div>
                        </div>
                    </li>
                ))}
                {products.length === 0 && !error && <li>데이터를 불러오는 중...</li>}
            </ul>
        </div>
    );
};

export default Test;
