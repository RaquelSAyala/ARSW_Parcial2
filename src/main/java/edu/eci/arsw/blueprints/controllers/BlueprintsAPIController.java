package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.ApiResponse;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    // GET /api/v1/blueprints
    @GetMapping
        @Operation(summary = "Get all blueprints")
        @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "All blueprints retrieved",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = edu.eci.arsw.blueprints.model.ApiResponse.class)))
        })
    public ResponseEntity<ApiResponse<Set<Blueprint>>> getAll() {
        Set<Blueprint> data = services.getAllBlueprints();
        ApiResponse<Set<Blueprint>> body = new ApiResponse<>(
                HttpStatus.OK.value(),
                "All blueprints retrieved",
                data
        );
        return ResponseEntity.ok(body);
    }

    // GET /api/v1/blueprints/{author}
    @GetMapping("/{author}")
        @Operation(summary = "Get blueprints by author")
        @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Blueprints by author retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No blueprints for author")
        })
    public ResponseEntity<ApiResponse<Set<Blueprint>>> byAuthor(@PathVariable String author) {
        try {
            Set<Blueprint> data = services.getBlueprintsByAuthor(author);
            ApiResponse<Set<Blueprint>> body = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Blueprints by author retrieved",
                    data
            );
            return ResponseEntity.ok(body);
        } catch (BlueprintNotFoundException e) {
            ApiResponse<Set<Blueprint>> body = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
    }

    // GET /api/v1/blueprints/{author}/{bpname}
    @GetMapping("/{author}/{bpname}")
        @Operation(summary = "Get blueprint by author and name")
        @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Blueprint retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Blueprint not found")
        })
    public ResponseEntity<ApiResponse<Blueprint>> byAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            Blueprint data = services.getBlueprint(author, bpname);
            ApiResponse<Blueprint> body = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Blueprint retrieved",
                    data
            );
            return ResponseEntity.ok(body);
        } catch (BlueprintNotFoundException e) {
            ApiResponse<Blueprint> body = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
    }

    // POST /api/v1/blueprints
    @PostMapping
        @Operation(summary = "Create a new blueprint")
        @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Blueprint created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid blueprint data")
        })
    public ResponseEntity<ApiResponse<Blueprint>> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            ApiResponse<Blueprint> body = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Blueprint created",
                    bp
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (BlueprintPersistenceException e) {
            ApiResponse<Blueprint> body = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }

    // PUT /api/v1/blueprints/{author}/{bpname}/points
    @PutMapping("/{author}/{bpname}/points")
        @Operation(summary = "Add a point to an existing blueprint")
        @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Point added to blueprint"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid point data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Blueprint not found")
        })
    public ResponseEntity<ApiResponse<Void>> addPoint(@PathVariable String author, @PathVariable String bpname,
                                      @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.x(), p.y());
            ApiResponse<Void> body = new ApiResponse<>(
                    HttpStatus.ACCEPTED.value(),
                    "Point added to blueprint",
                    null
            );
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(body);
        } catch (BlueprintNotFoundException e) {
            ApiResponse<Void> body = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}
