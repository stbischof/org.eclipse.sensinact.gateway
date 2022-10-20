<#import "markdown.ftl" as md>
<#import "blocks.ftl" as block>

---
bundle.symbolic.name: ${bundle.manifest.bundleSymbolicName.symbolicName}
bundle.version: <@block.osgiVersion version=bundle.manifest.bundleVersion/>
---
<@md.title title=bundle.manifest.bundleName/>

*Bundle-SymbolicName:* ${bundle.manifest.bundleSymbolicName.symbolicName}
*Bundle-Version:* <@block.osgiVersion version=bundle.manifest.bundleVersion/>
*Bundle-Description:*
${bundle.manifest.bundleDescription}

<@block.coordinates bundle=bundle/>


<@md.title title="Components"/>

list of components with link t components site
<@block.componentList bundle=bundle/>




<@md.title title="Gogo Commands"/>

list of Gogo Commands with list gogocommand site