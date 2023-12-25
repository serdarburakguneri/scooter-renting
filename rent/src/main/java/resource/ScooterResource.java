package resource;

import dto.ScooterCreationDTO;
import dto.ScooterDTO;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import service.ScooterService;

@Path("/scooter")
@RequestScoped
@Authenticated
public class ScooterResource {

    private final ScooterService scooterService;

    @Inject
    public ScooterResource(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @WithTransaction
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Operation(
            summary = "List all scooters",
            description = "List all scooters"
    )
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(
            schema = @Schema(type = SchemaType.ARRAY, implementation = ScooterDTO.class))
    )
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    @Tag(name = "Scooter")
    @RolesAllowed({"admin", "user"})
    public Uni<RestResponse<List<ScooterDTO>>> list() {
        return scooterService.list()
                .onItem()
                .transform(scooters -> RestResponse.status(Status.OK, scooters));
    }

    @WithTransaction
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Operation(
            summary = "Create a scooter",
            description = "Create a scooter"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = ScooterCreationDTO.class, required = true),
                    examples = {
                            @ExampleObject(
                                    name = "Create a scooter",
                                    description = "Create a scooter.",
                                    value =
                                            """
                                                    {
                                                        "serialNumber": "ABC12345",
                                                        "brand": "Bird",
                                                        "model": "One"                                       
                                                    }
                                                    """
                            )
                    }
            ))
    @APIResponse(responseCode = "201", description = "Created", content = @Content(
            schema = @Schema(implementation = ScooterDTO.class))
    )
    @APIResponse(responseCode = "400", description = "Bad request")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    @Tag(name = "Scooter")
    @RolesAllowed("admin")
    public Uni<RestResponse<ScooterDTO>> create(@Valid ScooterCreationDTO request) {
        return scooterService.create(request)
                .onItem()
                .transform(scooterAdded -> RestResponse.status(Status.CREATED, scooterAdded));
    }
}
