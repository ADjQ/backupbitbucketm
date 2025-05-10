package aiss.bitbucketminer.controller.unitarios;

import aiss.bitbucketminer.model.BitBucketIssue;
import aiss.bitbucketminer.service.IssueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bitbucket")
public class IssueController {

    private final IssueService issueService;

    @Value("${bitbucketminer.nissues}")
    private int defaultNIssues;

    @Value("${bitbucketminer.maxpages}")
    private int defaultMaxPages;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/{workspace}/{repoSlug}/issues")
    public List<BitBucketIssue> getIssues(
            @PathVariable String workspace,
            @PathVariable String repoSlug,
            @RequestParam(required = false) Integer nIssues,
            @RequestParam(required = false) Integer maxPages) {

        int issues = (nIssues != null) ? nIssues : defaultNIssues;
        int page = (maxPages != null) ? maxPages : defaultMaxPages;

        try {
            return issueService.getAllIssues(workspace, repoSlug, issues, page);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los commits", e);
        }
    }
}