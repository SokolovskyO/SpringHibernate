package ru.sokolovsky.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sokolovsky.spring.models.Book;
import ru.sokolovsky.spring.models.Person;
import ru.sokolovsky.spring.services.BooksService;
import ru.sokolovsky.spring.services.PeopleService;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final PeopleService peopleService;

    private final BooksService booksService;

    @Autowired
    public BookController(PeopleService peopleService, BooksService booksService) {
        this.peopleService = peopleService;
        this.booksService = booksService;
    }

    @GetMapping()
    public String index(Model model,
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false, defaultValue = "10") Integer size,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {

        if (page == null || size == null)
            model.addAttribute("addedAttribute", booksService.findAllByPage(sortByYear));
        else
            model.addAttribute("addedAttribute", booksService.findWithPagination(page, size, sortByYear));

        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        List<Person> personList = peopleService.findPersonByBookList(booksService.findAllById(id));

        if (!personList.isEmpty()) {
            model.addAttribute("owner", personList.get(0));

        } else
            model.addAttribute("people", peopleService.findAll());

        model.addAttribute("book", booksService.findOne(id));

        return "books/profile";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {

        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";

        booksService.save(book);
        return "redirect:/books";
    }


    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findOne(id));

        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "books/edit";

        booksService.update(book, id);
        return "redirect:/books";
    }

    @PatchMapping("{id}/add")
    public String chooseBook(@ModelAttribute("person") Person person, @PathVariable("id") int id) {
        booksService.setOwner(person, id);

        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);

        return "redirect:/books";
    }

    @PatchMapping("{id}/release")
    public String releaseBook(@PathVariable("id") int id) {
        booksService.bookRelease(id);

        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String getSearch() {

        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String txt) {
        model.addAttribute("books", booksService.findByStartsWith(txt));

        return "books/search";
    }
}

