// src/pages/Home.tsx
import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { Link } from 'react-router-dom';

const Home = () => {
  const [books, setBooks] = useState<any[]>([]);
  const [keyword, setKeyword] = useState('');

  const fetchBooks = async (search = '') => {
    try {
      const endpoint = search ? `/books?keyword=${search}` : '/books';
      const response = await api.get(endpoint);
      setBooks(response.data.content);
    } catch (error) {
      console.error("Error fetching books", error);
    }
  };

  useEffect(() => {
    fetchBooks();
  }, []);

  return (
    <div className="container">
      <div className="search-bar">
        <input 
          type="text" 
          placeholder="Search for a book..." 
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <button onClick={() => fetchBooks(keyword)}>Search</button>
      </div>

      <div className="book-grid">
        {books.map((book) => (
          <div key={book.id} className="book-card">
            <h3>{book.title}</h3>
            <p className="author">{book.author}</p>
            <p className="price">${book.price}</p>
            <Link to={`/books/${book.id}`} className="btn">View Details</Link>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Home;