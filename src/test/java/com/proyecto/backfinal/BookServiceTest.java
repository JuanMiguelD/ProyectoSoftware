package com.proyecto.backfinal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.proyecto.backfinal.models.*;
import com.proyecto.backfinal.repositories.BookRepository;
import com.proyecto.backfinal.services.BookService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private AbstractBook testBook;
    private Writer testWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testWriter = new Writer("test Writer","examplemail@example.com","1234","hi");
        
        testBook = new ElectronicBook("Title Test", "Genre Test", "2020-10-17",testWriter,"urlTest",100);
        
        testBook.setWriter(testWriter);
    }

    @Test
    void createBook_ShouldReturnSavedBook() {
        when(bookRepository.save(any(AbstractBook.class))).thenReturn(testBook);

        AbstractBook result = bookService.createBook(testBook);

        assertNotNull(result);
        assertEquals(testBook.getBookId(), result.getBookId());
        assertEquals(testBook.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).save(any(AbstractBook.class));
    }

    @Test
    void deleteBook_ShouldCallRepositoryDelete() {
        bookService.deleteBook(1);

        verify(bookRepository, times(1)).deleteById(1);
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBook() {
        AbstractBook updatedBook = new AudioBook("Updated Title", "Updated Genre", "2020-10-17",testWriter,"urlTest",100);


        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(AbstractBook.class))).thenReturn(testBook);

        AbstractBook result = bookService.updateBook(1L, updatedBook);

        assertNotNull(result);
        assertEquals(updatedBook.getTitle(), result.getTitle());
        assertEquals(updatedBook.getGenre(), result.getGenre());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(AbstractBook.class));
    }

    @Test
    void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            bookService.updateBook(1L, testBook)
        );
        
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(AbstractBook.class));
    }

    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        List<AbstractBook> expectedBooks = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        List<AbstractBook> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(expectedBooks.size(), result.size());
        assertEquals(expectedBooks.get(0), result.get(0));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBook_WhenBookExists_ShouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        AbstractBook result = bookService.getBook(1L);

        assertNotNull(result);
        assertEquals(testBook.getBookId(), result.getBookId());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBook_WhenBookDoesNotExist_ShouldThrowException() {
        testBook = new ElectronicBook("Title Test", "Genre Test", "2020-10-17",testWriter,"urlTest",100);
        
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            bookService.getBook(1L)
        );

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBooksByWriter_ShouldReturnWriterBooks() {
        List<AbstractBook> expectedBooks = Arrays.asList(testBook);
        
        when(bookRepository.findByWriterId(1L)).thenReturn(expectedBooks);

        List<AbstractBook> result = bookService.getBooksByWriterId(1L);

        assertNotNull(result);
        assertEquals(expectedBooks.size(), result.size());
        assertEquals(expectedBooks, result);
        verify(bookRepository, times(1)).findByWriterId(1L);
    }

    @Test
    public void testGetBooksByGenre_ReturnsBooks() {
        // Datos de prueba
        String genre = "Science Fiction";
        AbstractBook book1 = new ElectronicBook();
        book1.setTitle("Dune");
        book1.setGenre(genre);
    

        AbstractBook book2 = new AudioBook();
        book2.setTitle("Neuromancer");
        book2.setGenre(genre);

        List<AbstractBook> books = Arrays.asList(book1, book2);

        // Configuración del mock
        when(bookRepository.findByGenre(genre)).thenReturn(books);

        // Llamada al método
        List<AbstractBook> result = bookService.getBooksByGenre(genre);

        // Verificaciones
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dune", result.get(0).getTitle());
        assertEquals("Neuromancer", result.get(1).getTitle());
        verify(bookRepository, times(1)).findByGenre(genre);
    }

    @Test
    public void testGetBooksByGenre_ReturnsEmptyList() {
        String genre = "Nonexistent Genre";

        // Configuración del mock
        when(bookRepository.findByGenre(genre)).thenReturn(Arrays.asList());

        // Llamada al método
        List<AbstractBook> result = bookService.getBooksByGenre(genre);

        // Verificaciones
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookRepository, times(1)).findByGenre(genre);
    }
}
