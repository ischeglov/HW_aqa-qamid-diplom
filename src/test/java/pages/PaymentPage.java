package pages;

import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class PaymentPage {

    private final SelenideElement buyDebitCardButton = $("div > button:nth-child(3) > span > span");
    private final SelenideElement headingDebitPay = $(byText("Оплата по карте"));
    private final SelenideElement cardNumder = $("form > fieldset > div:nth-child(1) .input input ");
    private final SelenideElement month = $("form > fieldset > div:nth-child(2) > .input-group > span:nth-child(1) .input input");
    private final SelenideElement year = $("form > fieldset > div:nth-child(2) > .input-group > span:nth-child(2) .input input");
    private final SelenideElement cardOwner = $("form > fieldset > div:nth-child(3) > .input-group > span:nth-child(1) .input input");
    private final SelenideElement cardCode = $("form > fieldset > div:nth-child(3) > .input-group > span:nth-child(2) .input input");
    private final SelenideElement buttonSubmit = $("form > fieldset > div:nth-child(4) button");
    private final SelenideElement notificationChecking = $(".notification");
    private final SelenideElement notification = $(".notification_visible .notification__content");

    public void openDebitPayPage() {
        buyDebitCardButton.click();
        headingDebitPay.shouldBe(visible);
    }

    public void waitingNotification() {
        notificationChecking.shouldHave(visible, Duration.ofSeconds(15));
    }

    public void shouldNotificationText(String expectedText) {
        notification.shouldHave(exactText(expectedText)).shouldBe(visible, Duration.ofSeconds(15));
    }

    public void paymentByCard(DataHelper.CardInfo cardInfo) throws InterruptedException {
        cardNumder.setValue(cardInfo.getNumber());
        month.setValue(cardInfo.getMonth());
        year.setValue(cardInfo.getYear());
        cardOwner.setValue(cardInfo.getOwner());
        cardCode.setValue(cardInfo.getCvcCode());
        buttonSubmit.click();
    }
}
