package pages;

import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class PaymentPage {

    private SelenideElement buyDebitCardButton = $("div > button:nth-child(3) > span > span");
    private SelenideElement headingDebitPay = $(byText("Оплата по карте"));
    private SelenideElement cardNumder = $("form > fieldset > div:nth-child(1) .input input ");
    private SelenideElement month = $("form > fieldset > div:nth-child(2) > .input-group > span:nth-child(1) .input input");
    private SelenideElement year = $("form > fieldset > div:nth-child(2) > .input-group > span:nth-child(2) .input input");
    private SelenideElement cardOwner = $("form > fieldset > div:nth-child(3) > .input-group > span:nth-child(1) .input input");
    private SelenideElement cardCode = $("form > fieldset > div:nth-child(3) > .input-group > span:nth-child(2) .input input");
    private SelenideElement buttonSubmit = $("form > fieldset > div:nth-child(4) button");
    private SelenideElement notificationChecking = $(".notification");
    private SelenideElement notificationOkContent = $(".notification_status_ok .notification__content");
    private SelenideElement notificationStatusOk = $(".notification_status_ok");
    private SelenideElement notificationErrorContent = $(".notification_status_error .notification__content");
    private SelenideElement inputInvalid = $(".input_invalid");

    public void openDebitPayPage() {
        buyDebitCardButton.click();
        headingDebitPay.shouldBe(visible);
    }

    public void waitingNotification() {
        notificationChecking.shouldHave(visible, Duration.ofSeconds(15));
    }

    public void shouldNotificationSuccessfulText(String expectedText) {
        notificationOkContent.shouldHave(exactText(expectedText)).shouldBe(visible, Duration.ofSeconds(15));
    }

    public void shouldNotificationUnsuccessfulText(String expectedText) {
        notificationErrorContent.shouldHave(exactText(expectedText)).shouldBe(visible, Duration.ofSeconds(15));
    }

    public void shouldOkNotificationInvisibile() {
        notificationStatusOk.shouldNotBe(visible).should(disappear, Duration.ofSeconds(15));
    }

    public void checkInputInvalid(String expectedText) {
        inputInvalid.shouldHave(exactText(expectedText)).shouldBe(visible, Duration.ofSeconds(5));
    }

    public void paymentByCard(DataHelper.CardInfo cardInfo) {
        cardNumder.setValue(cardInfo.getNumber());
        month.setValue(cardInfo.getMonth());
        year.setValue(cardInfo.getYear());
        cardOwner.setValue(cardInfo.getOwner());
        cardCode.setValue(cardInfo.getCvcCode());
        buttonSubmit.click();
    }
}
