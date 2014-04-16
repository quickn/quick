<#list columns?values as column>
		<td>${column.name}：</td>
		<td><input type="text" id="${javaName}__${column.javaName}" name="${javaName}__${column.javaName}" value="<#noparse>${</#noparse>${javaName}.${column.javaName}<#noparse>}</#noparse>"></td>
</#list>