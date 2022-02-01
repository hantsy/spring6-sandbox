<#import "/spring.ftl" as spring/>
<#import "template.ftl" as t/>
<@t.page title="Spring MVC Sample with Freemarker">
    <div class="page-header">
        <h1> POST LIST</h1>
    </div>
    <!-- List group -->
    <#if posts?? && (posts?size > 0)>
        <table class="table table-striped">
            <thead>
            <tr>
                <th scope="col">TITLE</th>
                <th scope="col">CREATED</th>
                <th scope="col">ACTION</th>
            </tr>
            </thead>
            <tbody>
            <#list posts as p>
                <tr>
                    <td>${p.title()}</td>
                    <td>${p.createdAt()}</td>
                    <td>
                        <ul class="actions">
                            <li>
                                <a class="btn btn-sm btn-primary" href="<@spring.url '/posts/'+p.id() />">
                                    <i class="bi bi-file-check"></i>
                                </a>
                            </li>
                            <li>
                                <a class="btn btn-sm btn-primary" href="<@spring.url '/posts/'+p.id()+'/edit' />">
                                    <i class="bi bi-pencil-square"></i>
                                </a>
                            </li>
                            <li>
                                <form action="<@spring.url '/posts/'+p.id() />" method="post">
                                    <input type="hidden" name="_method" value="DELETE"/>
                                    <button type="submit" class="btn btn-sm btn-danger"><i class="bi bi-trash"></i></button>
                                </form>
                            </li>
                        </ul>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    <#else>
        <div class="card">
            <div class="card-body">
                No posts found. Try to <a  href="<@spring.url '/posts/new'/>">create a new POST</a>.
            </div>
        </div>
    </#if>
</@t.page>
