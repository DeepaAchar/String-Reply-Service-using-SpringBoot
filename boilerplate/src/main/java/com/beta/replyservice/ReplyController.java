package com.beta.replyservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigInteger;
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 

@RestController
public class ReplyController {

	@GetMapping("/reply")
	public ReplyMessage replying() {
		return new ReplyMessage("Message is empty");
	}

	@GetMapping("/reply/{message}")
	public ReplyMessage replying(@PathVariable String message) {
		/* 
		* Split message into 2 digit rule and a String data 
		* '-' is the separator
		*/
		
		String rule;
		StringBuilder data;
		
		message = message.replaceAll("\\s", ""); 
		if(message.contains("-")){
			String [] parts = message.split("-");
			rule = parts[0];
			data = new StringBuilder(parts[1]);
		}
		else {
			throw new IllegalArgumentException("url" + message + " does not contain -");
		}
					
		/* 
		*  rule '1' implies reverse the data
		*  rule '2' implies MD5 hashing
		*/
		for(int i = 0 ; i < rule.length() ; i++){
			switch (rule.charAt(i)){
				case '1': data = reverseData(data);
						break;

				case '2': data = encodeInMD5(data);
						break;

				default : break;
			}
		}
		message = data.toString();
		return new ReplyMessage(message);
	}


	public static StringBuilder reverseData(StringBuilder data){
		return data.reverse();
	}


	public static StringBuilder encodeInMD5(StringBuilder data){
		try{
			//Get MD5 hashing instance for the input data
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			byte [] digestedData = md.digest(data.toString().getBytes());

			//Convert into signed number
			BigInteger bigIntData = new BigInteger(1, digestedData);

			//Convert to 32 bit hexadecimal number
			String hashedMessage = bigIntData.toString(16);
			while(hashedMessage.length() < 32){
				hashedMessage = "0" + hashedMessage;
			}
			return new StringBuilder(hashedMessage);
		}
		catch (NoSuchAlgorithmException e) { 
			throw new RuntimeException(e); 
		}
	}

}