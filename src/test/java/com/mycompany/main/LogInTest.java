package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @ST10538709
 */
//Unit tests for the LogIn class (Part 1)
public class LogInTest{

   private LogIn login;

   //A fresh LogIn object before every test so the tests don't affect each other
   @BeforeEach
   public void setUp(){
      login = new LogIn();
   }

   //Username tests
   @Test
   public void usernameCorrectlyFormatted(){
      login.userName = "kyl_1";
      assertTrue(login.checkUserName());
   }

   @Test
   public void usernameIncorrectlyFormatted(){
      login.userName = "kyle!!!!!!!";
      assertFalse(login.checkUserName());
   }

   @Test
   public void usernameWithUnderscoreButTooLong(){
      login.userName = "kyle_123";
      assertFalse(login.checkUserName());
   }

   //Password tests
   @Test
   public void passwordMeetsComplexityRequirements(){
      login.passWord = "Ch&&sec@ke99!";
      assertTrue(login.checkPasswordComplexity());
   }

   @Test
   public void passwordDoesNotMeetComplexityRequirements(){
      login.passWord = "password";
      assertFalse(login.checkPasswordComplexity());
   }

   @Test
   public void passwordTooShort(){
      login.passWord = "Ch&9";
      assertFalse(login.checkPasswordComplexity());
   }

   @Test
   public void passwordMissingCapitalLetter(){
      login.passWord = "ch&&sec@ke99!";
      assertFalse(login.checkPasswordComplexity());
   }

   @Test
   public void passwordMissingNumber(){
      login.passWord = "Ch&&sec@ke!!";
      assertFalse(login.checkPasswordComplexity());
   }

   @Test
   public void passwordMissingSpecialCharacter(){
      login.passWord = "Chocsecake99";
      assertFalse(login.checkPasswordComplexity());
   }

   //Cell phone number tests
   @Test
   public void cellPhoneNumberCorrectlyFormatted(){
      login.cellPhoneNumber = "+27838968976";
      assertTrue(login.checkCellPhoneNumber());
   }

   @Test
   public void cellPhoneNumberIncorrectlyFormatted(){
      login.cellPhoneNumber = "08966553";
      assertFalse(login.checkCellPhoneNumber());
   }

   //Registration tests
   @Test
   public void registerUserWithValidDetails(){
      login.userName = "kyl_1";
      login.passWord = "Ch&&sec@ke99!";
      login.cellPhoneNumber = "+27838968976";
      assertEquals("User registered successfully. Welcome kyl_1!", login.registerUser());
   }

   @Test
   public void registerUserWithInvalidDetails(){
      login.userName = "kyle!!!!!!!";
      login.passWord = "password";
      login.cellPhoneNumber = "08966553";
      assertEquals("Registration failed, please check your details and try again.", login.registerUser());
   }

   //Login tests
   @Test
   public void loginSuccessful(){
      login.userName = "kyl_1";
      login.passWord = "Ch&&sec@ke99!";
      login.cellPhoneNumber = "+27838968976";
      login.registerUser();

      assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
   }

   @Test
   public void loginFailedWithWrongPassword(){
      login.userName = "kyl_1";
      login.passWord = "Ch&&sec@ke99!";
      login.cellPhoneNumber = "+27838968976";
      login.registerUser();

      assertFalse(login.loginUser("kyl_1", "WrongPass1!"));
   }

   @Test
   public void loginFailedWithWrongUsername(){
      login.userName = "kyl_1";
      login.passWord = "Ch&&sec@ke99!";
      login.cellPhoneNumber = "+27838968976";
      login.registerUser();

      assertFalse(login.loginUser("abc_1", "Ch&&sec@ke99!"));
   }

   @Test
   public void loginFailedWhenNobodyHasRegistered(){
      assertFalse(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
   }

   //Login status messages
   @Test
   public void loginStatusMessageWhenSuccessful(){
      login.registeredUserName = "kyl_1";
      assertEquals("Login successful, welcome back kyl_1, it is great to see you again.", login.returnLoginStatus(true));
   }

   @Test
   public void loginStatusMessageWhenFailed(){
      assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(false));
   }
}
