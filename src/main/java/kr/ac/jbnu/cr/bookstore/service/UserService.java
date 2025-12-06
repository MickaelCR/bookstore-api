package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.dto.request.UserUpdateRequest;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.model.User;
import kr.ac.jbnu.cr.bookstore.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find user by ID
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    /**
     * Find all users (paginated)
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Find active users (paginated)
     */
    public Page<User> findAllActive(Pageable pageable) {
        return userRepository.findByIsActiveTrue(pageable);
    }

    /**
     * Search users by username
     */
    public Page<User> searchByUsername(String username, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }

    /**
     * Update user profile
     */
    @Transactional
    public User update(Long id, UserUpdateRequest request) {
        User user = findById(id);

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        return userRepository.save(user);
    }

    /**
     * Deactivate user (admin only)
     */
    @Transactional
    public User deactivate(Long id) {
        User user = findById(id);
        user.setIsActive(false);
        return userRepository.save(user);
    }

    /**
     * Activate user (admin only)
     */
    @Transactional
    public User activate(Long id) {
        User user = findById(id);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    /**
     * Check if user exists by ID
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}