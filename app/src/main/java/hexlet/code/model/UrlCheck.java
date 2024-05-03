package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class UrlCheck {
    private Long id;
    private Integer statusCode;
    private String title;
    private String h1;
    private String description;
    private Long urlId;
    private Timestamp createdAt;
}
