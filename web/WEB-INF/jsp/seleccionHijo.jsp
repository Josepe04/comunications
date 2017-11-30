<%-- 
    Document   : seleccionHijo
    Created on : 15-nov-2017, 11:39:09
    Author     : Norhan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
    <head>
        <%@ include file="infouser.jsp" %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <div class="container">
            <h1 class="text-center">Select Child</h1>
             <fieldset>
                 <div class="col-md-2 col-md-offset-5">
                     <form:form action="seleccionado.htm" method="POST">
                        <c:set var="k" value="${0}"/>
                        <c:forEach var="hijo" items="${hijos}">
                            <div class="col-xs-12 checkbox">
                                <label><input type="checkbox" name="${k}" value="${hijo.id}"><strong>${hijo.firstname} ${hijo.lastname}</strong></label>
                            </div>
                            <c:set var="k" value="${k+1}"/>
                        </c:forEach>
                        <input type="hidden" name="length" value="${k}">
                        <div class="col-xs-12 text-center">
                            <input class="btn btn-primary btn-lg" type="submit" name="Submit" value="next">
                        </div>
                     </form:form>
                </div>
             </fieldset>
        </div>
    </body>
</html>
