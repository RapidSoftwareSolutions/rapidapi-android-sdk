<p align="center">
  <img src="https://storage.googleapis.com/rapid_connect_static/static/github-header.png" width=350 />
</p>

## Overview
RapidAPI is the world's first opensource API marketplace. It allows developers to discover and connect to the world's top APIs more easily and manage multiple API connections in one place.

## Android SDK
* First of all, grab latest sdk via Gradle:

  ```java
    dependencies {
        compile 'com.rapidapi:rapidconnect-android:0.1'
    }
  ```

## Initialization
Then, import package in your code:

  ```java
  import com.rapidapi.rapidconnect.RapidApiConnect;
  ```
    
Now initialize it using:
    
  ```java
  RapidApiConnect connect = new RapidApiConnect("PROJECT_NAME", "API_KEY");
  ```
  
## Usage
To use any block in the marketplace, just copy it's code snippet and paste it in your code. For example, the following is the snippet for the **Calculate.add** block:

  ```java
    Map<String, Argument> body = new HashMap<String, Argument>();
    body.put("num1", new Argument("data", "11"));
    body.put("num2", new Argument("data","2"));
    
    try {
    
    	Map<String, Object> response = connect.call("Calculate", "Add", body);
	if(response.get("success") != null) {
		System.out.println("success: " + response.get("success"));
	} else{
		System.out.println("error: " + response.get("error"));
    	}
	
    } catch(Exception e){
    
	System.out.println("Error: " + e);
    }
    
  ```


**Notice** that the `error` event will also be called if you make an invalid block call (for example - the package you refer to does not exist).

## Using Files
Whenever a block in RapidAPI requires a file, you can either pass a URL to the file or a read stream.

#### URL
The following code will call the block MicrosoftComputerVision.analyzeImage with a URL of an image:

  ```java  
	    Map < String, Argument > body = new HashMap < String, Argument > ();

	    body.put("subscriptionKey", new Argument("data", "############################"));
	    body.put("image", new Argument("data", "https://i.ytimg.com/vi/tntOCGkgt98/maxresdefault.jpg"));

	    try {
	     Map < String, Object > response = connect.call("MicrosoftComputerVision", "analyzeImage", body);
	     if (response.get("success") != null) {
	      System.out.println("success: " + response.get("success"));
	     } else {
	      System.out.println("error: " + response.get("error"));
	     }
	    } catch (Exception e) {
	     System.out.println("Error: " + e);
	    }
  ```

#### Post File
If the file is locally stored, you can read it and pass the read stream to the block, but notice that we used `files` flag instead of `data` for "image" argument:

  ```java  
	Map < String, Argument > body = new HashMap < String, Argument > ();

	body.put("subscriptionKey", new Argument("data", "#############################"));
	body.put("image", new Argument("files", "/YOUR_PATH_TO_FILE_HERE/maxresdefault.jpg"));

	try {
	 Map < String, Object > response = connect.call("MicrosoftComputerVision", "analyzeImage", body);
	 if (response.get("success") != null) {
	  System.out.println("success: " + response.get("success"));
	 } else {
	  System.out.println("error: " + response.get("error"));
	 }
	} catch (Exception e) {
	 System.out.println("Error: " + e);
	}
  ```
  
#### Webhook Events
After setting up the webhook, you can listen to real-time events via websockets like so:

```java
    class Callbacks implements RapidApiConnect.WebhookEvents {
        public void onMessage(JsonElement msg) {
            System.out.println("Webhook event: " + msg);
        }

        public void onClose(int code, String reason) {
            System.out.println("Closed connection, code: " + code);
        }

        public void onError(Exception e) {
            System.out.println("Error: " + e);
        }
    }
    
    protected String doInBackground(String... params) {
        RapidApiConnect connect = new RapidApiConnect("Dashboard", "0b7f82c1-cf1d-4e02-af9a-de129afe54b2");

        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, Object> response = new HashMap<String, Object>();
        Callbacks callbacks = new Callbacks();
        parameters.put("token", "ydt3vFyVEoW51ZFCC2i5QKab");
        try {
            response = connect.listen("Slack", "slashCommand", parameters, callbacks);
            if(response.get("success") != null) {
                System.out.println(response);
            }
        } catch(Exception e) {
            System.out.println("!!!Error: " + e);
        }
        return "";
    }
        
##Issues:

As this is a pre-release version of the SDK, you may expirience bugs. Please report them in the issues section to let us know. You may use the intercom chat on rapidapi.com for support at any time.

##License:

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
