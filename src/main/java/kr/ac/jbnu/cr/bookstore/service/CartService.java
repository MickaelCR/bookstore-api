package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.dto.request.CartItemRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.CartItemUpdateRequest;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.model.Book;
import kr.ac.jbnu.cr.bookstore.model.Cart;
import kr.ac.jbnu.cr.bookstore.model.CartItem;
import kr.ac.jbnu.cr.bookstore.model.User;
import kr.ac.jbnu.cr.bookstore.repository.BookRepository;
import kr.ac.jbnu.cr.bookstore.repository.CartItemRepository;
import kr.ac.jbnu.cr.bookstore.repository.CartRepository;
import kr.ac.jbnu.cr.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get cart for user (create if not exists)
     */
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

                    Cart cart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(cart);
                });
    }

    /**
     * Get cart for user
     */
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
    }

    /**
     * Add item to cart
     */
    @Transactional
    public Cart addItem(Long userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        Book book = bookRepository.findByIdAndIsActiveTrue(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", request.getBookId()));

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId());

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            // Add new item
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(request.getQuantity())
                    .build();

            cart.addItem(item);
            cartItemRepository.save(item);
        }

        return cartRepository.findByUserId(userId).orElse(cart);
    }

    /**
     * Update cart item quantity
     */
    @Transactional
    public Cart updateItem(Long userId, Long itemId, CartItemUpdateRequest request) {
        Cart cart = getCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", itemId));

        // Check ownership
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem", itemId);
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return cartRepository.findByUserId(userId).orElse(cart);
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = getCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", itemId));

        // Check ownership
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("CartItem", itemId);
        }

        cart.removeItem(item);
        cartItemRepository.delete(item);

        return cartRepository.findByUserId(userId).orElse(cart);
    }

    /**
     * Clear all items from cart
     */
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.clearItems();
        cartItemRepository.deleteByCartId(cart.getId());
    }

    /**
     * Check if cart is empty
     */
    public boolean isCartEmpty(Long userId) {
        Optional<Cart> cart = cartRepository.findByUserId(userId);
        return cart.isEmpty() || cart.get().getItems().isEmpty();
    }
}