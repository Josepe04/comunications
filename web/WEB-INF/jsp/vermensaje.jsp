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
        <script>
            function responder(){
                document.getElementById("replypanel").style.visibility = "visible";
                document.getElementById("bottonspanel").style.visibility = "hidden";
            }
        </script>
    </head>
    <body>
        
        <div class="container">
            <div class="menu">
                <form:form action="start.htm" method="GET">
                    <input type="submit" value="volver al menu" class="btn btn-success">
                </form:form>
            </div>
            <h1 class="text-center">See message</h1>
            <fieldset>
                <div style="padding-right: 0px; margin-top: 10px;">
                    <div class="col-xs-4 text-center">
                        <div>
                            <label class="control-label">Sender </label>
                        </div>
                        <div>
                            <textarea readonly="readonly" id="names" rows="1">${fromname}</textarea>
                        </div>
                    </div>
                    <div class="col-xs-4 text-center">
                        <div>
                            <label class="control-label">Asunto</label> 
                        </div>
                        <div>
                            <textarea readonly="readonly" name="asunto" id="asunto" rows="1" cols="30">${mensaje.asunto}</textarea>
                        </div>
                    </div>
                    <div class="col-xs-4 text-center">
                        <div>
                            <label class="control-label">Fecha</label> 
                        </div>
                        <div>
                            <textarea readonly="readonly" name="fecha" id="fecha" rows="1" >${mensaje.fecha}</textarea>
                        </div>
                    </div>
                </div>
                <div style="padding-right: 0px; margin-top: 100px;">
                    <div class="panel panel-default">
                        <div class="text-center panel-heading">
                            <h4>Message</h4>
                        </div>
                        <div class="panel-body">${mensaje.texto}</div>
                    </div>
                </div>
                <div id="bottonspanel" class="text-center">
                    <input type="submit" value="reply message" class="btn btn-success" onclick="responder()">
                </div>
                <div>            
                    <div id="replypanel" class="form-group" style="padding-right: 0px;visibility: hidden;">
                        <form:form action="responder.htm" method="POST">
                            <input name="destinatarios" id="destinatarios" type="hidden" value="['${mensaje.sender}']">
                            <input name="parentid" id="parentid" type="hidden" value="${mensaje.id}">
                            <div>
                                <label class="control-label">Asunto</label> 
                            </div>
                            <div>
                                <textarea name="asunto" id="asunto" rows="1" cols="40"></textarea>
                            </div>
                            <label class="control-label">Text</label>
                            <textarea name="NotificationMessage" id="NotificationMessage" required="required"></textarea>
                            <script> CKEDITOR.replace('NotificationMessage');</script>

                            <div class="col-xs-12 text-center">
                                <input class="btn btn-primary btn-lg" type="submit" name="Submit" value="send"onclick="rellenarText()">
                            </div>
                        </form:form>
                    </div>
                </div>
            </fieldset>
        </div>
    </body>
</html>
