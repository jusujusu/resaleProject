import { useState } from 'react';
import './Category.css';

interface CategoryResponse {
    id: number;
    name: string;
    sortOrder: number;
    children: CategoryResponse[];
}

const Category = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [categories, setCategories] = useState<CategoryResponse[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const toggleMenu = () => {
        setIsOpen(!isOpen);
        if (!isOpen && categories.length === 0) {
            fetchCategories();
        }
    };

    const fetchCategories = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await fetch('http://localhost:8080/api/v1/category');
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            const data: CategoryResponse[] = await response.json();
            setCategories(data);
        } catch (err) {
            console.error('Failed to fetch categories:', err);
            setError('카테고리를 불러오는데 실패했습니다.');
        } finally {
            setIsLoading(false);
        }
    };

    // Recursive component to render category items
    const renderCategoryItems = (items: CategoryResponse[]) => {
        return items.map((item) => (
            <li key={item.id} className="category-item">
                <a href={`/category/${item.id}`} className="category-link">
                    {item.name}
                </a>
                {item.children && item.children.length > 0 && (
                    <ul className="children-list">
                        {renderCategoryItems(item.children)}
                    </ul>
                )}
            </li>
        ));
    };

    return (
        <div className="category-container">
            <button className="category-button" onClick={toggleMenu}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <line x1="3" y1="12" x2="21" y2="12"></line>
                    <line x1="3" y1="6" x2="21" y2="6"></line>
                    <line x1="3" y1="18" x2="21" y2="18"></line>
                </svg>
                카테고리
            </button>

            <div className={`category-dropdown ${isOpen ? 'open' : ''}`}>
                {isLoading ? (
                    <div className="loading-text">불러오는 중...</div>
                ) : error ? (
                    <div className="error-text">{error}</div>
                ) : (
                    <ul className="category-list">
                        {renderCategoryItems(categories)}
                        {categories.length === 0 && !isLoading && (
                            <li className="loading-text">카테고리가 없습니다.</li>
                        )}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default Category;
