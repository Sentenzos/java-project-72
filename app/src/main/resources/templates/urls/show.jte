@import hexlet.code.dto.UrlPage
@param UrlPage page

@template.layout.page(content = @`
    <section>
        <div>${page.getUrl().getName()}</div>
        <div>${page.getUrl().getCreatedAt().toString()}</div>
        <div>${page.getUrl().getId()}</div>
    </section>
`)