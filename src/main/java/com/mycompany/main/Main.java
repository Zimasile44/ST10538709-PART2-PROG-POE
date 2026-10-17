package com.mycompany.main;
/**
 *
 * @ST10538709
 */
//P.O.E PART 1 and PART 2
import java.util.Scanner;

public class Main{
   public static void main(String[] args){
      Scanner input = new Scanner(System.in); //Scanner input for user input
      LogIn login = new LogIn(); //login object

      System.out.print("Enter your User Name:");//User name input
      login.userName = input.nextLine();
      while(!login.checkUserName()){
            System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            System.out.print("Enter your User Name:");//User name input
            login.userName = input.nextLine(); 
      }
      System.out.println("Username successfully entered.");
      
      System.out.print("Enter your Password:");//Password input
      login.passWord = input.nextLine();
      while(!login.checkPasswordComplexity()){
            System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            System.out.print("Enter your Password:");//Password input
            login.passWord = input.nextLine();
      }
      System.out.println("Password successfully captured.");
      
      System.out.print("Enter your Cell phone number:");//Cell phone number input 
      login.cellPhoneNumber = input.nextLine();
      while(!login.checkCellPhoneNumber()){
            System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
            System.out.print("Enter your Cell phone number:");//Cell phone number input 
            login.cellPhoneNumber = input.nextLine();
      }
      System.out.println("Cell phone number successfully added.");


      
      //Saves the validated details as the registered account, if all checks pass
      System.out.println(login.registerUser());


      
      //Now the user logs in separately, using the credentials they just registered
      System.out.print("\nEnter your User Name to log in:");
      String loginUserName = input.nextLine();

      System.out.print("Enter your Password to log in:");
      String loginPassWord = input.nextLine();

      boolean loginSuccessful = login.loginUser(loginUserName, loginPassWord);
      System.out.println(login.returnLoginStatus(loginSuccessful));

      //PART 2: messages can only be sent after a successful login
      if(!loginSuccessful){
         input.close();
         return;
      }

      System.out.println("\nWelcome to QuickChat.");

      //The user decides up front how many messages they want to enter
      int numMessages = readNumber(input, "How many messages do you want to enter? ", 1);
      boolean messagesEntered = false; //becomes true once the set number has been entered

      //The application keeps running until the user picks Quit
      boolean running = true;
      while(running){
         System.out.println("\nMenu");
         System.out.println("1) Send Messages");
         System.out.println("2) Show recently sent messages");
         System.out.println("3) Quit");
         int choice = readNumber(input, "Choose an option: ", 1);

         if(choice == 1){
            if(messagesEntered){
               System.out.println("You have already entered your " + numMessages + " messages.");
            }
            else{
               sendMessages(input, numMessages);
               messagesEntered = true;
            }
         }
         else if(choice == 2){
            System.out.println("Coming Soon.");
         }
         else if(choice == 3){
            running = false;
            System.out.println("Goodbye.");
         }
         else{
            System.out.println("Please choose 1, 2 or 3.");
         }
      }

      input.close();
    }

   //Lets the user enter the set number of messages, one loop per message
   public static void sendMessages(Scanner input, int numMessages){
      for(int i = 0; i < numMessages; i++){
         System.out.println("\nMessage " + (i + 1) + " of " + numMessages);
         Message msg = new Message(i); //i is the message number, the ID is generated for us
         System.out.println("Message ID generated: " + msg.getMessageID());

         //Recipient, asks again until the number is correct
         System.out.print("Enter the recipient's cell phone number: ");
         msg.setRecipient(input.nextLine().trim());
         while(!msg.checkRecipientCell().equals("Cell phone number successfully captured.")){
            System.out.println(msg.checkRecipientCell());
            System.out.print("Enter the recipient's cell phone number: ");
            msg.setRecipient(input.nextLine().trim());
         }
         System.out.println(msg.checkRecipientCell());

         //Message text, asks again until it is not empty and not over 250 characters
         System.out.print("Enter your message: ");
         msg.setMessageText(input.nextLine());
         while(msg.getMessageText().trim().isEmpty() || !msg.checkMessageLength().equals("Message ready to send.")){
            if(msg.getMessageText().trim().isEmpty()){
               System.out.println("The message cannot be empty.");
            }
            else{
               System.out.println(msg.checkMessageLength());
            }
            System.out.print("Enter your message: ");
            msg.setMessageText(input.nextLine());
         }
         System.out.println(msg.checkMessageLength());
         System.out.println("Message hash: " + msg.createMessageHash());

         //Send, disregard or store
         System.out.println("\n1) Send Message");
         System.out.println("2) Disregard Message");
         System.out.println("3) Store Message to send later");
         int option = readNumber(input, "Choose an option: ", 1);
         while(option > 3){
            System.out.println("Please choose 1, 2 or 3.");
            option = readNumber(input, "Choose an option: ", 1);
         }

         System.out.println(msg.SentMessage(option));

         if(option == 1){
            System.out.println("\n" + msg.getDetails());
         }
         else if(option == 2){
            //The message is only deleted once the user presses 0
            System.out.print("> ");
            while(!input.nextLine().trim().equals("0")){
               System.out.println("Press 0 to delete the message.");
               System.out.print("> ");
            }
            System.out.println("Message deleted.");
         }
      }

      System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
   }

