package resource;

import adapter.RentalHistoryDTOAdapter;
import dto.RentRequestDTO;
import dto.RentalHistoryDTO;
import entity.UserRole;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import service.RentService;

@Path("/rent")
@RequestScoped
@Authenticated
public class RentResource {

    private final RentService rentService;

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    public RentResource(RentService rentService) {
        this.rentService = rentService;
    }

    @WithTransaction
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Operation(
            summary = "Rent a scooter",
            description = "Rent a scooter"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RentRequestDTO.class, required = true),
                    examples = {
                            @ExampleObject(
                                    name = "Rent a scooter",
                                    description = "Rent a scooter.",
                                    value =
                                            """
                                                    {
                                                        "scooterId": "dd4b91ea-3b80-4961-9bc3-1d6bc7f3f7cf",
                                                        "userId": "dd4b91ea-3b80-4961-9bc3-1d6bc7f3f7cf"                         
                                                    }
                                                    """
                            )
                    }
            ))
    @APIResponse(responseCode = "200", description = "Ok", content = @Content(
            schema = @Schema(implementation = RentalHistoryDTO.class))
    )
    @APIResponse(responseCode = "400", description = "Bad request")
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    @Tag(name = "Scooter")
    @RolesAllowed({UserRole.ADMIN, UserRole.USER})
    public Uni<RestResponse<RentalHistoryDTO>> requestRenting(@Valid RentRequestDTO request) {
        var userId = securityIdentity.getPrincipal().getName();
        return rentService.requestRenting(request, userId)
                .map(RentalHistoryDTOAdapter::fromRentalHistory)
                .onItem()
                .transform(rentalHistory -> RestResponse.status(Status.OK, rentalHistory));
    }

}
