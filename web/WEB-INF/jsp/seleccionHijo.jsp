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
            <h1 class="text-center">what is your role?</h1>
             <fieldset>
                 <div class="col-md-2 col-md-offset-5">
                    <form:form action="seleccionado.htm" method="POST">
                        <div class="col-xs-12 text-center">
                            <!--<input type="hidden" name="length" value="0">-->
                            <input class="btn btn-primary btn-lg" type="submit" name="submit" value="father">
                        </div>
                    </form:form>
                    <form:form action="seleccionado.htm" method="POST">
                        <div class="col-xs-12 text-center">
                            <!--<input type="hidden" name="length" value="1">-->
                            <input class="btn btn-primary btn-lg" type="submit" name="submit" value="Profesor">
                        </div>
                     </form:form>
                </div>
             </fieldset>
        </div>
    </body>
</html>
