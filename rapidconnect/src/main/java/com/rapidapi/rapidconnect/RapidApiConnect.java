package com.rapidapi.rapidconnect;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RapidApiConnect {
  private static final RequestBody EMPTY_REQUEST_BODY = RequestBody.create(MediaType.parse("text/plain"), "");
  private final OkHttpClient client;
  private final String project;
  private final String key;

  public RapidApiConnect(String project, String key) {
    this.client = new OkHttpClient();
    this.project = project;
    this.key = key;
  }

  /**
  * Returns the base URL for block calls
  *
  * @return string Base URL for block calls
  */
  private static String getBaseUrl()
  {
    return "https://rapidapi.io/connect";
  }

  /**
  * Build a URL for a block call
  *
  * @param pack Package where the block is
  * @param block Block to be called
  * @return string Generated URL
  */
  public static String blockUrlBuild(String pack, String block)
  {
    return RapidApiConnect.getBaseUrl() + "/" + pack + "/" + block;
  }

  /**
  * Call a block
  *
  * @param pack Package of the block
  * @param block Name of the block
  * @param body Arguments to send to the block (Map)
  * @return Map
  */
  public Map<String, Object> call(String pack, String block, Map<String, Argument> body) throws IOException {
    Map<String, Object> result = new HashMap<>();

    RequestBody requestBody;
    if (body == null || body.isEmpty()) {
      requestBody = EMPTY_REQUEST_BODY;
    } else {
      MultipartBody.Builder builder = new MultipartBody.Builder()
              .setType(MultipartBody.FORM);

      Set<Map.Entry<String, Argument>> entrySet = body.entrySet();
      for (Map.Entry<String, Argument> entry : entrySet) {
        Argument argument = entry.getValue();
        if ("data".equals(argument.getType())) {
          builder.addFormDataPart(entry.getKey(), argument.getValue());
        } else {
          File file = new File(argument.getValue());
          if (file.exists() && file.isFile()) {
            builder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(MultipartBody.FORM, file));
          } else {
            result.put("error", "File not exist or can't be read.");

            return result;
          }
        }
      }
      requestBody = builder.build();
    }

    Request request = new Request.Builder()
      .url(RapidApiConnect.blockUrlBuild(pack, block))
      .addHeader("User-Agent", "RapidAPIConnect_Java")
      .addHeader("Authorization", Credentials.basic(this.project, this.key))
      .post(requestBody)
      .build();

    try (Response response = this.client.newCall(request).execute()) {
      Gson gson = new Gson();

      Map<String, Object> map = gson.fromJson(response.body().string(), new TypeToken<Map<String, Object>>(){}.getType());

      if(response.code() != 200 || "error".equals(map.get("outcome"))){
        result.put("error", map.get("payload"));

        return result;
      }else{
        result.put("success", map.get("payload"));

        return result;
      }
    }
  }
}