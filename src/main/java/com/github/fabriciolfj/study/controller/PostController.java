package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.projection.PostWithUserRecord;
import com.github.fabriciolfj.study.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        PostWithUserRecord result = postService.findPostWithUser(id);
        return ResponseEntity.ok(toResponse(result));
    }

    @PatchMapping("/{id}/title")
    public ResponseEntity<Map<String, Object>> updateTitle(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        PostWithUserRecord result = postService.updateTitle(id, body.get("title"));
        return ResponseEntity.ok(toResponse(result));
    }

    // ── Helpers ───────────────────────────────────────────────

    private Map<String, Object> toResponse(PostWithUserRecord r) {
        return Map.of(
            "postId",    r.post().getId(),
            "title",     r.post().getTitle(),
            "createdBy", r.user() != null ? r.user().getFirstName() : "N/A"
        );
    }
}
