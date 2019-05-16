<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.beans.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.SQLException"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="layout.css" rel="stylesheet" type="text/css" />
<style>@import url(//fonts.googleapis.com/earlyaccess/notosansmyanmar.css);</style>
<style type="text/css">
	.orig{font-family:"Noto Sans Myanmar",arial,sans-serif!important}
</style>
<title>管理员首页</title>
<script type="text/javascript">
function modifyStu(){
    var isMod=confirm("确定要查看该条语料吗?");    
    if(!isMod)
     return false;
   }
   function deleteStu(){
    var isDel=confirm("确定要删除该条语料吗?");   
    if(!isDel)
     return false;
   }
</script>
</head>
<body>
    <%String user = (String)request.getParameter("user");%>
	<div id="container">
		<div id="header">
			<br>
			<h1 align="center">缅语文本-图像语料标注系统</h1>

		</div>
		<div id="mainContent">
			<div id="sidebar">
				<a href="ADindex.jsp" target="_parent">首页</a><br> <a
					href="UserManage.jsp" target="_parent">用户管理</a><br> <a
					href="index.jsp" target="_parent">退出</a><br>
			</div>
			<div id="content">
				<div>
					<form method="post" action="ADQueryCropus.do">
						&nbsp;&nbsp;语料检索 注意：任意填写一个字段即可检索，其中创建时间格式为(yyyy/MM/dd)，如2019/05/01	
						<br> <select name="QueryCategory">
							<option value="">--请选择查询类型--</option>
							<option value="ChineseKey">文本关键字</option>
							<option value="BurmeseKey">缅文关键字</option>
							<option value="Operator">录入人员</option>
							<option value="createtime">录入日期</option>
						</select> <input name="textname" type="text"> &nbsp;&nbsp;<input
							type="submit"
							style="background: #9ACD32; width: 100px; height: 30px; font-size: 18px;"
							value="检索">
					</form>
				</div>

				<div>
					<table border="1" width="100%">
						<tr>
							<td align="center" width=8%>序号</td>
							<td align="center" width=16%>图像</td>
							<td align="center" width=16%>文本</td>
							<td align="center" width=8%>操作人员</td>
							<td align="center" width=16%>录入日期</td>
							<td align="center" width=12%>查看</td>
							<td align="center" width=12%>删除</td>
						</tr>
						<%
                     try {
                    	 	Connection con = null;
                    	 	java.sql.Statement stmt =null; 
                    		ResultSet rs = null;
                    	 try
                 		{
                 			Class.forName("com.mysql.jdbc.Driver");
                 			System.out.println("驱动加载成功");
                 			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/WebsiteUser?charsetEncoding=utf-8","root","1234");
                 			System.out.println("数据库连接成功");
                 		}
                 		catch(Exception ex)
                 		{
                 			System.out.println("数据库连接失败");
                 			ex.printStackTrace();
                 		}
                       //创建statement
                      	stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
                        //执行查询
 						String sqlADQueryCropus=(String)request.getAttribute("sqlADQueryCropus");
 						//sqlQueryCropus=sqlQueryCropus.replaceAll("\"","\'");
 						System.out.println("传递之后1"+sqlADQueryCropus);
						if(sqlADQueryCropus==null)
						{
							sqlADQueryCropus=request.getParameter("sqlQueryCropus");
							//sqlQueryCropus=sqlQueryCropus.replaceAll("\"","\'");
							System.out.println("传递之后2"+sqlADQueryCropus);
							if(sqlADQueryCropus==null)
								System.out.println("查询语句为空");
								sqlADQueryCropus="select * from Corpus order by cast(ID as char)";
						}
						
						System.out.println("sqlADQueryCropus"+sqlADQueryCropus);
                        rs = stmt.executeQuery(sqlADQueryCropus);

                      int intPageSize; //一页显示的记录数
                       int intRowCount; //记录的总数
                       int intPageCount; //总页数
                       int intPage; //待显示的页码
                        String strPage;
                        int i;
                         //设置一页显示的记录数
                        intPageSize = 3;
                        //取得待显示的页码
                        strPage = request.getParameter("page");
                        //判断strPage是否等于null,如果是，显示第一页数据
                        if (strPage == null) {
                             intPage = 1;
                         } else {
                            //将字符串转换为整型
                            intPage = java.lang.Integer.parseInt(strPage);
                        }
                        if (intPage < 1) {
                            intPage = 1;
                        }
                         //获取记录总数
                         rs.last();
                         intRowCount = rs.getRow();
                         //计算机总页数
                         intPageCount = (intRowCount + intPageSize - 1) / intPageSize;
                         //调整待显示的页码
                        if (intPage > intPageCount)
                             intPage = intPageCount;
                        if (intPageCount > 0) {
                             //将记录指针定位到待显示页的第一条记录上
                             rs.absolute((intPage - 1) * intPageSize + 1);
                         }
                         //下面用于显示数据
                         i = 0;
                         while (i < intPageSize && !rs.isAfterLast()) {
                        	 String ID=rs.getObject(1).toString();
                        	 String Chinese=rs.getObject(2).toString();
                        	 //if(Chinese.length()>10)
                        		// Chinese=Chinese.substring(0,10)+"...";
                        	 String Burmese=rs.getObject(3).toString();
                        	 if(Burmese.length()>10)
                        		 Burmese=Burmese.substring(0,10)+"...";
                        	 String Name=rs.getObject(4).toString();
                        	 String createtime=rs.getObject(5).toString();
                        	  
                        	 int xuhao=i+(intPage-1)*intPageSize+1;
                 %>
						<tr>
							<td width=8% align="center"><%=xuhao%></td>
							<td width=16% align="center">
							<img src="http://localhost:8080/MCM/upload/<%=Chinese%>" style="width: 50%;" />
							</td>
							<td width=16% align="center" class="orig"><%=Burmese%></td>
							<td width=8% align="center"><%=Name%></td>
							<td width=16% align="center"><%=createtime%></td>
							<td width=12% algin="center" onclick="return modifyStu()"><a
								href="ADDisplayCorpus.jsp?ID=<%=rs.getObject(1)%>">查看</a></td>
							<td width=12% algin="center" onclick="return deleteStu()"><a
								href="ADDeleCorpus.do?ID=<%=rs.getObject(1)%>">删除</a></td>
						</tr>
						<%
                    rs.next();
                             i++;
                         }
                         //关闭连接、释放资源
                         rs.close();
                         stmt.close();
                         con.close();
                 %>
					</table>
					<table border="1" width="100%">
						<tr>
							<td width="100%" style="text-align: center" colspan="2"
								align="center">共<span class="bluefont"><%=intRowCount%></span>个记录，分<span
								class="bluefont"><%=intPageCount%></span>页显示，当前页是：第<span
								class="bluefont"><%=intPage%></span>页 <span class="bluefont">
									<%
                        		//sqlQueryCropus=sqlQueryCropus.replaceAll("\"","\'");
                        	//sqlQueryCropus=sqlQueryCropus.replaceAll("\'","\"");
                                for (int j = 1; j <= intPageCount; j++) {
                                	out.print("&nbsp;&nbsp;<a href='ADindex.jsp?"+"page=" + j
                                                + "' style=''>" + j + "</a>");
                                     }
                            %>
							</span> <%
     							} catch (Exception e) {
         							e.printStackTrace();
      								}
  							%>
							</td>
						</tr>
					</table>
				</div>


			</div>
		</div>
		<div id="footer">
			<p align="center">Copyright 2019 by He Sixia,Kunming
				University of Science and Technology</p>
			<span style="display: none"><script language="javascript"
					type="text/javascript" src="http://js.users.51.la/1967272.js"></script></span>
		</div>
	</div>

</body>
</html>