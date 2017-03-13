package com.rapidapi.rapidconnect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.phoenixframework.channels.*;

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

    public interface WebhookEvents {
        void onMessage(JsonNode msg);
        void onClose(int code, String reason);
        void onError(String reason);
        void onJoin();
    };

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

    private static String callbackBaseUrl() { return "https://webhooks.rapidapi.com"; }

    private static String websocketBaseUrl() { return "wss://webhooks.rapidapi.com"; }

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

    public Map listen(String pack, String block, Map<String, String> parameters, final WebhookEvents callbacks) throws IOException {
        Map<String, Object>result =  new HashMap<>();
        final Gson gson = new Gson();
        final String userId = pack + "." + block + "_" + this.project + ":" + this.key;
        String url = RapidApiConnect.callbackBaseUrl() + "/api/get_token?user_id=" + userId + "&params=" + gson.toJson(parameters);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "RapidAPIConnect_Java")
                .addHeader("Authorization", Credentials.basic(this.project, this.key))
                .get()
                .build();

        try (Response response = this.client.newCall(request).execute()) {
            final Map<String, Object> map = gson.fromJson(response.body().string(), new TypeToken<Map<String, Object>>(){}.getType());
            try {
                String socket_url = RapidApiConnect.websocketBaseUrl() + "/socket/websocket?token=" + map.get("token");
                final String token = (String)map.get("token");
                Socket socket;
                Channel channel;

                socket = new Socket(socket_url);
                socket.connect();
                ObjectMapper mapper = new ObjectMapper();
                channel = socket.chan("users_socket:" + userId, mapper.convertValue(parameters, JsonNode.class));
                channel.join()
                        .receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                callbacks.onJoin();
                            }
                        });

                channel.on("new_msg", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        if (envelope.getPayload().get("token") == null) {
                            callbacks.onError("Token error");
                        } else if (token.equals(envelope.getPayload().get("token").asText())) {
                            callbacks.onMessage(envelope.getPayload().get("body"));
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }
}
