package aiss.bitbucketminer.controller.unitarios;

import aiss.bitbucketminer.model.BitBucketCommit;
import aiss.bitbucketminer.service.CommitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/bitbucket/{workspace}/{repo_slug}")
public class CommitController {

    private final CommitService commitService;

    @Value("${bitbucketminer.ncommits}")
    private int defaultNCommits;

    @Value("${bitbucketminer.maxpages}")
    private int defaultMaxPages;

    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping("/commits")
    public List<BitBucketCommit> getAllCommits(
            @PathVariable String workspace,
            @PathVariable String repo_slug,
            @RequestParam(required = false) Integer nCommits,
            @RequestParam(required = false) Integer maxPages) {

        int commits = (nCommits != null) ? nCommits : defaultNCommits;
        int page = (maxPages != null) ? maxPages : defaultMaxPages;

        try {
            return commitService.getAllCommits(workspace, repo_slug, commits, page);
        } catch (IOException e) {
            throw new RuntimeException("Error al obtener los commits", e);
        }
    }
}