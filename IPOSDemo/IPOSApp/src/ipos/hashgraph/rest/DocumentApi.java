package ipos.hashgraph.rest;

import com.txmq.exo.core.ExoPlatformLocator;
import com.txmq.exo.messaging.ExoMessage;
import io.swagger.annotations.Api;
import ipos.hashgraph.IPOSAppState;
import ipos.hashgraph.model.Document;
import ipos.hashgraph.model.Documents;
import ipos.hashgraph.transaction.TransactionType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/Hashgraph/1.0.0")
@Api(value = "/Hashgraph/1.0.0", description = "Simple CRUD functions on Customer table")
public class DocumentApi {

	@GET
	@Path("/endpoints")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEndpoints() {
		IPOSAppState state = (IPOSAppState) ExoPlatformLocator.getPlatform().getState();
		return Response.ok().entity(state.getEndpoints()).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
    @Path("/state")
    public Response getState() {
        IPOSAppState state = (IPOSAppState) ExoPlatformLocator.getPlatform().getState();
        return Response.ok().entity(state).build();
    }


	@GET
	@Path("/documents")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocuments() {
        IPOSAppState state = (IPOSAppState) ExoPlatformLocator.getPlatform().getState();
		Documents result = new Documents();
		if(state.getDocuments().size() == 0) {
		    return Response.ok().entity(result).build();
        }

		result.setHash(state.getDocuments());
		return Response.ok().entity(result).build();
	}

	@POST
	@Path("/document")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addDocument(Document document) {
		ExoMessage message = new ExoMessage(new TransactionType(TransactionType.ADD_DOC), document);
        boolean transactionStatus = false;
		try {
		    transactionStatus = ExoPlatformLocator.getPlatform().createTransaction(message.serialize(), null);
        } catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(201).entity("{\n" +
                "  \"result\":"+transactionStatus+"\n" +
                "}").build();
	}

}
