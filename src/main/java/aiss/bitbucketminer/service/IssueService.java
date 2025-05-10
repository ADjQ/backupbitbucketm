package aiss.bitbucketminer.service;

import aiss.bitbucketminer.model.BitBucketComment;
import aiss.bitbucketminer.model.BitBucketIssue;
import aiss.bitbucketminer.model.BitBucketUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class IssueService {

    @com.fasterxml.jackson.annotation.JsonPropertyOrder({
            "id",
            "title",
            "description",
            "state",
            "created_at",
            "updated_at",
            "closed_at",
            "labels",
            "votes"

    })


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommentService commentService;

    @Value("${bitbucketminer.baseuri}")
    private String baseURI;

    @Value("${bitbucketminer.token}")
    private String token;

    public List<BitBucketIssue> getAllIssues(String workspace, String repoSlug, int nIssues, int page) throws IOException {

        String url = baseURI + workspace + "/" + repoSlug + "/issues?pagelen=" + nIssues + "&page=" + page;

        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al obtener los issues: " + response.getStatusCode() + " - " + response.getBody());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        List<BitBucketIssue> issues = new ArrayList<>();
        for (JsonNode issueNode : rootNode.get("values")) {
            BitBucketIssue issue = new BitBucketIssue();
            issue.setId(issueNode.has("id") ? issueNode.get("id").asText() : null);
            issue.setTitle(issueNode.has("title") ? issueNode.get("title").asText() : null);
            issue.setDescription(issueNode.has("content") && issueNode.get("content").has("raw")
                    ? issueNode.get("content").get("raw").asText()
                    : null);
            issue.setState(issueNode.has("state") ? issueNode.get("state").asText() : null);
            issue.setCreatedAt(issueNode.has("created_on") ? issueNode.get("created_on").asText() : null);
            issue.setUpdatedAt(issueNode.has("updated_on") ? issueNode.get("updated_on").asText() : null);
            issue.setClosedAt(issueNode.has("closed_at") && !issueNode.get("closed_at").isNull()
                    ? issueNode.get("closed_at").asText()
                    : null);
            issue.setVotes(issueNode.has("votes") ? issueNode.get("votes").asInt() : 0);

            List<String> labels = new ArrayList<>();
            if (!issueNode.get("kind").isNull()) {
                labels.add(issueNode.get("kind").asText());
            }
            issue.setLabels(labels);

            JsonNode reporterNode = issueNode.get("reporter");
            if (reporterNode != null) {
                BitBucketUser author = new BitBucketUser();
                author.setId(reporterNode.has("account_id") ? reporterNode.get("account_id").asText() : null);
                author.setUsername(reporterNode.has("nickname") ? reporterNode.get("nickname").asText() : null);
                author.setName(reporterNode.has("display_name") ? reporterNode.get("display_name").asText() : null);
                author.setAvatarUrl(reporterNode.has("links") && reporterNode.get("links").has("avatar")
                        ? reporterNode.get("links").get("avatar").get("href").asText()
                        : null);
                author.setWebUrl(reporterNode.has("links") && reporterNode.get("links").has("self")
                        ? reporterNode.get("links").get("self").get("href").asText()
                        : null);
                issue.setAuthor(author);
            }

            JsonNode assigneeNode = issueNode.get("assignee");
            if (assigneeNode != null && !assigneeNode.isNull()) {
                BitBucketUser assignee = new BitBucketUser();
                assignee.setId(assigneeNode.has("account_id") ? assigneeNode.get("account_id").asText() : null);
                assignee.setUsername(assigneeNode.has("nickname") ? assigneeNode.get("nickname").asText() : null);
                assignee.setName(assigneeNode.has("display_name") ? assigneeNode.get("display_name").asText() : null);
                assignee.setAvatarUrl(assigneeNode.has("links") && assigneeNode.get("links").has("avatar")
                        ? assigneeNode.get("links").get("avatar").get("href").asText()
                        : null);
                assignee.setWebUrl(assigneeNode.has("links") && assigneeNode.get("links").has("self")
                        ? assigneeNode.get("links").get("self").get("href").asText()
                        : null);
                issue.setAssignee(assignee);
            } else {
                issue.setAssignee(null);
            }


            String issueNumber = issueNode.get("id").asText();
            try {
                List<BitBucketComment> comments = commentService.getIssueComments(workspace, repoSlug, issueNumber);
                issue.setComments(comments);
            } catch (Exception e) {
                issue.setComments(new ArrayList<>());
            }

            issues.add(issue);
        }
        return issues;
    }
}
