package ru.netology;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        // Генерируем валидного пользователя через DataGenerator
        var validUser = DataGenerator.Registration.generateUser("ru");

        // Создаем две разные даты: первая через 4 дня, вторая — через 7 дней
        int daysToAddForFirstMeeting = 4;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);

        int daysToAddForSecondMeeting = 7;
        String secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        // --- ПЕРВАЯ ЗАПИСЬ НА ВСТРЕЧУ ---
        $("[data-test-id='city'] input").setValue(validUser.getCity());

        // Очищаем дефолтную дату в поле ввода
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);

        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Запланировать")).click();

        // Проверяем всплывающее окно первой заявки
        $("[data-test-id='success-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + firstMeetingDate));

        // --- ПОВТОРНЫЙ ЗАКАЗ (ПЕРЕПЛАНИРОВАНИЕ) ---
        // Меняем только дату
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);

        $$("button").find(Condition.text("Запланировать")).click();

        // Проверяем появление окна с предложением перепланировать
        $("[data-test-id='replan-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Необходимо подтверждение У вас уже запланирована встреча на другую дату. Перепланировать?"));

        // Кликаем по кнопке Перепланировать
        $$("[data-test-id='replan-notification'] button").find(Condition.text("Перепланировать")).click();

        // Уведомление об успешном перепланировании
        $("[data-test-id='success-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + secondMeetingDate));
    }
}
