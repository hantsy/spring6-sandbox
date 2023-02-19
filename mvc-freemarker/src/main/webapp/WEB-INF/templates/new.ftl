<#import "/spring.ftl" as spring/>
<#import "template.ftl" as t/>
<@t.page title="Spring MVC Sample with Freemarker">
    <div class="page-header">
        <h1> Create new POST</h1>
    </div>
    <div class="card">
        <div class="card-body">
            <form id="form" role="form" class="needs-validation" novalidate="true" action="<@spring.url '/posts'/>" method="post">

                <@spring.bind "post.title"/>
                <div class="mb-3">
                    <label class="control-label" for="title"> TITLE:</label>

                    <input id="title"
                           type="text"
                           class="form-control form-control-lg <#if spring.status.errorMessages?size gt 0>is-invalid</#if>"
                           name="${spring.status.expression}"
                           value="${spring.status.value!''}" required="required"></input>
                    <div class="invalid-feedback">
                        <#list spring.status.errorMessages as error> <p>${error}</p> </#list>
                    </div>
                </div>

                <@spring.bind "post.content"/>
                <div class="mb-3">

                    <label class="control-label" for="content">CONTENT:</label>
                    <textarea
                            id="content"
                            class="form-control <#if spring.status.errorMessages?size gt 0>is-invalid</#if>"
                            name="${spring.status.expression}"
                            value="${spring.status.value!''}"
                            rows="8" required="required"></textarea>
                    <div class="invalid-feedback">
                        <#list spring.status.errorMessages as error> <p>${error}</p> </#list>
                    </div>
                </div>

                <div class="mb-3">
                    <button id="submitTask" type="submit" class="btn btn-lg btn-primary">SAVE POST</button>
                </div>
            </form>
        </div>
    </div>
</@t.page>