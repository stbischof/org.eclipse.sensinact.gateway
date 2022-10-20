<#-- Markdown element macros-->

<#-- Title Element -->
<#macro title title level=1 >
${""?left_pad(level, "#")} ${title?trim}
</#macro>



<#-- Multiline Code Element -->
<#macro code code language="">
```${language}
${code}
```
</#macro>