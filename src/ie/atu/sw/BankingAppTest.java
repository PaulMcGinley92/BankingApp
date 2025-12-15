package ie.atu.sw;

import org.junit.jupiter.api.*;                           // @BeforeAll, @BeforeEach, etc.

import ie.atu.sw.exceptions.AccountNotFoundException;
import ie.atu.sw.exceptions.InsufficientFundsException;
import ie.atu.sw.exceptions.InvalidAmountException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;        // assertEquals, assertTrue, etc.

class BankingAppTest {

    private BankingApp bank;

    @BeforeAll
    static void initAll() {
        System.out.println("---- Starting BankingApp test suite ----");
    }

    @BeforeEach
    void init() {
        bank = new BankingApp();
        bank.addAccount("Alice", 1000.0);
        bank.addAccount("Bob", 500.0);
    }

    @Test
    void addAccountIncreasesTotalDepositsAndStoresBalance() {
        bank.addAccount("Charlie", 300.0);

        assertEquals(1000.0 + 500.0 + 300.0, bank.getTotalDeposits(), 0.0001);
        assertEquals(300.0, bank.getBalance("Charlie"), 0.0001);
    }

    @Test
    void depositValidAmountUpdatesBalanceAndTotalDeposits() {
        double beforeBalance = bank.getBalance("Alice");
        double beforeTotal   = bank.getTotalDeposits();

        bank.deposit("Alice", 200.0);

        assertEquals(beforeBalance + 200.0, bank.getBalance("Alice"), 0.0001);
        assertEquals(beforeTotal + 200.0, bank.getTotalDeposits(), 0.0001);
    }

    @Test
    void withdrawValidAmountUpdatesBalanceAndTotalDeposits() {
        double beforeBalance = bank.getBalance("Bob");
        double beforeTotal   = bank.getTotalDeposits();

        bank.withdraw("Bob", 300.0);

        assertEquals(beforeBalance - 300.0, bank.getBalance("Bob"), 0.0001);
        assertEquals(beforeTotal - 300.0, bank.getTotalDeposits(), 0.0001);
    }

    @Test
    void approveLoanReducesTotalDepositsAndSetsLoanOnAccount() {
        double beforeTotal = bank.getTotalDeposits();

        bank.approveLoan("Alice", 400.0);

        assertEquals(400.0, bank.getLoan("Alice"), 0.0001);
        assertEquals(beforeTotal - 400.0, bank.getTotalDeposits(), 0.0001);
    }

    @Test
    void repayLoanReducesLoanAndIncreasesTotalDeposits() {
        bank.approveLoan("Alice", 400.0);
        double beforeLoan  = bank.getLoan("Alice");
        double beforeTotal = bank.getTotalDeposits();

        bank.repayLoan("Alice", 150.0);

        assertEquals(beforeLoan - 150.0, bank.getLoan("Alice"), 0.0001);
        assertEquals(beforeTotal + 150.0, bank.getTotalDeposits(), 0.0001);
    }

    @Test
    void getBalanceReturnsCorrectValue() {
        assertEquals(1000.0, bank.getBalance("Alice"), 0.0001);
        assertEquals(500.0, bank.getBalance("Bob"), 0.0001);
    }

    @Test
    void getLoanReturnsZeroForNewAccount() {
        assertEquals(0.0, bank.getLoan("Alice"), 0.0001);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test finished. Total deposits: " + bank.getTotalDeposits());
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("----- BankingApp test suite complete ----");
    }
    
    @Test
    void depositNegativeAmountThrows() {
        assertThrows(InvalidAmountException.class,
                () -> bank.deposit("Alice", -50));
    }
    
    @Test
    void withdrawTooMuchThrows() {
        assertThrows(InsufficientFundsException.class,
                () -> bank.withdraw("Alice", 2000));
    }
    
    void repayMoreThanLoanThrows() {
        bank.approveLoan("Alice", 300);

        assertThrows(InvalidAmountException.class,
                () -> bank.repayLoan("Alice", 500));
    }
    
    @Test
    void getBalanceForMissingAccountThrows() {
        assertThrows(AccountNotFoundException.class,
                () -> bank.getBalance("DoesNotExist"));
    }
}
