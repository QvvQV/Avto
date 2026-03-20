package ru.tutor.base;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.tutor.page.orders.CloudPage;
import ru.tutor.page.OrderDatesPage;
import ru.tutor.page.orders.SberPage;
import ru.tutor.page.admin.AdminOrderDetails;
import ru.tutor.page.admin.AdminOrdersPage;
import ru.tutor.page.orders.TtPage;
import ru.tutor.test.context.TestExecutionContext;
import ru.tutor.test.executor.PaymentCycleExecutor;
import ru.tutor.utils.DateValidator;

public class RecurrentPaymentTestBase extends BaseUiTest {

    // Константы
    protected static final int ALLOWED_TIME_DIFFERENCE_MINUTES = 1;

    // Page Objects
    protected AdminOrderDetails adminOrderDetails = new AdminOrderDetails();
    protected AdminOrdersPage adminOrdersPage = new AdminOrdersPage();
    protected TtPage ttPage = new TtPage();
    protected CloudPage cloudPage = new CloudPage();
    protected SberPage sberPage = new SberPage();

    // Вспомогательные классы
    protected OrderDatesPage orderDatesPage = new OrderDatesPage(adminOrderDetails);
    protected DateValidator dateValidator = new DateValidator(ALLOWED_TIME_DIFFERENCE_MINUTES);

    protected TestExecutionContext context =  new TestExecutionContext();
    protected PaymentCycleExecutor  paymentCycleExecutor;

    @BeforeAll
    public static void setUp() {
        setupBrowserWithoutIncognito();
    }

    @BeforeEach
    public void init() {
        adminOrderDetails = new AdminOrderDetails();
        adminOrdersPage = new AdminOrdersPage();
        ttPage = new TtPage();
        cloudPage = new CloudPage();
        sberPage = new SberPage();

        orderDatesPage = new OrderDatesPage(adminOrderDetails);
        dateValidator = new DateValidator(ALLOWED_TIME_DIFFERENCE_MINUTES);

        context = new TestExecutionContext();

        initializePaymentCycleExecutor();
    }

    protected void initializePaymentCycleExecutor() {
        paymentCycleExecutor = new PaymentCycleExecutor(orderDatesPage, dateValidator, adminOrdersPage, adminOrderDetails);
    }
}
