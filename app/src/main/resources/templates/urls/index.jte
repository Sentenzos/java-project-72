@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(content = @`
    @if (page.getFlash() != null)
        <div>${page.getFlash()}</div>
    @endif
    @for(var url: page.getUrls())
        <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
    @endfor
`)
