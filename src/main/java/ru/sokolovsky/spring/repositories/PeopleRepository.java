package ru.sokolovsky.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sokolovsky.spring.models.Book;
import ru.sokolovsky.spring.models.Person;
import java.util.List;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    List<Person> findByBookListIn(List<Book> list);
}
