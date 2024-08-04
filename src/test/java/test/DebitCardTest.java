package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import pages.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

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

    @DisplayName("Успешная покупка с валидными данными дебетовой карты со статусом APPROVED")
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
            paymentPage.waitingNotification();
        });

        var expectedStatus = "APPROVED";
        var actualStatus = SQLHelper.getInfoFromDebitPayment().getStatus();
        var transactionID = SQLHelper.getInfoFromDebitPayment().getTransaction_id();
        var paymentID = SQLHelper.getInfoFromOrder().getPayment_id();

        assertAll(
                () ->
                        step("Проверка уведомления об оплате", () -> {
                            paymentPage.shouldNotificationSuccessfulText("Операция одобрена Банком.");
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

    @DisplayName("Отклонение оплаты с дебетовой карты со статусом DECLINED")
    @Test
    public void PaymentRejectionWithCardDeclined() {
        var cardNumber = DataHelper.declinedCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, cardOwner, cardCode);
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
            paymentPage.waitingNotification();
        });

        var expectedStatus = "DECLINED";
        var actualStatus = SQLHelper.getInfoFromDebitPayment().getStatus();
        var transactionID = SQLHelper.getInfoFromDebitPayment().getTransaction_id();
        var paymentID = SQLHelper.getInfoFromOrder().getPayment_id();

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.shouldNotificationSuccessfulText("Ошибка! Банк отказал в проведении операции.");
                        }),
                () ->
                        step("Проверка статуса платежа в БД", () -> {
                            assertEquals(expectedStatus, actualStatus);
                        }),
                () ->
                        step("Проверка отсутствия платежа со статусом Declined в таблице заказов", () -> {
                            assertNotEquals(transactionID, paymentID);
                        })
        );
    }

    @Test
    @DisplayName("Отклонение оплаты с недействительным номером дебетовой карты")
    void PaymentRejectionWithInvalidCard() {
        var cardNumber = DataHelper.getRandomCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, cardOwner, cardCode);
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
            paymentPage.waitingNotification();
        });

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.shouldNotificationUnsuccessfulText("Ошибка! Банк отказал в проведении операции.");
                        }),
                () ->
                        step("Проверка отсутствия видимости уведомления об успехе", paymentPage::shouldOkNotificationInvisibile)
        );
    }

    @DisplayName("Неуспешная оплата без указания номера дебетовой карты")
    @Test
    public void UnsuccessfulPaymentWithoutCard() {
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo("", month, year, cardOwner, cardCode);
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Номер карты Неверный формат");
                        })
        );
    }

    @DisplayName("Неуспешная оплата без указания месяца дебетовой карты")
    @Test
    public void UnsuccessfulPaymentWithoutMonth() {
        var cardNumber = DataHelper.getRandomCardNumber();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, "", year, cardOwner, cardCode);
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Месяц Неверный формат");
                        })
        );
    }

    @DisplayName("Неуспешная оплата без указания года дебетовой карты")
    @Test
    public void UnsuccessfulPaymentWithoutYear() {
        var cardNumber = DataHelper.getRandomCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, "", cardOwner, cardCode);
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Год Неверный формат");
                        })
        );
    }

    @DisplayName("Неуспешная оплата без указания CVV/CVC дебетовой карты")
    @Test
    public void UnsuccessfulPaymentWithoutCode() {
        var cardNumber = DataHelper.getRandomCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, cardOwner, "");
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("CVC/CVV Неверный формат");
                        })
        );
    }

    @DisplayName("Неуспешная оплата без указания владельца дебетовой карты")
    @Test
    public void UnsuccessfulPaymentWithoutOwner() {
        var cardNumber = DataHelper.getRandomCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, "", cardCode);
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Владелец Поле обязательно для заполнения");
                        })
        );
    }

    @DisplayName("Неуспешная оплата с невалидным значением в поле владельц дебетовой карты")
    @Test
    public void UnsuccessfulPaymentWithoutInvalidOwner() {
        var cardNumber = DataHelper.approvedCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getInvalidCardOwnerNameSpecSimbol();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, cardOwner, cardCode);
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
            paymentPage.waitingNotification();
        });

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Владелец Неверный формат");
                        }),
                () ->
                        step("Проверка отсутствия видимости уведомления об успехе", paymentPage::shouldOkNotificationInvisibile)
        );
    }

    @Test
    public void UnsuccessfulPaymentWith15DigitCardNumber() {
        var cardNumber = DataHelper.getInvalidCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, cardOwner, cardCode);
        var paymentPage = new PaymentPage();

        step("Производим оплату", () -> {
            paymentPage.paymentByCard(cardInfo);
        });

        assertAll(
                () ->
                        step("Проверка уведомления об ошибке", () -> {
                            paymentPage.checkInputInvalid("Номер карты Неверный формат");
                        })
        );
    }
}
