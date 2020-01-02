<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/common/head.jsp"%>
<!-- 引入Spring表单标签库 -->
<%@ taglib prefix="fm" uri="http://www.springframework.org/tags/form" %>

<div class="right">
        <div class="location">
            <strong>你现在所在的位置是:</strong>
            <span>用户管理页面 >> 用户添加页面</span>
        </div>
        <div class="providerAdd">
            <fm:form id="userForm" modelAttribute="user" name="userForm" method="post" action="${pageContext.request.contextPath }/addUser">

                <!-- JSR 303数据验证--出生日期 -->
                <%--<fm:errors path="birthday"></fm:errors>--%>

                <!--div的class 为error是验证错误，ok是验证成功-->
                <div>
                    <label for="userCode">用户编码：</label>
                    <fm:input type="text" path="userCode" id="userCode" value=""/>
					<!-- 放置提示信息 -->
					<font color="red"></font>
                </div>
                <div>
                    <label for="userName">用户名称：</label>
                    <fm:input type="text" path="userName" id="userName" value=""/>
					<font color="red"></font>
                </div>
                <div>
                    <label for="userPassword">用户密码：</label>
                    <fm:input type="password" path="userPassword" id="userPassword" value=""/>
					<font color="red"></font>
                </div>
                <div>
                    <label for="ruserPassword">确认密码：</label>
                    <input type="password" name="ruserPassword" id="ruserPassword" value=""/>
					<font color="red"></font>
                </div>
                <div>
                    <label >用户性别：</label>
					<fm:select path="gender" id="gender">
					    <option value="1" selected="selected">男</option>
					    <option value="2">女</option>
					 </fm:select>
                </div>
                <div>
                    <label for="birthday">出生日期：</label>
                    <fm:input type="text" Class="Wdate" id="birthday" path="birthday"
                              readonly="readonly" onclick="WdatePicker();"/>
					<font color="red"></font>
                </div>
                <div>
                    <label for="phone">用户电话：</label>
                    <fm:input type="text" path="phone" id="phone" value=""/>
					<font color="red"></font>
                </div>
                <div>
                    <label for="address">用户地址：</label>
                    <fm:input path="address" id="address"  value=""/>
                </div>
                <div>
                    <label >用户角色：</label>
                    <!-- 列出所有的角色分类 -->
					<fm:select path="userRole" id="userRole"></fm:select>
	        		<font color="red"></font>
                </div>
                <%--<div>
                    <label for="newIdPicPath">上传照片：</label>
                    <input type="file" id="newIdPicPath" name="newIdPicPath"/>
                    <font color="red"></font>
                </div>--%>
                <div class="providerAddBtn">
                    <input type="button" name="add" id="add" value="保存" >
					<input type="button" id="back" name="back" value="返回" >
                </div>
            </fm:form>
        </div>
</div>
</section>
<%@include file="/WEB-INF/jsp/common/foot.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/useradd.js"></script>
