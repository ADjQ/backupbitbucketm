package aiss.bitbucketminer.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "username",
    "name",
    "links"
})

public class BitBucketUser {

    @JsonProperty("id")
    private String id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("name")
    private String name;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("web_url")
    private String webUrl;


    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("avatar_url")
    public String getAvatarUrl() { return avatarUrl; }

    @JsonProperty("avatar_url")
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    @JsonProperty("web_url")
    public String getWebUrl() { return webUrl; }

    @JsonProperty("web_url")
    public void setWebUrl(String webUrl) { this.webUrl = webUrl; }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BitBucketUser.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("username");
        sb.append('=');
        sb.append(((this.username == null)?"<null>":this.username));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("avatar_url");
        sb.append('=');
        sb.append(((this.avatarUrl == null)?"<null>":this.avatarUrl));
        sb.append(',');
        sb.append("web_url");
        sb.append('=');
        sb.append(((this.webUrl == null)?"<null>":this.webUrl));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
