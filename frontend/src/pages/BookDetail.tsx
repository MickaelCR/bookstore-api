// src/pages/BookDetail.tsx
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

const BookDetail = () => {
  const { id } = useParams();
  const [book, setBook] = useState<any>(null);
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    api.get(`/books/${id}`).then(res => setBook(res.data)).catch(console.error);
  }, [id]);

  const addToCart = async () => {
    if (!isAuthenticated) return navigate('/login');
    try {
      await api.post('/cart/items', { bookId: book.id, quantity: 1 });
      alert('Added to cart!');
    } catch (error) {
      alert('Error adding to cart');
    }
  };

  if (!book) return <div>Loading...</div>;

  return (
    <div className="container detail-page">
      <h1>{book.title}</h1>
      <h3>{book.author}</h3>
      <p className="price">${book.price}</p>
      <p>{book.summary}</p>
      <p>ISBN: {book.isbn}</p>
      <button onClick={addToCart} className="btn-primary">Add to Cart</button>
      
      {/* Ici, on pourrait ajouter le composant Reviews */}
    </div>
  );
};

export default BookDetail;