package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import pages.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DebitCardTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080");
        var paymentPage = new PaymentPage();
        paymentPage.openDebitPayPage();
    }

    @Test
    public void SuccessfulPurchaseWithValidCard() {
        var cardNumber = DataHelper.approvedCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, cardOwner, cardCode);
        var paymentPage = new PaymentPage();


        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
        });

         var expectedStatus = "APPROVED";
         var actualStatus = SQLHelper.getInfoFromDebitPayment().getStatus();
         var transactionID = SQLHelper.getInfoFromDebitPayment().getTransaction_id();
         var paymentID = SQLHelper.getInfoFromOrder().getPayment_id();

        assertAll(
                () ->
                        step("Проверка уведомления об оплате", () -> {
                            paymentPage.shouldNotificationText("Операция одобрена Банком.");
                        }),
                () ->
                        step("Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Проверка платежа в таблице заказов в БД", () -> {
                            assertEquals(transactionID, paymentID);
                        }));
    }
}
