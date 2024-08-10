package data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.util.Locale;

public class DataHelper {

    private static final Faker FAKER = new Faker(new Locale("en"));

    private DataHelper() {
    }

    @Value
    public static class CardInfo {
        String number;
        String month;
        String year;
        String owner;
        String cvcCode;
    }

    public static String approvedCardNumber() {
        return "1111 2222 3333 4444";
    }

    public static String declinedCardNumber() {
        return "5555 6666 7777 8888";
    }

    public static String getRandomCardNumber() {
        return FAKER.numerify("#### #### #### ####");
    }

    public static String getInvalidCardNumber() {
        return FAKER.numerify("#### #### #### ###");
    }

    @Value
    public static class CardMonthAndYear {
        String cardMonth;
        String cardYear;
    }

    public static CardMonthAndYear getValidMonthAndYear() {
        var currentDate = LocalDate.now();
        var randomMonth = String.format("%02d", FAKER.number().numberBetween(1, 12));
        var randomYear = String.format("%04d", Faker.instance().number().numberBetween(currentDate.getYear() + 1, currentDate.getYear() + 5)).substring(2);
        return new CardMonthAndYear(randomMonth, randomYear);
    }

    public static CardMonthAndYear getInvalidMonthAndYear() {
        var currentDate = LocalDate.now();
        var randomMonth = String.format("%02d", FAKER.number().numberBetween(1, 12));
        var randomYear = String.format("%04d", Faker.instance().number().numberBetween(currentDate.getYear() + 6, currentDate.getYear() + 50)).substring(2);
        return new CardMonthAndYear(randomMonth, randomYear);
    }

    public static CardMonthAndYear getCurrentMonthAndYear() {
        var currentDate = LocalDate.now();
        var formattedMonth = String.format("%02d", LocalDate.now().getMonthValue());
        var formattedYear = String.format("%04d", LocalDate.now().getYear()).substring(2);
        return new CardMonthAndYear(formattedMonth, formattedYear);
    }

    public static String getValidCardOwnerName() {
        var firstName = FAKER.name().firstName();
        var lastName = FAKER.name().lastName();
        return firstName + " " + lastName;
    }

    public static String getInvalidCardOwnerNameNumber() {
            return "13454677";
    }

    public static String getInvalidCardOwnerNameSpecSimbol() {
        return "#$&^%#&%#&";
    }

    public static String getInvalidCardOwnerNameOneSimbol() {
        return "A";
    }

    public static String getInvalidCardOwnerNameCyrillic() {
        return "Иван Щеглов";
    }

    public static String getRandomCardCode() {
        return FAKER.numerify("###");
    }

    public static String getInvalidCardCode() {
        return FAKER.numerify("##");
    }

    public static String getInvalidCardCodeLetters() {
        return "aqa";
    }
}
