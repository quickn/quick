<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
<title>UCenter Administrator's Control Panel</title>
<link rel="stylesheet" href="${ctx}/images/simpleui/simpleui.css" type="text/css" media="all" />
<script type="text/javascript" src="${ctx}/scripts/jquery-1.2.2.pack.js"></script>
<script type="text/javascript" src="${ctx}/scripts/jquery.bgiframe.pack.js"></script>
<script type="text/javascript" src="${ctx}/scripts/jmesa.js"></script>
		<script src="${ctx}/scripts/calendar.js" type="text/javascript"></script>
</head>
<body><div id="append"></div>


	<script type="text/javascript">
		function switchbtn(btn,url) {
			$('#srchdiv')[0].style.display = btn == 'srch' ? '' : 'none';
			$('#srchdiv')[0].className = btn == 'srch' ? 'tabcontentcur' : '' ;
			$('#srchbtn')[0].className = btn == 'srch' ? 'tabcurrent' : '';
			
			$('#adddiv')[0].style.display = btn == 'srch' ? 'none' : '';
			$('#adddiv')[0].className = btn == 'srch' ? '' : 'tabcontentcur';
			$('#addbtn')[0].className = btn == 'srch' ? '' : 'tabcurrent';
			if(btn =='add'){
				$('.hastabmenu')[0].style.height='200px';
				$('#adddiv').load(url);
			}else{
				$('.hastabmenu')[0].style.height='90px';
			}
		}
	</script>

	<div class="container">
	<#if errorMsg??>
			<div class="errormsg"><p>${errorMsg}</p></div>
	</#if>
	<#if message??>
			<div class="correctmsg"><p>${message}</p></div>
	</#if>
			<div class="hastabmenu">
				<ul class="tabmenu">
					<li id="srchbtn" class="tabcurrent"><a href="#" onclick="switchbtn('srch')">搜索菜单</a></li>
					<li id="addbtn"><a href="#" onclick="switchbtn('add','/simpleui/menu/create')">添加菜单</a></li>
				</ul>
				<div id="adddiv" class="tabcontent" style="display:none;">
				</div>
				<div id="srchdiv" class="tabcontentcur">
					<form action="/simpleui/menu/search" method="post">
					<table width="100%">
						<tr>
							<td>菜单名称:</td>
							<td><input type="text" name="menu__title" class="txt" /></td>
							<td><input type="submit" value="提 交" class="btn" /></td>
						</tr>
					</table>
					</form>
				</div>
			</div>

		
		<h3>菜单列表</h3>
		<div class="mainbox">
<form action="${ctx}/simpleui/menu/delete" method="post">
<input type="hidden" id="ids" name="ids" />
			${renderTable(menus,"id=menus:width=100%:pagesize=10",
			"name=selected:width=40px,
			 name=title:title=名称,
			 name=url:title=URL,
			 title=资源关联:format=\"@if{resource!=null}是@else{}否@end{}\",
			 title=操作:width=80px:format=\"<a href=/simpleui/menu/delete/@{id}>删除</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href=javascript:switchbtn('add','/simpleui/menu/edit/@{id}')>编辑</a>\"
			"
			)}
</form>
		</div>
	</div>


</body>
</html>