package pt.studymatch.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.studymatch.dto.GroupingContextCreateRequest;
import pt.studymatch.dto.GroupingContextResponse;
import pt.studymatch.dto.GroupingContextStudentsRequest;
import pt.studymatch.service.GroupingContextService;

import java.util.List;

@RestController
@RequestMapping("/api/grouping-contexts")
public class GroupingContextController {

    private final GroupingContextService groupingContextService;

    public GroupingContextController(
        GroupingContextService groupingContextService
    ) {
        this.groupingContextService = groupingContextService;
    }

    @PostMapping
    public ResponseEntity<GroupingContextResponse> create(
        @Valid @RequestBody GroupingContextCreateRequest request
    ) {
        GroupingContextResponse response =
            groupingContextService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupingContextResponse>> findAll() {
        return ResponseEntity.ok(groupingContextService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupingContextResponse> findById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            groupingContextService.findById(id)
        );
    }

    @PutMapping("/{id}/students")
    public ResponseEntity<GroupingContextResponse> replaceStudents(
        @PathVariable Long id,
        @Valid @RequestBody GroupingContextStudentsRequest request
    ) {
        return ResponseEntity.ok(
            groupingContextService.replaceStudents(id, request)
        );
    }
}
