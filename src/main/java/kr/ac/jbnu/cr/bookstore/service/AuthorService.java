package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.dto.request.AuthorRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.AuthorResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.PageResponse;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.model.Author;
import kr.ac.jbnu.cr.bookstore.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "authors", key = "#pageable.pageNumber")
    public PageResponse<AuthorResponse> getAllAuthors(String name, Pageable pageable) {
        Page<Author> page;
        if (name != null && !name.isBlank()) {
            page = authorRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = authorRepository.findAll(pageable);
        }

        List<AuthorResponse> content = page.getContent().stream()
                .map(AuthorResponse::from)
                .collect(Collectors.toList());

        return PageResponse.of(page, content);
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return AuthorResponse.from(author);
    }

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());
        author.setBio(request.getBio());
        author.setBirthDate(request.getBirthDate());
        return AuthorResponse.from(authorRepository.save(author));
    }

    @Transactional
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));

        author.setName(request.getName());
        author.setBio(request.getBio());
        author.setBirthDate(request.getBirthDate());

        return AuthorResponse.from(authorRepository.save(author));
    }

    @Transactional
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }
}