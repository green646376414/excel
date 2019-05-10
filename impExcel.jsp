<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="head.jsp"%>

  
<div class="modal fade" id="addModal_1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">excel导入</h4>
            </div>
             <form  name = "frm" action="/school/score/impExcel" method="post" enctype="multipart/form-data">
		        <input id="jsLink" name="excelFile"  type="file" value="浏览">
		        <input type="button" style="width:80px;height:25px;" id="submitB" class="submitB" value="提交"/><br/>
		
		    </form>
        </div>
    </div>
</div>
    <!-- /. WRAPPER  -->
<!-- JS Scripts-->
<!-- jQuery Js -->
<script src="${pageContext.request.contextPath }/assets/js/jquery-1.10.2.js"></script>
<!-- Bootstrap Js -->
<script src="${pageContext.request.contextPath }/assets/js/bootstrap.min.js"></script>
<!-- Metis Menu Js -->
<script src="${pageContext.request.contextPath }/assets/js/jquery.metisMenu.js"></script>
<!-- Custom Js -->
<script src="${pageContext.request.contextPath }/assets/js/custom-scripts.js"></script>

 
 <script>
  
 
 $(function (){
	var ses='${ses}';
	console.log("-----------------"+ses);
	if(ses=='1'){
		alert('导入成功!');
	}
     $('#submitB').click(function(){
         if($("input[type='file']").val() == ""){
             alert("请选择导入的文件！");
         }else{
             frm.submit();
         }
     });


 });
</script>