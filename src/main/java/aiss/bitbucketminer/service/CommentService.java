package aiss.bitbucketminer.service;

import aiss.bitbucketminer.model.BitBucketComment;
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
public class CommentService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${bitbucketminer.baseuri}")
    private String baseURI;

    @Value("${bitbucketminer.token}")
    private String token;

    public List<BitBucketComment> getIssueComments(String workspace, String repoSlug, String issueNumber) throws IOException {
        String url = baseURI + workspace + "/" + repoSlug + "/issues/" + issueNumber + "/comments";

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
        JsonNode commentsNode = rootNode.get("values");

        List<BitBucketComment> comments = new ArrayList<>();
        if (commentsNode != null) {
            for (JsonNode commentNode : commentsNode) {
                BitBucketComment comment = new BitBucketComment();
                comment.setId(commentNode.get("id").asText());
                comment.setBody(commentNode.get("content").get("raw").asText());
                comment.setCreatedAt(commentNode.get("created_on").asText());
                comment.setUpdatedAt(commentNode.has("updated_on") ? commentNode.get("updated_on").asText() : null);

                JsonNode userNode = commentNode.get("user");
                if (userNode != null) {
                    BitBucketUser author = new BitBucketUser();
                    author.setId(userNode.get("account_id").asText());
                    author.setUsername(userNode.get("nickname").asText());
                    author.setName(userNode.has("display_name") ? userNode.get("display_name").asText() : null);
                    author.setAvatarUrl(userNode.get("links").get("avatar").get("href").asText());
                    author.setWebUrl(userNode.get("links").get("self").get("href").asText());
                    comment.setAuthor(author);
                }
                comments.add(comment);
            }
        }
        return comments;
    }
}
