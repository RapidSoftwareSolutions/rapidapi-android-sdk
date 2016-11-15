#RapidAPI Connect - ANDROID SDK

This SDK allows you to connect to RapidAPI blocks from your java app. To start off, follow the following guide:

[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.png?v=103)](https://opensource.org/licenses/mit-license.php)
[![forthebadge](http://forthebadge.com/images/badges/built-by-developers.svg)](http://forthebadge.com)

##Set-up:

First of all, grab latest sdk via Gradle:

    dependencies {
        classpath 'com.rapidapi:rapidconnect-android:0.1'
    }

Then, import package in your code:

    import com.rapidapi.rapidconnect-android.RapidApiConnect;

Once imported, the last step is to initialize the SDK with your project name and project API Key:

    RapidApiConnect connect = new RapidApiConnect("PROJECT_NAME", "API_KEY");

That's all, your SDK is set up! You can now use any block by copying the code snippet from the marketplace.

##Usage:

First of all, we will prepare data, we will use HashMap You can add as many arguments, as api you will call need.

	Map<String, Argument> body = new HashMap<String, Argument>();

    body.put("num1", new Argument("data", "11"));
    body.put("num2", new Argument("data","2"));

Note: you should always use "data" key, unless files that will be streamming from you side, then key will be "files".

Than, just copy it's code snippet and paste it in your code. For example, the following will call the **Calculate.add** block, and print the result:

```
    try{
     Map<String, Object> response = connect.call("Calculate", "Add", body);

	    if(response.get("success") != null){
	    	System.out.println("success: " + response.get("success"));
	    }else{
      	System.out.println("error: " + response.get("error"));
      }
    }catch(Exception e){
	    	System.out.println("Error: " + e);
    }

```

The printed result will be:

```
  success: 13.0
```

**Notice** that the `error` event will also be called if you make an invalid block call (for example - the package you refer to does not exist).

##Files:
Whenever a block in RapidAPI requires a file, you can either pass a URL to the file or a read stream.

###URL:
The following code will call the block MicrosoftComputerVision.analyzeImage with a URL of an image:

```
	    Map<String, Argument> body = new HashMap<String, Argument>();

    	body.put("subscriptionKey", new Argument("data", "############################"));
    	body.put("image", new Argument("data","https://i.ytimg.com/vi/tntOCGkgt98/maxresdefault.jpg"));

	    try{
	    	Map<String, Object> response = connect.call("MicrosoftComputerVision", "analyzeImage", body);
	    	if(response.get("success") != null){
	    		System.out.println("success: " + response.get("success"));
	    	}else{
      			System.out.println("error: " + response.get("error"));
      		}
	    }catch(Exception e){
	    	System.out.println("Error: " + e);
	    }

```

###Read Stream
If the file is locally stored, you can read it using `CURLFile` and pass the read stream to the block, like the following:
```
		Map<String, Argument> body = new HashMap<String, Argument>();

    	body.put("subscriptionKey", new Argument("data", "#############################"));
    	body.put("image", new Argument("files","/YOUR_PATH_TO_FILE_HERE/maxresdefault.jpg"));

	    try{
	    	Map<String, Object> response = connect.call("MicrosoftComputerVision", "analyzeImage", body);
	    	if(response.get("success") != null){
	    		System.out.println("success: " + response.get("success"));
	    	}else{
      			System.out.println("error: " + response.get("error"));
      		}
	    }catch(Exception e){
	    	System.out.println("Error: " + e);
	    }
```

The printed result of will be:

```
success: {"categories":[{"name":"animal_cat","score":0.99609375}],"requestId":"fb3663f4-5d94-445a-9a4a-5effc1042ff3","metadata":{"width":1600,"height":1200,"format":"Jpeg"}}
```

RapidAPI uses the [form-data](https://github.com/form-data/form-data) library by [@felixge](https://github.com/felixge) to handle files, so please refer to it for more information.

##Issues:

As this is a pre-release version of the SDK, you may expirience bugs. Please report them in the issues section to let us know. You may use the intercom chat on rapidapi.com for support at any time.

##Licence:

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
