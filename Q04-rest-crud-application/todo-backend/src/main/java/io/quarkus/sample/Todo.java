package io.quarkus.sample;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Schema(
        name = "Todo",
        type = SchemaType.OBJECT,
        description = "Object representing a todo")
public class Todo extends PanacheEntity {

    @NotBlank
    @Column(unique = true)
    @Schema(
            name = "title",
            description = "title of the todo",
            example = "My task")
    public String title;

    @Schema(
            name = "completed",
            description = "whether the todo is completed or not",
            example = "false")
    public boolean completed;

    @Column(name = "ordering")
    @Schema(
            name = "order",
            description = "order in the priority list",
            example = "10")
    public int order;

    @Schema(
            name = "url",
            description = "url associated with the todo")
    public String url;

    public static List<Todo> findNotCompleted() {
        return list("completed", false);
    }

    public static List<Todo> findCompleted() {
        return list("completed", true);
    }

    public static long deleteCompleted() {
        return delete("completed", true);
    }

}
