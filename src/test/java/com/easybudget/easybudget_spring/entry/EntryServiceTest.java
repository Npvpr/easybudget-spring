package com.easybudget.easybudget_spring.entry;

import com.easybudget.easybudget_spring.account.*;
import com.easybudget.easybudget_spring.category.*;
import com.easybudget.easybudget_spring.embedding.EmbeddingService;
import com.easybudget.easybudget_spring.user.User;
import com.easybudget.easybudget_spring.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EntryServiceTest {

    @InjectMocks
    private EntryService entryService;

    @Mock
    private EntryRepository entryRepository;
    @Mock
    private EntryCheckService entryCheckService;
    @Mock
    private AccountCheckService accountCheckService;
    @Mock
    private AccountBalanceService accountBalanceService;
    @Mock
    private CategoryCheckService categoryCheckService;
    @Mock
    private EmbeddingService embeddingService;
    @Mock
    private UserService userService;

    private User testUser;
    private Account testAccount;
    private Category testCategory;
    private Entry testEntry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setBalance(BigDecimal.valueOf(1000));

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");

        testEntry = Entry.builder()
                .id(1L)
                .type(Type.OUTCOME)
                .user(testUser)
                .account(testAccount)
                .category(testCategory)
                .cost(BigDecimal.valueOf(100))
                .date(LocalDate.now())
                .description("Test expense")
                .build();
    }

    @Test
    void testCreateEntry_ShouldSaveNewEntry() {
        CreateEntryRequestDto dto = new CreateEntryRequestDto(
                Type.OUTCOME,
                testAccount.getId(),
                testCategory.getId(),
                BigDecimal.valueOf(100),
                LocalDate.now(),
                "Lunch expense"
        );

        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(accountCheckService.findAccountById(testAccount.getId())).thenReturn(testAccount);
        when(categoryCheckService.findCategoryById(testCategory.getId())).thenReturn(testCategory);
        when(entryRepository.save(any(Entry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EntryDto result = entryService.createEntry(dto);

        assertNotNull(result);
        assertEquals("Lunch expense", result.getDescription());
        verify(accountBalanceService).updateAccountBalanceOfNewEntry(any(Entry.class), eq(testAccount));
        verify(embeddingService).createEntryEmbedding(any(Entry.class));
        verify(entryRepository).save(any(Entry.class));
    }

    @Test
    void testUpdateEntry_ShouldUpdateValues() {
        UpdateEntryRequestDto dto = new UpdateEntryRequestDto(
                Type.INCOME,
                testAccount.getId(),
                testCategory.getId(),
                BigDecimal.valueOf(200),
                LocalDate.now(),
                "Updated entry"
        );

        when(entryCheckService.findEntryById(testEntry.getId())).thenReturn(testEntry);
        when(accountCheckService.findAccountById(testAccount.getId())).thenReturn(testAccount);
        when(categoryCheckService.findCategoryById(testCategory.getId())).thenReturn(testCategory);
        when(entryRepository.save(any(Entry.class))).thenReturn(testEntry);

        EntryDto result = entryService.updateEntry(testEntry.getId(), dto);

        assertNotNull(result);
        assertEquals("Updated entry", result.getDescription());
        verify(accountBalanceService).updateAccountBalanceOfOldEntry(eq(testEntry), any(Entry.class), eq(testAccount));
        verify(embeddingService).updateEntryEmbedding(any(Entry.class));
    }

    @Test
    void testDeleteEntry_ShouldDeleteSuccessfully() {
        when(entryCheckService.findEntryById(testEntry.getId())).thenReturn(testEntry);

        String response = entryService.deleteEntry(testEntry.getId());

        assertTrue(response.contains("deleted successfully"));
        verify(accountBalanceService).updateAccountBalanceOfDeletedEntry(testEntry);
        verify(embeddingService).deleteEntryEmbedding(testEntry.getId());
        verify(entryRepository).deleteById(testEntry.getId());
    }

    @Test
    void testFilterEntriesForMonthEntry_ShouldReturnCorrectTotals() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(entryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(testEntry));

        Map<String, Object> result = entryService.filterEntriesForMonthEntry(2025, 1);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(0).subtract(testEntry.getCost()), result.get("totalBalance"));
        assertEquals(BigDecimal.valueOf(100), result.get("totalOutcome"));
        assertEquals(0, ((BigDecimal) result.get("totalIncome")).intValue());
    }

    @Test
    void testFilterEntriesForHistory_ShouldRespectSortingAndTotals() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(testUser);
        when(entryRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(testEntry));

        Map<String, Object> result = entryService.filterEntriesForHistory(
                Type.OUTCOME, testAccount.getId(), testCategory.getId(),
                LocalDate.now().minusDays(5), LocalDate.now(), "date", "DESC");

        assertNotNull(result);
        assertTrue(((List<?>) result.get("entries")).size() > 0);
        assertEquals(BigDecimal.valueOf(-100), result.get("totalCost"));
    }
}
