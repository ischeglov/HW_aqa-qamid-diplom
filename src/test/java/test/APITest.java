package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.APIHelper;
import data.DataHelper;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

public class APITest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @DisplayName("Успешная покупка с валидными данными дебетовой карты со статусом APPROVED")
    @Test
    public void successfulPurchaseWithValidCardAPI() {
        var cardNumber = DataHelper.approvedCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, cardOwner, cardCode);
        Response response = APIHelper.paymentByDebitCard(cardInfo, 200);

        String actualStatus = response.path("status");
        String expectedStatus = "APPROVED";

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @DisplayName("Отклонение оплаты с дебетовой карты со статусом DECLINED")
    @Test
    public void paymentRejectionWithCardDeclinedAPI() {
        var cardNumber = DataHelper.declinedCardNumber();
        var month = DataHelper.getValidMonthAndYear().getCardMonth();
        var year = DataHelper.getValidMonthAndYear().getCardYear();
        var cardOwner = DataHelper.getValidCardOwnerName();
        var cardCode = DataHelper.getRandomCardCode();
        var cardInfo = new DataHelper.CardInfo(cardNumber, month, year, cardOwner, cardCode);
        Response response = APIHelper.paymentByDebitCard(cardInfo, 200);

        String actualStatus = response.path("status");
        String expectedStatus = "DECLINED";

        Assertions.assertEquals(expectedStatus, actualStatus);
    }
}
