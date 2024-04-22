@import hexlet.code.dto.UrlsPage
@param UrlsPage page

@template.layout.page(content = @`
    @if (page.getFlash() != null)
        <div>${page.getFlash()}</div>
    @endif
    @for(var url: page.getUrls())
        <div>${url.getName()}</div>
    @endfor
`)
