package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * @ST10538709
 */
//Unit tests for the Message class (Part 2)
public class MessageTest{

   //The test data from the task
   private static final String RECIPIENT_1 = "+27718693002";
   private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
   private static final String RECIPIENT_2 = "08575975889";
   private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

   @TempDir
   Path tempFolder;

   private Path jsonFile;

   //Messages are saved to a temporary file so the real messages.json is not changed
   @BeforeEach
   public void setUp(){
      jsonFile = tempFolder.resolve("test-messages.json");
      Message.setStorageFile(jsonFile.toString());
   }

   //Builds test message 1 with a fixed ID so the hash is always the same
   private Message testMessage1(){
      return new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
   }

   //Message length tests
   @Test
   public void messageLengthSuccess(){
      assertEquals("Message ready to send.", testMessage1().checkMessageLength());
   }

   @Test
   public void messageOf250CharactersIsAllowed(){
      Message msg = new Message("0012345678", 0, RECIPIENT_1, "a".repeat(250));
      assertEquals("Message ready to send.", msg.checkMessageLength());
   }

   @Test
   public void messageLengthFailure(){
      Message msg = new Message("0012345678", 0, RECIPIENT_1, "a".repeat(260));
      assertEquals("Message exceeds 250 characters by 10; please reduce the size.", msg.checkMessageLength());
   }

   //Recipient tests
   @Test
   public void recipientCorrectlyFormatted(){
      assertEquals("Cell phone number successfully captured.", testMessage1().checkRecipientCell());
   }

   @Test
   public void recipientIncorrectlyFormatted(){
      Message msg = new Message("0012345678", 1, RECIPIENT_2, TEXT_2);
      assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.", msg.checkRecipientCell());
   }

   //Message ID tests
   @Test
   public void messageIDIsCreatedWithTenDigits(){
      Message msg = new Message(0);
      String id = msg.getMessageID();

      assertEquals(10, id.length());
      assertTrue(id.matches("[0-9]+"));
      assertTrue(msg.checkMessageID());
   }

   @Test
   public void messageIDLongerThanTenCharactersFails(){
      Message msg = new Message("12345678901", 0, RECIPIENT_1, TEXT_1);
      assertFalse(msg.checkMessageID());
   }

   //Message hash tests
   @Test
   public void messageHashForTestMessage1(){
      assertEquals("00:0:HITONIGHT", testMessage1().createMessageHash());
   }

   //Tests the rest of the hashes in a loop
   @Test
   public void messageHashesInALoop(){
      String[] ids = {"0012345678", "0012345678", "0012345678", "4598765432"};
      String[] texts = {TEXT_1, TEXT_2, "Hi there, thanks", "Where are you? You are late! I have asked you to be on time."};
      String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT", "00:2:HITHANKS", "45:3:WHERETIME"};

      for(int i = 0; i < texts.length; i++){
         Message msg = new Message(ids[i], i, RECIPIENT_1, texts[i]);
         assertEquals(expected[i], msg.createMessageHash(), "Hash for message " + i);
      }
   }

   //Send, disregard and store tests
   @Test
   public void sendMessageIsAddedToSentMessages(){
      Message msg = testMessage1();
      msg.createMessageHash();
      int totalBefore = Message.returnTotalMessages();

      assertEquals("Message successfully sent.", msg.SentMessage(1));
      assertEquals(totalBefore + 1, Message.returnTotalMessages());
      assertTrue(Message.printMessages().contains(TEXT_1));
   }

   @Test
   public void disregardMessageIsNotCounted(){
      Message msg = new Message("0012345678", 1, RECIPIENT_2, TEXT_2);
      int totalBefore = Message.returnTotalMessages();

      assertEquals("Press 0 to delete the message.", msg.SentMessage(2));
      assertEquals(totalBefore, Message.returnTotalMessages());
   }

   @Test
   public void storeMessageSavesToJsonFile() throws IOException{
      Message msg = testMessage1();
      msg.createMessageHash();
      int totalBefore = Message.returnTotalMessages();

      assertEquals("Message successfully stored.", msg.SentMessage(3));
      assertEquals(totalBefore, Message.returnTotalMessages()); //stored messages are not counted as sent

      String content = Files.readString(jsonFile);
      assertTrue(content.startsWith("["));
      assertTrue(content.endsWith("]"));
      assertTrue(content.contains("00:0:HITONIGHT"));
      assertTrue(content.contains(RECIPIENT_1));
      assertTrue(content.contains(TEXT_1));
   }

   @Test
   public void storingTwoMessagesKeepsBothInTheFile() throws IOException{
      Message first = testMessage1();
      first.createMessageHash();
      first.storeMessage();

      Message second = new Message("0012345678", 1, RECIPIENT_1, TEXT_2);
      second.createMessageHash();
      second.storeMessage();

      String content = Files.readString(jsonFile);
      assertTrue(content.contains("00:0:HITONIGHT"));
      assertTrue(content.contains("00:1:HIPAYMENT"));
      assertEquals(1, content.split("\\[", -1).length - 1); //only one opening bracket, so it is one JSON array
   }

   @Test
   public void quotesInMessageAreEscapedInJson() throws IOException{
      Message msg = new Message("0012345678", 0, RECIPIENT_1, "Say \"hello\" now");
      msg.createMessageHash();
      msg.storeMessage();

      assertTrue(Files.readString(jsonFile).contains("Say \\\"hello\\\" now"));
   }

   @Test
   public void invalidSendOption(){
      assertEquals("Invalid option.", testMessage1().SentMessage(9));
   }

   //Message details are shown in the order: ID, Hash, Recipient, Message
   @Test
   public void messageDetailsAreInTheCorrectOrder(){
      Message msg = testMessage1();
      msg.createMessageHash();

      String expected = "Message ID: 0012345678\n"
                      + "Message Hash: 00:0:HITONIGHT\n"
                      + "Recipient: +27718693002\n"
                      + "Message: Hi Mike, can you join us for dinner tonight?";
      assertEquals(expected, msg.getDetails());
   }
}
