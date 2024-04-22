@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.MainPage
@param MainPage page = null

@template.layout.page(
content = @`
    @if (page.getFlash() != null)
        <div>${page.getFlash()}</div>
    @endif
    <section>
        <form action="${NamedRoutes.urlsPath()}" method="post">
            <input placeholder="Ссылка" type="text" class="form-control" name="url">
            <input type="submit" class="btn btn-primary" value="Проверить" />
        </form>
    </section>
`
)