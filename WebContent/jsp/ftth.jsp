<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GPON MANAGEMENT</title>
</head>
<body>
	<sql:setDataSource var="snapshot"  driver="com.mysql.jdbc.Driver"
		url="jdbc:mysql://10.96.36.10:3306/gpon" user="gpon"
		password="gpon_vnpt" />
	<sql:query dataSource="${snapshot}" var="result"  >
	SELECT * FROM manager WHERE `Group`='${param["group"]}'
	</sql:query>
	<h1 class="h1">GROUP <c:out value="${param.group}" /></h1>
	<table id="tbl" border="1" align="center">
		<tr>
			<th  class="stt">STT</th>
			<th  class="name">Name</th>
			<th  class="address">Address</th>
			<th  class="manufacture">Manufacture</th>
			<th  class="type">Type</th>
			<th  class="vlanhsi">VlanHSI</th>
			<th  class="ctrlboard">Ctrl Board</th>
			<th  class="pon8">PON-8</th>
			<th  class="pon16">PON-16</th>
			<th  class="sfpused">SFPused</th>
			<th  class="used">ONUused</th>
			<th  class="uplink">Uplink</th>
			<th  class="upto">UpTo</th>
		</tr>
		<%
			int i = 1;
		%>
		<c:forEach items="${result.rows}" var="row">
			<tr>
				<td><c:out value="<%=i++%>" />
				<td><c:out value="${row.Name}" /></td>
				<td><c:out value="${row.Address}" /></td>
				<td><c:out value="${row.Manufacture}" /></td>
				<td><c:out value="${row.Type}" /></td>
				<td><c:out value="${row.VlanHSI}" /></td>
				<td><c:out value="${row.CtrlBoard}" /></td>
				<td><c:out value="${row.PON8}" /></td>
				<td><c:out value="${row.PON16}" /></td>
				<td><c:out value="${row.SfpUsed}" /></td>
				<td><c:out value="${row.Used}" /></td>
				<td><c:out value="${row.Uplink}" /></td>
				<td><c:out value="${row.UpTo}" /></td>

			</tr>
		</c:forEach>
	</table>
</body>
</html>
