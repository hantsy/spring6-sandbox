<#import "/spring.ftl" as spring/>
<#macro page title>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <!-- Required meta tags -->
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>${title?html}</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
              crossorigin="anonymous"/>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css"/>

        <link href="<@spring.url '/css/main.css'/>" rel="stylesheet"/>
    </head>

    <body>
    <nav class="navbar navbar-expand-lg sticky-top navbar-light bg-light">
        <div class="container-md">
            <a class="navbar-brand mr-2" href="<@spring.url '/posts'/>">
                <img class="logo" src="<@spring.url '/images/spring.svg'/>" />
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                    aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item mr-1 active">
                        <a class="nav-link" href="<@spring.url '/posts'/>">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<@spring.url '/posts/new'/>"><i class="bi bi-plus-circle"></i> New Post</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <div id="main" class="container-md">
        <#if flashMessage??>
            <div class="alert alert-${flashMessage.type} alert-dismissible fade show mt-2 mb-2" role="alert">
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                <div>${flashMessage.text}</div>
            </div>
        </#if>
        <#nested>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
            crossorigin="anonymous"></script>
    </body>
    </html>
</#macro>