   //Keeps asking until the user types a whole number that is at least the minimum
   public static int readNumber(Scanner input, String prompt, int minimum){
      while(true){
         System.out.print(prompt);
         try{
            int number = Integer.parseInt(input.nextLine().trim());
            if(number >= minimum){
               return number;
            }
         }
         catch(NumberFormatException e){
            //not a number, falls through and asks again
         }
         System.out.println("Please enter a whole number of " + minimum + " or more.");
      }
   }
}
class LogIn{

   String userName;
   String passWord;
   String cellPhoneNumber;

   //Stores the details of the successfully registered user
   String registeredUserName;
   String registeredPassWord;
   String registeredCellPhoneNumber;

   public boolean checkUserName(){ //checking user name method
      if(userName.contains("_") && userName.length() <= 5){ //Evaluates the user name
         return true;
      }
      else{
         return false;
      }
   }
   public boolean checkPasswordComplexity(){ //Password complexity check method
      String specialChars = "!@#$%^&*()\":{}|<>";
      
      boolean hasUpper = false;
      boolean hasDigit =  false;
      boolean hasSpecial = false;
      
      if(passWord.length() < 8){ //Evaluating the passwword length
         return false;
      }
      for(char c : passWord.toCharArray()){ //Iterating through the character array
         if(Character.isUpperCase(c)){ // Evaluating if there is atleast one uppercase character
            hasUpper = true;
         }
         if(specialChars.indexOf(c) >= 0){ // Evaluating if there is atleast one special character
            hasSpecial = true;
         }
         if(Character.isDigit(c)){ // Evaluating if there is atleast one digit in the password
            hasDigit = true;
         }
      }
      return hasUpper && hasDigit && hasSpecial;
        
   }
   public boolean checkCellPhoneNumber(){ //Evaluate the phone number
 
   
      if(cellPhoneNumber.startsWith("+27") && cellPhoneNumber.length() == 12){ //Evalautes if the number contains +27 and the length is 9(+27 is the first 3)
         return true; //return value if true
      }
      else{
         return false; //return value if false
      }
   }

   //Registers the user by saving their validated details as the "registered" account
   public String registerUser(){
      if(checkUserName() && checkPasswordComplexity() && checkCellPhoneNumber()){ //Evaluates if the username, password and cell number are all correct
         registeredUserName = userName;
         registeredPassWord = passWord;
         registeredCellPhoneNumber = cellPhoneNumber;
         return "User registered successfully. Welcome " + userName + "!";
      }
      else{
         return "Registration failed, please check your details and try again.";
      }
   }

   //Logs the user in by comparing entered details against the registered account
   public boolean loginUser(String enteredUserName, String enteredPassWord){
      if(registeredUserName == null || registeredPassWord == null){
         return false; //No one has registered yet
      }
      if(enteredUserName.equals(registeredUserName) && enteredPassWord.equals(registeredPassWord)){
         return true;
      }
      else{
         return false;
      }
   }

   //Returns the login status message based on the result of loginUser()
   public String returnLoginStatus(boolean loginSuccessful){
      if(loginSuccessful){
         return "Login successful, welcome back " + registeredUserName + ", it is great to see you again.";
      }
      else{
         return "Username or password incorrect, please try again.";
      }
   }
}
