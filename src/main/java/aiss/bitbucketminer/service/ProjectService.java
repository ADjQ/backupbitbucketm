package aiss.bitbucketminer.service;

import aiss.bitbucketminer.model.BitBucketCommit;
import aiss.bitbucketminer.model.BitBucketIssue;
import aiss.bitbucketminer.model.BitBucketProject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommitService commitService;

    @Autowired
    private IssueService issueService;

    @Value("${bitbucketminer.baseuri}")
    private String baseURI;

    @Value("${bitbucketminer.token}")
    private String token;

    private final List<BitBucketProject> projects = new ArrayList<>();

    public BitBucketProject getProject(String workspace, String repoSlug, int nCommits, int nIssues, int maxPages) throws JsonProcessingException {
        String url = baseURI + workspace + "/" + repoSlug;

        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al obtener el proyecto: " + response.getStatusCode());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode projectNode = objectMapper.readTree(response.getBody());

        if (projectNode == null) {
            throw new RuntimeException("Error: proyecto no encontrado.");
        }

        List<BitBucketCommit> commits = new ArrayList<>();
        List<BitBucketIssue> issues = new ArrayList<>();

        try {
            for (int page = 1; page <= maxPages; page++) {
                try {
                    List<BitBucketCommit> pageCommits = commitService.getAllCommits(workspace, repoSlug, nCommits, page);
                    commits.addAll(pageCommits);
                    if (pageCommits.size() < nCommits) {
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener commits de la página " + page + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener commits: " + e.getMessage());
        }

        try {
            for (int page = 1; page <= maxPages; page++) {
                try {
                    List<BitBucketIssue> pageIssues = issueService.getAllIssues(workspace, repoSlug, nIssues, page);
                    issues.addAll(pageIssues);
                    if (pageIssues.size() < nIssues) {
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener issues de la página " + page + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener issues: " + e.getMessage());
        }

        return new BitBucketProject(
                projectNode.get("uuid").asText(),
                projectNode.get("name").asText(),
                projectNode.get("links").get("html").get("href").asText(),
                commits,
                issues
        );
    }

    public BitBucketProject createProject(BitBucketProject project) {
        projects.add(project);
        return project;
    }
}
