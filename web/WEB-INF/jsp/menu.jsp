<%-- 
    Document   : menu
    Created on : 13-nov-2017, 10:13:52
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
        <script>
             function append_tabla_folders(seleccion) {
                    
                } 
            function createFolder(nombre){
                $.ajax({
                        type: "POST",
                        url: "createfolder.htm?nombre="+nombre,
                        data: nombre ,
                        dataType: 'text' ,           

                        success: function(data) {
                            var json = JSON.parse(data);
                            $('#table_folders_wrapper').remove();
                            $('#tabla_carpetas').prepend('<table id="table_folders" class="display" >'+
                                '<thead>'+
                                    '<tr>'+
                                        '<td>id</td>'+
                                        '<td>Folders:</td>'+
                                    '</tr>'+
                                '</thead>'+
                                '<tbody></tbody>'+
                                '</table>');
                            $.each(json, function(i) {
                                var columna = $('<tr>'+
                                        '<td>'+json[i].id+'</td>'+
                                        '<td>'+json[i].nombre+'</td>'+
                                        '</tr>');   
                                $('#table_folders tbody').append(columna);
                            });
                            table = $('#table_folders').DataTable(
                            {
                                "searching": true,
                                "paging": false,
                                "ordering": false,
                                "info": false,
                                columns: [
                                    {data: 'id',
                                        visible: false},
                                    {data: 'name'}
                                ]
                            });
                            $('#table_folders tbody').on('click', 'tr',function(){
                                var seleccion = table.row( this ).data().id;
                                $.ajax({
                                    type: "POST",
                                    url: "chargefolder.htm?seleccion="+seleccion,
                                    data: seleccion ,
                                    dataType: 'text' ,           

                                    success: function(data) {
                                        var json = JSON.parse(data);

                                        $("#table_id_wrapper").remove();
                                        $("#tabla_mensajes").prepend('<table id="table_id" class="display" >'+
                                            '<thead>'+
                                                '<tr>'+
                                                    '<td>id</td>'+
                                                    '<td>Asunto</td>'+
                                                    '<td>Sender</td>'+
                                                    '<td>Resume</td>'+
                                                    '<td>Date</td>'+
                                                    '<td>Options</td>'+
                                                '</tr>'+
                                            '</thead>'+
                                            '</table>');
                                        $("#table_id").append($('<tbody></tbody>'));
                                        $.each(json, function(i) {
                                            var columna = $('<tr>'+
                                                    '<td>'+json[i].parentid+'</td>'+
                                                    '<td>'+json[i].asunto+'</td>'+
                                                    '<td>'+json[i].sender+'</td>'+
                                                    '<td>'+json[i].texto+'</td>'+
                                                    '<td>'+json[i].fecha+'</td>'+
                                                    '<td> <div class="sinpadding text-center">'+
                                                                '<input name="TXTid_lessons_detalles" type="image" src="<c:url value="/recursos/img/btn/btn_details.svg"/>" width="30px" data-placement="bottom" title="Details">'+
                                                            '</div>'+
                                                            '<div class="col-xs-6 sinpadding text-center">'+
                                                                '<input class="delete" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/btn_delete.svg"/>" width="30px" data-placement="bottom" title="Delete">'+
                                                            '</div>'+
                                                    '</tr>');   
                                            $('#table_id tbody').append(columna);
                                        });
                                        $('#table_id').DataTable({
                                            "aLengthMenu": [[5, 10, 20, -1], [5, 10, 20, "All"]],
                                            "iDisplayLength": 5,

                                            "columnDefs": [
                                                    { "width": "10%",  "targets": [ 0 ],
                                                        "visible": false,
                                                        "searchable": false},
                                                    { "width": "25%",   "targets": [ 1 ]},
                                                    { "width": "5%",    "targets": [ 2 ] },
                                                    { "width": "40%",   "targets": [ 3 ] },
                                                    { "width": "10%",   "targets": [ 4 ] },
                                                    { "width": "10%",   "targets": [ 5 ] }
                                            ]
                                        });
                                    },
                                    error: function (xhr, ajaxOptions, thrownError) {
                                        console.log(xhr.status);
                                        console.log(xhr.responseText);
                                        console.log(thrownError);
                                    }
                                });
                            });
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            console.log(xhr.status);
                            console.log(xhr.responseText);
                            console.log(thrownError);
                        }
                    });
            }
            $(document).ready( function (){
                //$("#tg").treegrid();
                 table = $('#table_folders').DataTable(
                        {
                            "searching": true,
                            "paging": false,
                            "ordering": false,
                            "info": false,
                            columns: [
                                {data: 'id',
                                    visible: false},
                                {data: 'name'}
                            ]
                        });
                $('#table_id').DataTable({
                    "aLengthMenu": [[5, 10, 20, -1], [5, 10, 20, "All"]],
                    "iDisplayLength": 5,

                    "columnDefs": [
                            { "width": "10%",  "targets": [ 0 ],
                                "visible": false,
                                "searchable": false},
                            { "width": "25%",   "targets": [ 1 ]},
                            { "width": "5%",    "targets": [ 2 ] },
                            { "width": "40%",   "targets": [ 3 ] },
                            { "width": "10%",   "targets": [ 4 ] },
                            { "width": "10%",   "targets": [ 5 ] }
                    ]
                });
               
                $('#table_folders tbody').on('click', 'tr',function(){
                    var seleccion = table.row( this ).data().id;
                    $.ajax({
                        type: "POST",
                        url: "chargefolder.htm?seleccion="+seleccion,
                        data: seleccion ,
                        dataType: 'text' ,           

                        success: function(data) {
                            var json = JSON.parse(data);

                            $("#table_id_wrapper").remove();
                            $("#tabla_mensajes").prepend('<table id="table_id" class="display" >'+
                                '<thead>'+
                                    '<tr>'+
                                        '<td>id</td>'+
                                        '<td>Asunto</td>'+
                                        '<td>Sender</td>'+
                                        '<td>Resume</td>'+
                                        '<td>Date</td>'+
                                        '<td>Options</td>'+
                                    '</tr>'+
                                '</thead>'+
                                '</table>');
                            $("#table_id").append($('<tbody></tbody>'));
                            $.each(json, function(i) {
                                var columna = $('<tr>'+
                                        '<td>'+json[i].parentid+'</td>'+
                                        '<td>'+json[i].asunto+'</td>'+
                                        '<td>'+json[i].sender+'</td>'+
                                        '<td>'+json[i].texto+'</td>'+
                                        '<td>'+json[i].fecha+'</td>'+
                                        '<td> <div class="col-xs-6 sinpadding text-center" >'+
                                                    '<input name="TXTid_lessons_modificar" type="image" src="<c:url value="/recursos/img/btn/btn_Edit.svg"/>" width="30px" data-placement="bottom" title="Reply">'+
                                               '</div>'+
                                                '<div class="col-xs-6 sinpadding text-center">'+
                                                    '<input class="delete" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/btn_delete.svg"/>" width="30px" data-placement="bottom" title="Delete">'+
                                                '</div>'+
                                        '</tr>');   
                                $('#table_id tbody').append(columna);
                            });
                            $('#table_id').DataTable({
                                "aLengthMenu": [[5, 10, 20, -1], [5, 10, 20, "All"]],
                                "iDisplayLength": 5,

                                "columnDefs": [
                                        { "width": "10%",  "targets": [ 0 ],
                                            "visible": false,
                                            "searchable": false},
                                        { "width": "25%",   "targets": [ 1 ]},
                                        { "width": "5%",    "targets": [ 2 ] },
                                        { "width": "40%",   "targets": [ 3 ] },
                                        { "width": "10%",   "targets": [ 4 ] },
                                        { "width": "10%",   "targets": [ 5 ] }
                                ]
                            });
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            console.log(xhr.status);
                            console.log(xhr.responseText);
                            console.log(thrownError);
                        }
                    });
                });
            });
        </script>
    </head>
    <body>
        <div class="container">
            <h1 class="text-center">Menu</h1>
            <fieldset>
                <div class="row uk-grid dt-merge-grid" style="padding-right: 0px;">
                    <div class="col-xs-3">
                        <div id="tabla_carpetas" class="col-xs-12 studentarea">
                            <table id="table_folders" class="display" >
                                <thead>
                                    <tr>
                                        <td>ID</td>
                                        <td>Folders:</td>
                                    </tr>
                                </thead>
                                <c:forEach var="folder" items="${folders}" >
                                    <tr>
                                        <td>${folder.id}</td>
                                        <td>${folder.nombre}</td>
                                    </tr>
                                </c:forEach>
                            </table>      
                        </div>
                        <div class="text-center">
                            <input id="fname" style="margin-bottom: 10px;" type="text">
                            <input type="submit" value="Create Folder" class="btn btn-success" onclick="createFolder($('#fname').val())">
                        </div>
                    </div>
                    <div class="col-xs-9" id="tabla_mensajes">
                            <table id="table_id" class="display" >
                                <thead>
                                    <tr>
                                        <td>id</td>
                                        <td>Asunto</td>
                                        <td>Sender</td>
                                        <td>Resume</td>
                                        <td>Date</td>
                                        <td>Options</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="mensaje" items="${lista}" >
                                        <tr>
                                            <td>${mensaje.parentid}</td>
                                            <td>${mensaje.asunto}</td>
                                            <td>${mensaje.sender}</td>
                                            <td>${mensaje.texto}</td>
                                            <td>${mensaje.fecha}</td>
                                            <td>
                                                <div class="col-xs-6 sinpadding text-center">
                                                    <input name="TXTid_lessons_detalles" type="image" src="<c:url value="/recursos/img/btn/btn_details.svg"/>" value="${lecciones.id}" width="30px" data-placement="bottom" title="Details">
                                                </div>
                                                <div class="col-xs-6 sinpadding text-center">
                                                    <input  name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/btn_delete.svg"/>" value="${lecciones.id}" width="30px" data-placement="bottom" title="Delete">
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                    <form:form action="enviar.htm" method="POST">
                        <input type="submit" value="create message" class="btn btn-success">
                    </form:form>
                </div>
            </fieldset>
        </div>
    </body>
</html>
