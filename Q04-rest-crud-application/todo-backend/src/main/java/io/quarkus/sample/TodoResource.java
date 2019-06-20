package io.quarkus.sample;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import io.quarkus.panache.common.Sort;

@Path("/api")
@Produces("application/json")
@Consumes("application/json")
public class TodoResource {

    @OPTIONS
    @Operation(
            operationId = "todoOpt",
            summary = "Get the options")
    public Response opt() {
        return Response.ok().build();
    }

    @GET
    @Operation(
            operationId = "todoGetAll",
            summary = "Get the list of all Todos")
    @APIResponses(
            value = @APIResponse(
                            responseCode = "200",
                            description = "List of all Todos",
                            content = @Content(
                                    schema = @Schema(
                                            type = SchemaType.ARRAY,
                                            implementation = Todo.class))))
    public List<Todo> getAll() {
        return Todo.listAll(Sort.by("order"));
    }

    @GET
    @Path("/{id}")
    @Operation(
            operationId = "todoGetOne",
            summary = "Get one todo")
    @APIResponses(
            value = @APIResponse(
                            responseCode = "200",
                            description = "The requested Todo",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = Todo.class))))
    public Todo getOne(@PathParam("id") @Parameter(
            description = "The id of the todo",
            name = "id",
            example = "42",
            required = true,
            schema = @Schema(type = SchemaType.INTEGER, format = "int64")) Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
        return entity;
    }

    @POST
    @Transactional
    @Operation(
            operationId = "todoCreate",
            summary = "Create a new todo")
    @APIResponses(
            value = @APIResponse(
                            responseCode = "201",
                            description = "The created Todo",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = Todo.class))))
    public Response create(@Valid @RequestBody(
            description = "The todo to create",
            content = @Content(
                schema = @Schema(
                    implementation = Todo.class))) Todo item) {
        item.persist();
        return Response.status(Status.CREATED).entity(item).build();
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    @Operation(
            operationId = "todoUpdate",
            summary = "Update an exsiting todo")
    @APIResponses(
            value = @APIResponse(
                            responseCode = "200",
                            description = "The updated Todo",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = Todo.class))))
    public Response update(@Valid @RequestBody(
            description = "The todo to update",
            content = @Content(
                schema = @Schema(
                    implementation = Todo.class))) Todo todo, 
        @PathParam("id") @Parameter(
            description = "The id of the todo",
            name = "id",
            example = "42",
            required = true,
            schema = @Schema(type = SchemaType.INTEGER, format = "int64")) Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Item with id of " + id + " does not exist.", 404);
        }
        entity.id = id;
        entity.completed = todo.completed;
        entity.order = todo.order;
        entity.title = todo.title;
        entity.url = todo.url;
        return Response.ok(entity).build();
    }

    @DELETE
    @Transactional
    @Operation(
            operationId = "todoDeleteCompleted",
            summary = "Delete the all todos that are completed")
    @APIResponses(	
            value = @APIResponse(
                            responseCode = "204",
                            description = "No content"))
    public Response deleteCompleted() {
        Todo.deleteCompleted();
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @Operation(
            operationId = "todoDeleteOne",
            summary = "Delete one todo")
    @APIResponses(
            value = @APIResponse(
                            responseCode = "204",
                            description = "No content"))
    public Response deleteOne(@PathParam("id") @Parameter(
            description = "The id of the todo",
            name = "id",
            example = "42",
            required = true,
            schema = @Schema(type = SchemaType.INTEGER, format = "int64"))  Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
        entity.delete();
        return Response.noContent().build();
    }
}