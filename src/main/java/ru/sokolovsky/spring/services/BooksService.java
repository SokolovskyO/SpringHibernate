package ru.sokolovsky.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sokolovsky.spring.models.Book;
import ru.sokolovsky.spring.models.Person;
import ru.sokolovsky.spring.repositories.BooksRepository;
import ru.sokolovsky.spring.repositories.PeopleRepository;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    private final PeopleRepository peopleRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository, PeopleRepository peopleRepository) {
        this.booksRepository = booksRepository;
        this.peopleRepository = peopleRepository;
    }

    public List<Book> findByStartsWith(String query) {
        return booksRepository.findByBookNameStartingWith(query);
    }

    public List<Book> findAllByPage(boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(Sort.by("year"));
        else
            return booksRepository.findAll();
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public Book findOne(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);

        return foundBook.orElse(null);
    }

    public List<Book> findAllById(int id) {
        List<Book> foundBook = booksRepository.findAllById(Collections.singleton(id));

        return foundBook;
    }

    @Transactional
    public void update(Book updatedBook, int id) {
        updatedBook.setBook_id(id);
        updatedBook.setOwner(updatedBook.getOwner()); // чтобы не терялась связь при обновлении
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void setOwner(Person person, int id) {
        Person personById = peopleRepository.findById(person.getId()).orElse(null);
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(personById);
                    book.setDate(new Date()); // текущее время
                });
    }

    @Transactional
    public void bookRelease(int id) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setDate(null);
                });
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }
}
