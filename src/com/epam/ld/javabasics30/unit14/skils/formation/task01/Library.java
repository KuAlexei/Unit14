package com.epam.ld.javabasics30.unit14.skils.formation.task01;

import com.epam.ld.javabasics30.unit14.skils.Author;
import com.epam.ld.javabasics30.unit14.skils.Book;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Library {
    private static final Logger LOG = LoggerFactory.getLogger(Library.class);

    public static final Comparator<Book> COMPARATOR_AUTHORS_CONTAINS = (book1, book2) -> book1.getAuthors().containsAll(book2.getAuthors()) ? 0 : -1;
    public static final Comparator<Book> COMPARATOR_TITLE_STARTS_WITH = (book1, book2) -> book1.getTitle().startsWith(book2.getTitle()) ? 0 : -1;
    public static final Comparator<Book> COMPARATOR_TITLE_COMPARE_IGNORE_CASE = Comparator.comparing(book -> book.getTitle().toLowerCase());

    private static final String TITLE = "Title";
    private static final String AUTHORS = "Authors";
    private static final String PUBLISHING_YEAR = "PublishingYear";
    private static final String PRICE = "Price";

    private static final String AUTHOR_NAME = "Name";
    private static final String AUTHOR_SURNAME = "Surname";
    private static final String AUTHOR_MIDDLENAME = "Middlename";
    private static final String AUTHOR_BIRTHDAY = "Birthday";

    private static final String AUTHOR_RECORD_SEPARATOR = "|";

    private static final CSVFormat AUTHOR_CSV_FORMAT = CSVFormat.DEFAULT
            .withHeader(AUTHOR_NAME, AUTHOR_SURNAME, AUTHOR_MIDDLENAME, AUTHOR_BIRTHDAY);

    private static final CSVFormat LIBRARY_CSV_FORMAT = CSVFormat.DEFAULT
            .withHeader(TITLE, AUTHORS, PUBLISHING_YEAR, PRICE)
            .withFirstRecordAsHeader();

    private static final SimpleDateFormat AUTHOR_BIRTHDAY_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    private List<Book> books = new ArrayList<>(); // В условии список, возможно уместнее было бы использовать Set

    public Library() {
    }

    public Library add(Book book) {
        books.add(book);
        return this;
    }

    public Library add(Library library) {
        books.addAll(library.books);
        return this;
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public Library remove(Book book) {
        if (books.remove(book)) {
            LOG.warn("Book {} has not been found.", book);
        } else {
            LOG.info("Book {} has been removed.", book);
        }
        return this;
    }

    public Library removeAll(Library library) {
        for (Book book : library.books) {
            remove(book);
        }
        return this;
    }

    public Book getFirstBookByAuthor(Author author) {
        HashSet<Author> authors = new HashSet<>();
        authors.add(author);
        return getFirstBookByAuthor(authors);
    }

    public Library getBooksByAuthor(Author author) {
        HashSet<Author> authors = new HashSet<>();
        authors.add(author);
        return getBooksByAuthor(authors);
    }

    public Book getFirstBookByAuthor(Set<Author> authors) {
        Book toFind = new Book(null, authors, 0, 0);
        return getFirstBookByCustomSearch(COMPARATOR_AUTHORS_CONTAINS, toFind);
    }

    public Library getBooksByAuthor(Set<Author> authors) {
        Book toFind = new Book(null, authors, 0, 0);
        return getBooksByCustomSearch(COMPARATOR_AUTHORS_CONTAINS, toFind, false);
    }

    public Book getFirstBookByNameStartsWith(String name) {
        Book toFind = new Book(name, null, 0, 0);
        return getFirstBookByCustomSearch(COMPARATOR_TITLE_STARTS_WITH, toFind);
    }

    public Library getBooksByNameStartsWith(String name) {
        Book toFind = new Book(name, null, 0, 0);
        return getBooksByCustomSearch(COMPARATOR_TITLE_STARTS_WITH, toFind, false);
    }

    public Book getFirstBookByName(String name) {
        Book toFind = new Book(name, null, 0, 0);
        return getFirstBookByCustomSearch(COMPARATOR_TITLE_COMPARE_IGNORE_CASE, toFind);
    }

    public Library getBooksByName(String name) {
        Book toFind = new Book(name, null, 0, 0);
        return getBooksByCustomSearch(COMPARATOR_TITLE_COMPARE_IGNORE_CASE, toFind, false);
    }

    public Book getFirstBookByCustomSearch(Comparator<Book> comparator, Book toFind) {
        Library library = getBooksByCustomSearch(comparator, toFind, true);
        if (library.books.size() == 1) {
            return library.books.get(0);
        }
        return null;
    }

    public Library getBooksByCustomSearch(Comparator<Book> comparator, Book toFind) {
        return getBooksByCustomSearch(comparator, toFind, false);
    }

    private Library getBooksByCustomSearch(Comparator<Book> comparator, Book toFind, Boolean firstOnly) {
        Library newLibrary = new Library();
        for (Book book : books) {
            if (comparator.compare(book, toFind) == 0) {
                newLibrary.add(book);
                if (firstOnly) {
                    return newLibrary;
                }
            }
        }
        return newLibrary;
    }

    public void loadLibraryFromFile(File file) throws IOException {
        Library newLibrary = new Library();
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = LIBRARY_CSV_FORMAT.parse(in);
        for (CSVRecord record : records) {
            String field_title = record.get(TITLE);
            String field_authors = record.get(AUTHORS).replaceAll(Pattern.quote(AUTHOR_RECORD_SEPARATOR),"\n");
            String field_year = record.get(PUBLISHING_YEAR);
            String field_price = record.get(PRICE);
            try {
                Integer year = new Integer(field_year);
                Integer price = new Integer(field_price);
                Set<Author> authors = new HashSet<>();

                Iterable<CSVRecord> authorsRecords = AUTHOR_CSV_FORMAT.parse(new StringReader(field_authors));
                for(CSVRecord author : authorsRecords) {
                    authors.add(new Author(author.get(AUTHOR_NAME),
                            author.get(AUTHOR_SURNAME),
                            author.get(AUTHOR_MIDDLENAME),
                            AUTHOR_BIRTHDAY_FORMATTER.parse(author.get(AUTHOR_BIRTHDAY))));
                }
                newLibrary.add(new Book(field_title, authors, year, price));
            } catch (NumberFormatException nfe) {
                LOG.error("Error parsing integer values for {}", record, nfe);
            } catch (IOException ioe) {
                LOG.error("Error parsing authors {}", field_authors, ioe);
            } catch (ParseException pe) {
                LOG.error("Error parsing author's birthday {}", field_authors, pe);
            }
        }
        books.clear();
        add(newLibrary);
    }

    public void saveLibraryToFile(File file) throws IOException {
        try(FileWriter fw = new FileWriter(file, false)) {
            CSVPrinter libraryPrinter = new CSVPrinter(fw, LIBRARY_CSV_FORMAT);
            libraryPrinter.printRecord(TITLE, AUTHORS, PUBLISHING_YEAR, PRICE);
            CSVFormat writeAuthorFormat = AUTHOR_CSV_FORMAT.withSkipHeaderRecord().withRecordSeparator(AUTHOR_RECORD_SEPARATOR);
            for (Book book : books) {
                StringWriter field_authors = new StringWriter();
                CSVPrinter authorsPrinter = new CSVPrinter(field_authors, writeAuthorFormat);
                for (Author author : book.getAuthors()) {
                    authorsPrinter.printRecord(author.getName(), author.getSurname(), author.getMiddlename(), AUTHOR_BIRTHDAY_FORMATTER.format(author.getBirthday()));
                }
                libraryPrinter.printRecord(book.getTitle(), field_authors.toString(), book.getYear(), book.getPrice());
            }
        }
    }

    // задание зачено
    public Library sortByPrice() {
        return sortByCustomCondition(Comparator.comparing(Book::getPrice));
    }

    public Library sortByCustomCondition(Comparator<Book> condition) {
        books.sort(condition);
        return this;
    }

}
