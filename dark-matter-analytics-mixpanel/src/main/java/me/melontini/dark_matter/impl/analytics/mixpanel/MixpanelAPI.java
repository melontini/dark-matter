package me.melontini.dark_matter.impl.analytics.mixpanel;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.analytics.mixpanel.interfaces.Mixpanel;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TODO (Union, Append, Remove) List properties
//TODO Group profiles (everything)
@ApiStatus.Internal
public class MixpanelAPI implements Mixpanel {
    private static final String BASE_URL = "https://api.mixpanel.com";
    private static final String EU_URL = "https://api-eu.mixpanel.com";
    private static final HttpClient CLIENT = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofMillis(2000)).proxy(ProxySelector.getDefault()).build();
    private final Holder holder;

    public MixpanelAPI(boolean eu, String projectToken) {
        this.holder = new Holder(projectToken, eu ? EU_URL : BASE_URL);
    }

    public void trackEvent(String userID, String eventName, JsonObject props) {
        JsonObject object = new JsonObject();
        object.addProperty("event", eventName);

        if (props == null) {
            props = new JsonObject();
        }

        if (!props.has("token")) props.addProperty("token", this.holder.token());
        if (!props.has("time")) props.addProperty("time", System.currentTimeMillis());
        if (!props.has("$insert_id")) props.addProperty("$insert_id", UUID.randomUUID().toString());
        if (userID != null && !props.has("distinct_id")) props.addProperty("distinct_id", userID);

        object.add("properties", props);

        call("/track?ip=0", object);
    }

    public void set(String userID, JsonObject props) {
        JsonObject object = profileDefaults(userID);
        object.add("$set", props);
        call("/engage?ip=0#profile-set", object);
    }

    public void setOnce(String userID, JsonObject props) {
        JsonObject object = profileDefaults(userID);
        object.add("$set_once", props);
        call("/engage?ip=0#profile-set-once", object);
    }

    public void add(String userID, Map<String, Long> increment) {
        JsonObject object = profileDefaults(userID);

        JsonObject props = new JsonObject();
        for (Map.Entry<String, Long> entry : increment.entrySet()) {
            props.addProperty(entry.getKey(), entry.getValue());
        }

        object.add("$add", props);
        call("/engage?ip=0#profile-numerical-add", object);
    }

    public void unset(String userID, String... props) {
        JsonObject object = profileDefaults(userID);
        JsonArray array = new JsonArray();
        for (String prop : props) {
            array.add(prop);
        }
        object.add("$unset", array);
        call("/engage?ip=0#profile-unset", object);
    }

    public void unset(String userID, List<String> props) {
        JsonObject object = profileDefaults(userID);
        JsonArray array = new JsonArray();
        for (String prop : props) {
            array.add(prop);
        }
        object.add("$unset", array);
        call("/engage?ip=0#profile-unset", object);
    }

    public void delete(String userID) {
        JsonObject object = profileDefaults(userID);
        object.add("$delete", JsonNull.INSTANCE);
        call("/engage?ip=0#profile-delete", object);
    }

    public JsonObject profileDefaults(String userID) {
        JsonObject object = new JsonObject();
        if (!object.has("$token")) object.addProperty("$token", this.holder.token());
        if (!object.has("$time")) object.addProperty("$time", System.currentTimeMillis());
        if (userID != null && !object.has("$distinct_id")) object.addProperty("$distinct_id", userID);
        return object;
    }

    private void call(String endpoint, JsonObject... objects) {
        JsonArray array = new JsonArray();
        for (JsonObject object : objects) {
            array.add(object);
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.holder.endpoint() + endpoint))
                    .header("accept", "text/plain")
                    .header("content-type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(array.toString()))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new RuntimeException("Status Code: " + response.statusCode() + " Body: " + response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private record Holder(String token, String endpoint) {

    }
}
