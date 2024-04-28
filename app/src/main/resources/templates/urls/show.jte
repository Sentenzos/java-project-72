@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.util.TimestampFormatter
@param UrlPage page

@template.layout.page(content = @`
    @if(page.getFlash() != null)
        <div>${page.getFlash()}</div>
    @endif
    <section>
        <div>${page.getUrl().getName()}</div>
        <div>${page.getUrl().getCreatedAt().toString()}</div>
        <div>${page.getUrl().getId()}</div>
    </section>
    <section>
        <form method="post" action="${NamedRoutes.urlChecks(page.getUrl().getId())}">
            <input type="submit" class="btn btn-primary" value="Запустить проверку">
        </form>
        @if (page.getChecks() != null)
            <div>
                @for(var check: page.getChecks())
                    <div>Status: ${check.getStatusCode()}</div>
                    <div>
                        Time: ${TimestampFormatter.format(check.getCreatedAt(), "dd/MM/yyyy HH:mm")}
                    </div>
                    <div>Title: ${check.getTitle()}</div>
                    <div>H1: ${check.getH1()}</div>
                    <div>Description: ${check.getDescription()}</div>
                @endfor
            </div>
        @endif
    </section>
`)