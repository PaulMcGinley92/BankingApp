package ie.atu.sw;

import org.junit.jupiter.api.*;                           
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ie.atu.sw.exceptions.AccountNotFoundException;
import ie.atu.sw.exceptions.InsufficientFundsException;
import ie.atu.sw.exceptions.InvalidAmountException;

import static org.junit.jupiter.api.Assertions.*;        

/**
 * JUnit 5 test class for the BankingApp.
 * 
 * This class verifies:
 * - Correct behavior of core banking operations 
 * - Tracking of total deposits
 * - Exception handling for invalid operations
 * - Performance via a timeout test
 * - Variations in input using parameterized tests
 */
class BankingAppTest {

    // Fresh instance of the system under test created before each test
    private BankingApp bank;

    /**
     * Runs once before any test methods in this class.
     * Used here for high-level suite initialisation/logging.
     */
    @BeforeAll
    static void initAll() {
        System.out.println("---- Starting BankingApp test suite ----");
    }

    /**
     * Runs before each test method.
     * Ensures each test starts with a clean, consistent Bank state.
     */
    @BeforeEach
    void init() {
        bank = new BankingApp();
        bank.addAccount("Alice", 1000.0);
        bank.addAccount("Bob", 500.0);
    }

    /**
     * Verifies that adding a new account increases total deposits
     * and stores the correct initial balance for the new account.
     */
    @Test
    void addAccountIncreasesTotalDepositsAndStoresBalance() {
        bank.addAccount("Charlie", 300.0);

        assertEquals(1000.0 + 500.0 + 300.0, bank.getTotalDeposits(), 0.0001);
        assertEquals(300.0, bank.getBalance("Charlie"), 0.0001);
    }

    /**
     * Verifies a valid deposit updates both the account balance
     * and the bank's total deposits.
     */
    @Test
    void depositValidAmountUpdatesBalanceAndTotalDeposits() {
        double beforeBalance = bank.getBalance("Alice");
        double beforeTotal   = bank.getTotalDeposits();

        bank.deposit("Alice", 200.0);

        assertEquals(beforeBalance + 200.0, bank.getBalance("Alice"), 0.0001);
        assertEquals(beforeTotal + 200.0, bank.getTotalDeposits(), 0.0001);
    }

    /**
     * Verifies a valid withdrawal reduces both the account balance
     * and the bank's total deposits.
     */
    @Test
    void withdrawValidAmountUpdatesBalanceAndTotalDeposits() {
        double beforeBalance = bank.getBalance("Bob");
        double beforeTotal   = bank.getTotalDeposits();

        bank.withdraw("Bob", 300.0);

        assertEquals(beforeBalance - 300.0, bank.getBalance("Bob"), 0.0001);
        assertEquals(beforeTotal - 300.0, bank.getTotalDeposits(), 0.0001);
    }

    /**
     * Verifies that an approved loan:
     * - Increases the customer's loan value
     * - Decreases the bank's total deposits by the loan amount
     */
    @Test
    void approveLoanReducesTotalDepositsAndSetsLoanOnAccount() {
        double beforeTotal = bank.getTotalDeposits();

        bank.approveLoan("Alice", 400.0);

        assertEquals(400.0, bank.getLoan("Alice"), 0.0001);
        assertEquals(beforeTotal - 400.0, bank.getTotalDeposits(), 0.0001);
    }

    /**
     * Verifies that loan repayment:
     * - Reduces the outstanding loan
     * - Increases the bank's total deposits
     */
    @Test
    void repayLoanReducesLoanAndIncreasesTotalDeposits() {
        bank.approveLoan("Alice", 400.0);
        double beforeLoan  = bank.getLoan("Alice");
        double beforeTotal = bank.getTotalDeposits();

        bank.repayLoan("Alice", 150.0);

        assertEquals(beforeLoan - 150.0, bank.getLoan("Alice"), 0.0001);
        assertEquals(beforeTotal + 150.0, bank.getTotalDeposits(), 0.0001);
    }

    /**
     * Verifies that getBalance returns the correct initial balances
     * for known accounts.
     */
    @Test
    void getBalanceReturnsCorrectValue() {
        assertEquals(1000.0, bank.getBalance("Alice"), 0.0001);
        assertEquals(500.0, bank.getBalance("Bob"), 0.0001);
    }

    /**
     * Verifies that a newly created account has no outstanding loan.
     */
    @Test
    void getLoanReturnsZeroForNewAccount() {
        assertEquals(0.0, bank.getLoan("Alice"), 0.0001);
    }

    /**
     * Runs after each test method.
     * Used here for simple logging / debugging support.
     */
    @AfterEach
    void tearDown() {
        System.out.println("Test finished. Total deposits: " + bank.getTotalDeposits());
    }

    /**
     * Runs once after all test methods in this class have executed.
     * Used for final logging/cleanup.
     */
    @AfterAll
    static void tearDownAll() {
        System.out.println("----- BankingApp test suite complete ----");
    }
    
    /**
     * Verifies that attempting to deposit a negative amount
     * results in an InvalidAmountException.
     */
    @Test
    void depositNegativeAmountThrows() {
        assertThrows(InvalidAmountException.class,
                () -> bank.deposit("Alice", -50));
    }
    
    /**
     * Verifies that withdrawing more than the account balance
     * results in an InsufficientFundsException.
     */
    @Test
    void withdrawTooMuchThrows() {
        assertThrows(InsufficientFundsException.class,
                () -> bank.withdraw("Alice", 2000));
    }

    /**
     * Verifies that attempting to repay more than the outstanding loan
     * results in an InvalidAmountException.
     */
    @Test
    void repayMoreThanLoanThrows() {
        bank.approveLoan("Alice", 300);

        assertThrows(InvalidAmountException.class,
                () -> bank.repayLoan("Alice", 500));
    }
    
    /**
     * Verifies that requesting the balance for a non-existent account
     * results in an AccountNotFoundException.
     */
    @Test
    void getBalanceForMissingAccountThrows() {
        assertThrows(AccountNotFoundException.class,
                () -> bank.getBalance("DoesNotExist"));
    }
    
    /**
     * Parameterized test that verifies depositing a range of valid amounts
     * correctly increases the account balance by the given amount.
     */
    @ParameterizedTest
    @ValueSource(doubles = {50.0, 100.0, 150.0})
    void depositVariousAmountsShouldIncreaseBalance(double amount) {
        double startingBalance = bank.getBalance("Alice");

        bank.deposit("Alice", amount);

        assertEquals(startingBalance + amount,
                     bank.getBalance("Alice"),
                     0.0001,
                     "Balance should increase by parameter amount");
    }
    
    /**
     * Parameterized test that verifies multiple valid withdrawal amounts
     * reduce the account balance by the correct amount each time.
     */
    @ParameterizedTest
    @ValueSource(doubles = {50.0, 75.0, 100.0})
    void withdrawVariousValidAmounts(double amount) {
        double startingBalance = bank.getBalance("Bob");

        bank.withdraw("Bob", amount);

        assertEquals(startingBalance - amount,
                     bank.getBalance("Bob"),
                     0.0001);
    }
    
    /**
     * Timeout test to ensure that a large number of operations
     * complete within a reasonable time.
     */
    @Timeout(1) 
    @Test
    void manyOperationsCompleteQuickly() {
        for (int i = 0; i < 1_000; i++) {
            bank.deposit("Alice", 1.0);
            bank.withdraw("Alice", 1.0);
        }

        assertTrue(bank.getBalance("Alice") >= 0);
    }
}
