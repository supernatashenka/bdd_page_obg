package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.netology.pages.DashboardPage;
import ru.netology.pages.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.DataHelper.generateValidAmount;


public class Test {
    LoginPage loginPage;
    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @org.junit.jupiter.api.Test
    void shouldTransferFromFirstCardToSecondCard() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(firstCardBalance);
        var expectedFirstCardBalanceAfterTransfer = firstCardBalance - amount;
        var expectedSecondCardBalanceAfterTransfer = secondCardBalance + amount;
        var moneyTransferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = moneyTransferPage.successfulTransfer(valueOf(amount), firstCardInfo);
        var actualFirstCardBalanceAfterTransfer = dashboardPage.getCardBalance(firstCardInfo);
        var actualFSecondCardBalanceAfterTransfer = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedFirstCardBalanceAfterTransfer, actualFirstCardBalanceAfterTransfer);
        assertEquals(expectedSecondCardBalanceAfterTransfer, actualFSecondCardBalanceAfterTransfer);

    }

    @org.junit.jupiter.api.Test
    void shouldTransferFromSecondCardToFirst() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(secondCardBalance);
        var firstCardBalanceAfterTransfer = firstCardBalance + amount;
        var secondCardBalanceAfterTransfer = secondCardBalance - amount;
        var moneyTransferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = moneyTransferPage.successfulTransfer(valueOf(amount), secondCardInfo);
        var actualFirstCardBalanceAfterTransfer = dashboardPage.getCardBalance(firstCardInfo);
        var actualFSecondCardBalanceAfterTransfer = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalanceAfterTransfer, actualFirstCardBalanceAfterTransfer);
        assertEquals(secondCardBalanceAfterTransfer, actualFSecondCardBalanceAfterTransfer);
    }

    @org.junit.jupiter.api.Test
    void transferIfAmountOverLimit() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateInvalidAmount(secondCardBalance);
        var moneyTransferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        moneyTransferPage.transfer(String.valueOf(amount), secondCardInfo);
        moneyTransferPage.findErrorNotification("Перевод невозможен. На карте недостаточно средств");
        var actualFirstCardBalanceAfterTransfer = dashboardPage.getCardBalance(firstCardInfo);
        var actualFSecondCardBalanceAfterTransfer = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalance, actualFirstCardBalanceAfterTransfer);
        assertEquals(secondCardBalance, actualFSecondCardBalanceAfterTransfer);


    }

}