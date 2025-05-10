package aiss.bitbucketminer.controller;

import aiss.bitbucketminer.model.BitBucketProject;
import aiss.bitbucketminer.service.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/bitbucket")
public class ProjectController {

    @Autowired
    private RestTemplate restTemplate;

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Value("${bitbucketminer.ncommits}")
    private int defaultNCommits;

    @Value("${bitbucketminer.nissues}")
    private int defaultNIssues;

    @Value("${bitbucketminer.maxpages}")
    private int defaultMaxPage;

    @GetMapping("/{workspace}/{repoSlug}")
    public ResponseEntity<?> getProject(
            @PathVariable String workspace,
            @PathVariable String repoSlug,
            @RequestParam(required = false) Integer nCommits,
            @RequestParam(required = false) Integer nIssues,
            @RequestParam(required = false) Integer maxPages) {
        try {
            BitBucketProject project = projectService.getProject(
                    workspace, repoSlug,
                    nCommits != null ? nCommits : defaultNCommits,
                    nIssues != null ? nIssues : defaultNIssues,
                    maxPages != null ? maxPages : defaultMaxPage
            );

            if (project == null) {
                return ResponseEntity.status(404).body("Proyecto no encontrado.");
            }
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }

    @PostMapping("/{workspace}/{repoSlug}")
    public ResponseEntity<String> sendProject(
            @PathVariable String workspace,
            @PathVariable String repoSlug,
            @RequestParam(required = false) Integer nCommits,
            @RequestParam(required = false) Integer nIssues,
            @RequestParam(required = false) Integer maxPages
    ) throws JsonProcessingException {

        BitBucketProject project = projectService.getProject(
                workspace, repoSlug,
                nCommits != null ? nCommits : defaultNCommits,
                nIssues != null ? nIssues : defaultNIssues,
                maxPages != null ? maxPages : defaultMaxPage
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BitBucketProject> requestEntity = new HttpEntity<>(project, headers);

        try {
            String gitMinerUrl = "http://localhost:8080/gitminer/projects";
            ResponseEntity<String> response = restTemplate.postForEntity(gitMinerUrl, requestEntity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending data to the main API: " + e.getMessage());
        }
    }
}
