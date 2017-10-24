<!DOCTYPE html>
<html>
<%
	String pushStatus = "";
	Object pushStatusObj = request.getAttribute("pushStatus");

	if (pushStatusObj != null) {
		pushStatus = pushStatusObj.toString();
	}
%>

<head>
    <script>
        
        
        function setHiddenTxt(str)
        {
            
        }
    </script>
<title> Google Cloud Messaging (GCM) Server </title>
</head>
<body>

    <h1 align="Center">Messaging Server </h1>

	<form action="GCMNotification" method="post">

            <div align="center">
			<textarea rows="7" name="message" cols="43" placeholder="Write the message"></textarea>
		</div>
            <div align="center">
                    </br>     
                   Specific Client Code : <input type="text" name ="clientcode"  value="" />               
		</div>
		<div align="center">
                    </br>     
                    <input type="submit" name ="send"  value="Send" />               
		</div>
                
             
	</form>
    
    <Table>
        <tr>
    </Table>
    <p>
		<h3 align="Center">
			<%=pushStatus%>
		</h3>
                
	</p>
</body>
</html>