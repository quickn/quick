				<form action="/simpleui/menu/save" method="post">
				<input type="hidden" name="token" value="${token!''}" />
				<input type="hidden" name="menu__id" value="${menu.id!''}" />
				<table width="100%">
					<tr>
						<td>上级菜单:</td>
						<td><select	name="menu__parentId">
							<option value="0" <#if menu.parentId==0>selected</#if> >根菜单</option>
						<#list menus as m>
							<option value="${m.id}" <#if menu.parentId==m.id>selected</#if> >${m.title}</option>
						</#list>
						</select> 

						<td>名称:</td>
						<td><input type="text" name="menu__title" class="txt" value="${menu.title!''}"/></td>
						<td>位置:</td>
						<td><input type="text" name="menu__position" value="${menu.position!'0'}" class="txt" size="5" /></td>
						<td><input type="checkbox" name="resource" value="true" <#if menu.resource??>checked</#if> />设为资源</td>
					</tr>
					<tr>
						<td colspan="1">URL:</td><td colspan="6"><input name="menu__url" width=100% value="${menu.url!''}" /></td>
					</tr>
					<tr>
						<td colspan="1">菜单html代码:</td><td colspan="6"><textarea name="menu__html" cols="61" rows="3">${menu.html!''}</textarea></td>
					</tr>
					<tr>
						<td colspan="7"><input type="submit" value="保  存"  class="btn" /></td>
					</tr> 
				</table>
				</form>
