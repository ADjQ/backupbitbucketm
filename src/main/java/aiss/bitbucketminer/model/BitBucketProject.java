package aiss.bitbucketminer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "web_url",
    "commits",
    "issues"
})
public class BitBucketProject {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("commits")
    private List<BitBucketCommit> commits;
    @JsonProperty("issues")
    private List<BitBucketIssue> issues;

    public BitBucketProject(String id, String name, String webUrl, List<BitBucketCommit> commits, List<BitBucketIssue> issues) {
        this.id = id;
        this.name = name;
        this.webUrl = webUrl;
        this.commits = commits;
        this.issues = issues;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("web_url")
    public String getLinks() {
        return webUrl;
    }

    @JsonProperty("web_url")
    public void setLinks(String links) {
        this.webUrl = webUrl;
    }

    @JsonProperty("commits")
    public List<BitBucketCommit> getCommits() { return commits; }

    @JsonProperty("commits")
    public void setCommits(List<BitBucketCommit> commits) { this.commits = commits; }

    @JsonProperty("issues")
    public List<BitBucketIssue> getIssues() { return issues; }

    @JsonProperty("issues")
    public void setIssues(List<BitBucketIssue> issues) { this.issues = issues; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BitBucketProject.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("web_url");
        sb.append('=');
        sb.append(((this.webUrl == null)?"<null>":this.webUrl));
        sb.append(',');
        sb.append("commits");
        sb.append('=');
        sb.append(((this.commits == null)?"<null>":this.commits));
        sb.append(',');
        sb.append("issues");
        sb.append('=');
        sb.append(((this.issues == null)?"<null>":this.issues));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
