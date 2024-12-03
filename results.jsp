<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%
    List<String> results = (List<String>) request.getAttribute("results");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
</head>
<body>
    <h1>Search Results</h1>
    <ul>
    <% if (results != null && !results.isEmpty()) {
        for (String result : results) { %>
            <li><%= result %></li>
    <%  } } else { %>
        <p>No results found.</p>
    <% } %>
    </ul>
    <a href="index.jsp">Back to Search</a>
</body>
</html>
