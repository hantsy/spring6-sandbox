<#import "/spring.ftl" as spring/>
<#import "template.ftl" as t/>
<@t.page title="Spring MVC Sample with Freemarker">
    <div class="page-header">
        <h1> Oops, something is wrong...</h1>
    </div>
    <div class="card">
        <div class="card-body">
            <div class="card-title"><i class="bi bi-bug"></i> Error Details</div>
            <div class="card-text">
                <p class="text-center">${ex}</p>
            </div>
        </div>
    </div>
</@t.page>
