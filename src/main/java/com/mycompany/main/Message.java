package com.mycompany.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @ST10538709
 */
//P.O.E PART 2
public class Message{

   //Details of one message
   private String messageID;
   private int messageNumber; //The loop counter from Main, starts at 0
   private String recipient;
   private String messageText;
   private String messageHash;

   //Shared by every message while the program is running
   private static ArrayList<Message> sentMessages = new ArrayList<>();
   private static int totalSent = 0;
   private static String jsonFile = "messages.json"; //changed by the tests so they do not touch the real file
   private static final Random random = new Random();

   //Used by Main, the message ID is generated automatically
   public Message(int messageNumber){
      this.messageNumber = messageNumber;
      this.messageID = createMessageID();
   }

   //Used by the unit tests so I can pass in my own ID, recipient and message
   public Message(String messageID, int messageNumber, String recipient, String messageText){
      this.messageID = messageID;
      this.messageNumber = messageNumber;
      this.recipient = recipient;
      this.messageText = messageText;
   }

   //Builds a random ten digit ID, one digit at a time
   public String createMessageID(){
      String id = "";
      for(int i = 0; i < 10; i++){
         id = id + random.nextInt(10);
      }
      return id;
   }

   //Checks that the message ID is not more than ten characters
   public boolean checkMessageID(){
      return messageID != null && messageID.length() > 0 && messageID.length() <= 10;
   }

   //Reuses the cell phone check from the LogIn class (Part 1)
   public String checkRecipientCell(){
      LogIn checker = new LogIn();
      checker.cellPhoneNumber = recipient;
      if(recipient != null && checker.checkCellPhoneNumber()){
         return "Cell phone number successfully captured.";
      }
      else{
         return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
      }
   }

   //Checks the message is not longer than 250 characters
   public String checkMessageLength(){
      if(messageText.length() <= 250){
         return "Message ready to send.";
      }
      else{
         int extra = messageText.length() - 250;
         return "Message exceeds 250 characters by " + extra + "; please reduce the size.";
      }
   }

   //Hash = first two digits of the ID : message number : first word + last word, all in caps
   public String createMessageHash(){
      String[] words = messageText.trim().split("\\s+");
      String firstWord = words[0].replaceAll("[^A-Za-z0-9]", ""); //removes commas, question marks etc.
      String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");

      messageHash = (messageID.substring(0, 2) + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
      return messageHash;
   }

   //choice: 1 = send, 2 = disregard, 3 = store to send later
   public String SentMessage(int choice){
      if(choice == 1){
         sentMessages.add(this);
         totalSent++;
         return "Message successfully sent.";
      }
      else if(choice == 2){
         return "Press 0 to delete the message.";
      }
      else if(choice == 3){
         storeMessage();
         return "Message successfully stored.";
      }
      else{
         return "Invalid option.";
      }
   }

   //Details of this one message, in the order the task asks for
   public String getDetails(){
      return "Message ID: " + messageID + "\n"
           + "Message Hash: " + messageHash + "\n"
           + "Recipient: " + recipient + "\n"
           + "Message: " + messageText;
   }

   //Returns every message that was sent while the program has been running
   public static String printMessages(){
      if(sentMessages.isEmpty()){
         return "No messages have been sent yet.";
      }
      String all = "";
      for(Message m : sentMessages){
         all = all + m.getDetails() + "\n\n";
      }
      return all;
   }

   public static int returnTotalMessages(){
      return totalSent;
   }

   //Lets the unit tests save to a temporary file instead of messages.json
   public static void setStorageFile(String path){
      jsonFile = path;
   }

   //Saves the message in messages.json. The file is one JSON array, so every new
   //message is added before the closing bracket and the old ones stay in the file.
   //TODO: add the reference for the source you used to write to JSON
   public void storeMessage(){
      String entry = "  {\n"
                   + "    \"messageID\": \"" + escapeJson(messageID) + "\",\n"
                   + "    \"messageHash\": \"" + escapeJson(messageHash) + "\",\n"
                   + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                   + "    \"message\": \"" + escapeJson(messageText) + "\"\n"
                   + "  }";
      try{
         Path path = Paths.get(jsonFile);
         String existing = "";
         if(Files.exists(path)){
            existing = Files.readString(path).trim();
         }

         String newContent;
         if(existing.isEmpty() || existing.equals("[]")){
            newContent = "[\n" + entry + "\n]";
         }
         else{
            String withoutBracket = existing.substring(0, existing.lastIndexOf("]")).trim();
            newContent = withoutBracket + ",\n" + entry + "\n]";
         }
         Files.writeString(path, newContent);
      }
      catch(IOException e){
         System.out.println("The message could not be saved to " + jsonFile + ".");
      }
   }

   //Makes sure quotes, backslashes and new lines don't break the JSON
   private static String escapeJson(String text){
      if(text == null){
         return "";
      }
      return text.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
   }

   public String getMessageID(){
      return messageID;
   }
   public String getMessageHash(){
      return messageHash;
   }
   public String getRecipient(){
      return recipient;
   }
   public String getMessageText(){
      return messageText;
   }
   public void setRecipient(String recipient){
      this.recipient = recipient;
   }
   public void setMessageText(String messageText){
      this.messageText = messageText;
   }
}
