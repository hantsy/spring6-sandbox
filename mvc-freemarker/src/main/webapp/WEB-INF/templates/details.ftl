<#import "/spring.ftl" as spring/>
<#import "template.ftl" as t/>
<@t.page title="Spring MVC Sample with Freemarker">
    <div class="page-header">
        <h1>POST #${details.id} </h1>
    </div>
    <div class="card">
        <div class="card-body">
            <div class="card-text">
                <dl class="row g-3">
                    <dt class="col-3 text-uppercase"> TITLE:</dt>
                    <dd class="col-9"> ${details.title}</dd>
                    <dt class="col-3 text-uppercase"> CONTENT:</dt>
                    <dd class="col-9"> ${details.content}</dd>
                    <dt class="col-3 text-uppercase"> Status:</dt>
                    <dd class="col-9"> ${details.status}</dd>
                    <dt class="col-3 text-uppercase"> Created At:</dt>
                    <dd class="col-9"> ${details.createdAt}</dd>
                </dl>
            </div>
            <a class="card-link" href="<@spring.url '/posts'/>"> Back to Home</a>
        </div>
    </div>

</@t.page>