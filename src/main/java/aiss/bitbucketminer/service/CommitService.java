package aiss.bitbucketminer.service;

import aiss.bitbucketminer.model.BitBucketCommit;
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
public class CommitService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${bitbucketminer.baseuri}")
    private String baseURI;

    @Value("${bitbucketminer.token}")
    private String token;

    public List<BitBucketCommit> getAllCommits(String workspace, String repoSlug, int nCommits, int maxPages) throws IOException {
        String url = baseURI + workspace + "/" + repoSlug + "/commits?pagelen=" + nCommits + "&page=" + maxPages;

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
        JsonNode commitsNode = rootNode.get("values");

        List<BitBucketCommit> commits = new ArrayList<>();
        if (commitsNode != null) {
            for (JsonNode commitNode : commitsNode) {
                BitBucketCommit commit = new BitBucketCommit();
                commit.setId(commitNode.get("hash").asText());
                commit.setTitle(commitNode.get("message").asText().split("\n")[0]);
                commit.setMessage(commitNode.get("message").asText());
                commit.setAuthorName(commitNode.get("author").get("user").get("display_name").asText());
                commit.setAuthorEmail(commitNode.get("author").get("raw").asText());
                commit.setAuthoredDate(commitNode.get("date").asText());
                commit.setWebUrl(commitNode.get("links").get("html").get("href").asText());
                commits.add(commit);
            }
        }
        return commits;
    }
}