<%@page import="DBRelated.DBPool"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome to Leave Application</title>
    </head>
    <body>
        <h1 style="color: greenyellow">Leave Application Log2 Details</h1>
            <%
            DBPool.printLog(out);
            %>
    </body>
</html>
