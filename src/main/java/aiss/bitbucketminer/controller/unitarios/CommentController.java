package aiss.bitbucketminer.controller.unitarios;

import aiss.bitbucketminer.model.BitBucketComment;
import aiss.bitbucketminer.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bitbucket/{workspace}/{repo_slug}")
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/issues/{issueNumber}/comments")
    public List<BitBucketComment> getComments(
            @PathVariable String workspace,
            @PathVariable String repo_slug,
            @PathVariable String issueNumber
    ) throws RuntimeException {
        try{
            return commentService.getIssueComments(workspace, repo_slug, issueNumber);
        } catch (Exception e){
            throw new RuntimeException("Error al obtener los commits", e);
        }
    }
}
