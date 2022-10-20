<#-- Blocks macros-->
<#import "markdown.ftl" as md>

<#-- Coordinates Element -->
<#macro coordinates bundle level=1>
<@md.title title="Coordinates" level=level />

<@md.title title="Maven" level=level+1 />

<@mavenCoordinate mavenCoordinate=bundle.mavenCoordinate />

<@md.title title="OSGi" level=level+1 />

<@osgiCoordinate bsn=bundle.manifest.bundleSymbolicName.symbolicName version=bundle.manifest.bundleVersion/>

<#if bundle.checksum??>
<@md.title title="Checksum" level=level+1 />
<@checksum checksum=bundle.checksum/>
</#if>
</#macro>


<#-- mavenCoordinate Element -->
<#macro mavenCoordinate mavenCoordinate>
```xml
<dependency>
    <groupId>${mavenCoordinate.groupId}</groupId>
    <artifactId>${mavenCoordinate.artifactId}</artifactId>
    <version>${mavenCoordinate.version}</version>
<#if mavenCoordinate.classifier?has_content>
    <classifier>${mavenCoordinate.classifier}</classifier>
</#if>
</dependency>
```
</#macro>


<#-- osgiCoordinate Element -->
<#macro osgiCoordinate bsn version>
```
Bundle Symbolic Name: ${bsn}
Version             : <@osgiVersion version=version/>
```
</#macro>

<#-- osgiVersion Element -->
<#macro osgiVersion version>
${version.major}.${version.minor}.${version.micro}<#if version.qualifier?has_content>.${version.qualifier}</#if>
</#macro>
<#-- mavenCoordinate Element -->
<#macro checksum checksum>

</#macro>


<#-- componentList Element -->
<#macro componentList bundle>

| Component | Description |
| --------- | ----------- |
<#if bundle.components??>
<#list bundle.components as c>
| (${c.name})[../component/${c.name}.md] | <@componentPropertyValue component=c property="service.description"/> |
</#list>
</#if>

</#macro>

<#-- componentPropertyValue Element -->
<#macro componentPropertyValue component property>
<#if component.properties[property]?has_content>
<#if component.properties[property]["values"]?has_content>
${component.properties[property]["values"]?first}</#if></#if></#macro>



<#-- codeSnippets Element -->
<#macro codeSnippets codeSnippets level=1>

<#list codeSnippets as cs>

<@md.title title=cs.title level=level />

${cs.description}

<#if cs.codeSnippet?has_content>
<@md.code code=cs.codeSnippet language=cs.programmingLanguage />
</#if> 

<#if cs.steps?has_content>

<#list cs.steps as step>

<@md.title title=step.title level=level+1 />

${step.description}
<#if step?has_content>
<@md.code code=step.codeSnippet language=step.programmingLanguage />
</#if> 


</#list>
</#if> 

</#list>
</#macro>

