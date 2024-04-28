package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UrlPage {
    Url url;
    List<UrlCheck> checks;
    String flash;

    public UrlPage(Url url) {
        this.url = url;
    }
}
