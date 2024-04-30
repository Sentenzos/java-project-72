package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UrlPage extends BasePage {
    Url url;
    List<UrlCheck> checks;

    public UrlPage(Url url) {
        this.url = url;
    }
}
