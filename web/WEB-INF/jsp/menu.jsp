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
        <title>Menu </title>
        <script>
            function borrarmsg(p,id,folderid){
                console.log(id);
                console.log(folderid);
                var datos=id+" "+folderid; 
                if(p)
                    datos='p'+datos;
                $.ajax({
                    type: "POST",
                    url: "borrarmsg.htm?id="+datos,
                    data:  datos,
                    dataType: 'text' ,           
                    
                    success: function() {
                        $('#tr_'+id).remove();
                        location.href="start.htm?folder="+folderid;
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        console.log(thrownError);
                    }
                });
            }
            
            function showmodal(id,folderid){
                $('#change_modal').modal("show");
                $('#changefolder').empty();
                $('#changefolder').append('<button type="button" class="btn btn-success" onclick="changefoldermsg('
                        +id+','+folderid+')">Move to folder</button> '
                );
            }
            
            function changefoldermsg(id,folderid){
                var folderid2 = $('#pepe').val();
                $.ajax({
                    type: "POST",
                    url: "changemsgfolder.htm?idfolder="+folderid+"&idmsg="+id+"&idfolder2="+folderid2,
                    dataType: 'text' ,           
                    
                    success: function(data) {
                        if(data==='1'){
                            $('#change_modal').modal("hide");
                            $('#tr_'+id).remove();
                            location.href="start.htm?folder="+folderid;
                        }else
                            console.log("error");
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        console.log(thrownError);
                    }
                });
            }
            
            function verMsg(datos) {
                $.ajax({
                    type: "POST",
                    url: "vermsgajax.htm?id="+datos,
                    data:  datos,
                    dataType: 'text' ,           
                    
                    success: function(data) {
                        var object = JSON.parse(data);
                        var cosas = object.senderid;
                        $('#asuntov').empty();
                        $('#asuntov').append('<tr><td>'+object.asunto+'</td></tr>');
                        $('#senderv').empty();
                        $('#senderv').append('<tr><td>'+object.sender+'</td></tr>');
                        $('#replydata').empty();
                        $('#replydata').append('<input name="destinatarios" id="destinatarios" value="'+cosas+'" type="hidden">');
                        $('#replydata').append('<input name="parentid" id="parentid" type="hidden" value="'+datos+'">');
                        $('#fechav').empty();
                        $('#fechav').append('<tr><td>'+object.fecha+'</td></tr>');
                        $('#textov').empty();
                        $('#textov').append('<tr><td>'+object.texto+'</td></tr>');
                        $('#detailsLesson').modal('show');
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        console.log(thrownError);
                    }
                });
            };

            function recovermsg(id,folderid){
                console.log(id);
                console.log(folderid);
                var datos=id+" "+folderid; 
                $('#tr_'+id).remove();
                $.ajax({
                    type: "POST",
                    url: "recover.htm?id="+datos,
                    data:  datos,
                    dataType: 'text' ,           
                    
                    success: function(vacio) {
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        console.log(thrownError);
                    }
                });
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
                                        '<td></td>'+
                                    '</tr>'+
                                '</thead>'+
                                '<tbody></tbody>'+
                                '</table>');
                            $('#pepe').empty();
                            $.each(json, function(i) {
                                if(json[i].nombre !== 'Sent' && json[i].nombre!=='Trash' 
                                        && json[i].nombre!=='Inbox')
                                    $('#pepe').append('<option id="folder2_'+json[i].id
                                            +'" value="'+json[i].id
                                            +'" >'+json[i].nombre+'</option>');
                                var columna = '<tr id="folder_'+json[i].id+'">'+
                                        '<td>'+json[i].id+'</td>'+
                                        '<td>'+json[i].nombre+'</td>'+
                                        '<td>';
                                if(json[i].nombre!=='Sent'&&json[i].nombre!=='Trash'&&json[i].nombre!=='Inbox')
                                    columna+='<input onclick="borrarFolder('+json[i].id+')" type="image" src="<c:url value="/recursos/img/btn/borrar.svg"/>" width="30px" data-placement="bottom" title="Delete">';
                                columna+='</td> </tr>'; 
                                $(columna);   
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
                                    {data: 'name'},
                                    {data: 'button'}
                                ]
                            });
                            $('#table_folders tbody').on('click', 'tr',function(){
                                var seleccion = table.row( this ).data().id;
                                var nombre = table.row( this ).data().name;
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
                                        var anadir = true;
                                        if(nombre==='Trash')
                                            anadir = false;
                                        $.each(json, function(i) {
                                            var columna = '<tr id="tr_'+json[i].id +'">'+
                                                    '<td>'+json[i].id+'</td>'+
                                                    '<td>'+json[i].asunto+'</td>'+
                                                    '<td>'+json[i].sender+'</td>'+
                                                    '<td>'+json[i].texto+'</td>'+
                                                    '<td>'+json[i].fecha+'</td>'+
                                                    '<td> <div class="col-xs-6 sinpadding text-center" >';
                                            if(nombre!=='Sent' && nombre!=='Trash')
                                                columna+='<input onclick="verMsg('+json[i].id+');" id="ver_button" name="ver_button" type="image" src="<c:url value="/recursos/img/btn/btn_details.svg"/>" width="30px" data-placement="bottom" title="Details">';
                                            columna+='</div>';
                                            if(nombre!=='Sent'){
                                                columna+='<div class="col-xs-6 sinpadding text-center">'+
                                                                '<input id="borrar_button_'+json[i].id+'" onclick="borrarmsg('+anadir+','+json[i].id+','+json[i].folderid+')" class="delete" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/btn_delete.svg"/>" width="30px" data-placement="bottom" title="Delete">'+
                                                            '</div>';//+'</tr>'; 
                                            }
                                            columna+='<div>'+
                                                            '<div class="col-xs-6 sinpadding text-center">'+
                                                                '<input id="changefolder_button_'+json[i].id+'" onclick="showmodal('+json[i].id+','+json[i].folderid+')" class="delete" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/paste.svg"/>" width="30px" data-placement="bottom" title="Move">'+
                                                            '</div>' + '</tr>';
                                            $('#table_id tbody').append($(columna));
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
                                {data: 'name'},
                                {data: 'button'}
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
                    var nombre = table.row( this ).data().name;
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
                            var anadir = true;
                            if(nombre==='Trash')
                                anadir = false;
                            $.each(json, function(i) {
                                var columna = '<tr id="tr_'+json[i].id +'">'+
                                        '<td>'+json[i].id+'</td>'+
                                        '<td>'+json[i].asunto+'</td>'+
                                        '<td>'+json[i].sender+'</td>'+
                                        '<td>'+json[i].texto+'</td>'+
                                        '<td>'+json[i].fecha+'</td>'+
                                        '<td> <div class="col-xs-6 sinpadding text-center" >';
                                if(nombre!=='Sent' && nombre!=='Trash')
                                    columna+='<input onclick="verMsg('+json[i].id+');" id="ver_button" name="ver_button" type="image" src="<c:url value="/recursos/img/btn/btn_details.svg"/>" width="30px" data-placement="bottom" title="Details">';
                                columna+='</div>';
                                if(nombre!=='Sent'){
                                    columna+='<div class="col-xs-6 sinpadding text-center">'+
                                                    '<input id="borrar_button_'+json[i].id+'" onclick="borrarmsg('+anadir+','+json[i].id+','+json[i].folderid+')" class="delete" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/btn_delete.svg"/>" width="30px" data-placement="bottom" title="Delete">'+
                                                '</div>';//+'</tr>'; 
                                
                                    columna+='<div>'+
                                                    '<div class="col-xs-6 sinpadding text-center">'+
                                                            '<input id="changefolder_button_'+json[i].id+'" onclick="showmodal('+json[i].id+','+json[i].folderid+')" class="delete" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/paste.svg"/>" width="30px" data-placement="bottom" title="Move">'+
                                                    '</div>' + '</tr>';
                                }
                                $('#table_id tbody').append($(columna));
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
           
            function borrarFolder(id){
                $('#borrarfolder').empty();
                $('#borrarfolder').append('<button onclick="borrarFolderAjax('+id+')" type="button" class="btn btn-danger">Delete folder</button>');
                $('#borrar_modal').modal('show');
            }
            
            function borrarFolderAjax(id){
                $.ajax({
                    type: "POST",
                    url: "deletefolder.htm?id="+id,
                    data:  id,
                    dataType: 'text' ,           
                    success: function() {
                        $('#folder_'+id).remove();
                        $('#folder2_'+id).remove();
                        $('#borrar_modal').modal('hide');
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        console.log(thrownError);
                    }
                });
            }
            
        </script>
        <style>
            #tabla_carpetas{
                border: 1px solid #ccc!important;
            }
        </style>
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
                                        <td>Folders :</td>
                                        <td></td>
                                    </tr>
                                </thead>
                                <c:forEach var="folder" items="${folders}" >
                                    <tr  id="folder_${folder.id}">
                                        <td>${folder.id}</td>
                                        <td>${folder.nombre}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${folder.nombre=='Inbox'}">
                                                </c:when>    
                                                <c:when test="${folder.nombre=='Sent'}">
                                                </c:when>
                                                <c:when test="${folder.nombre=='Trash'}">
                                                </c:when>    
                                                <c:otherwise>
                                                    <input onclick="borrarFolder(${folder.id})" type="image" src="<c:url value="/recursos/img/btn/borrar.svg"/>" width="30px" data-placement="bottom" title="Delete">
                                                </c:otherwise>
                                            </c:choose>
                                            
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>     
                            <div class="text-center">
                                <input id="fname" style="margin-bottom: 10px;" type="text">
                                <input type="submit" value="Create Folder" class="btn btn-success" onclick="createFolder($('#fname').val())">
                            </div>
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
                                        <tr id="tr_${mensaje.id}">
                                            <td>${mensaje.id}</td>
                                            <td>${mensaje.asunto}</td>
                                            <td>${mensaje.sender}</td>
                                            <td>${mensaje.texto}</td>
                                            <td>${mensaje.fecha}</td>
                                            <td>
                                                <c:choose> 
                                                    <c:when test='${nombrefolder=="Sent"}'>
                                                    </c:when>
                                                    <c:when test='${nombrefolder=="Trash"}'>
                                                        <div class="col-xs-6 sinpadding text-center">
                                                            <input id="borrar_button_${mensaje.id}" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/btn_delete.svg"/>" value="${mensaje.id}" onclick="borrarmsg(true,${mensaje.id},${mensaje.folderid})" width="30px" data-placement="bottom" title="Delete">
                                                        </div>
                                                    </c:when>    
                                                    <c:otherwise>
                                                        <div class="col-xs-6 sinpadding text-center">
                                                            <input id="ver_button" onclick="verMsg(${mensaje.id})" name="ver_button" type="image" src="<c:url value="/recursos/img/btn/btn_details.svg"/>" value="${mensaje.id}" width="30px" data-placement="bottom" title="Details">
                                                        </div>
                                                        <div class="col-xs-6 sinpadding text-center">
                                                            <input id="borrar_button_${mensaje.id}" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/btn_delete.svg"/>" value="${mensaje.id}" onclick="borrarmsg(true,${mensaje.id},${mensaje.folderid})" width="30px" data-placement="bottom" title="Delete">
                                                        </div>
                                                        <div class="col-xs-6 sinpadding text-center">
                                                            <input id="changefolder_button_${mensaje.id}" onclick="showmodal(${mensaje.id},${idfolder})" class="delete" name="TXTid_lessons_eliminar" type="image" src="<c:url value="/recursos/img/btn/paste.svg"/>" width="30px" data-placement="bottom" title="Move">
                                                        </div> 
                                                    </c:otherwise>
                                                </c:choose>
                                                
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                    <form:form action="enviar.htm" method="POST">
                        <input type="submit" value="create message" class="btn btn-success">
                    </form:form>
                </div>
                </div>
            </fieldset>
        </div>
        <!-- Modal delete-->
        <div id="detailsLesson" class="modal fade" role="dialog">
          <div class="modal-dialog modal-lg">

            <!-- Modal content-->
            <div class="modal-content">
              <div class="modal-header modal-header-details">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">show message</h4>
              </div>
                <div class="modal-body">
                    <div class="container-fluid">
                        <div class="col-xs-4">
                            <div class="row title">
                                <h4>Subject</h4>
                            </div>
                             <div class="row" id="asuntov">

                            </div>
                            <div class="row title">     
                                <h4>Sender</h4>
                            </div>
                            <div class="row" id ="senderv">

                            </div>
                            <div class="row title">
                                <h4>Date</h4>
                            </div>
                            <div class="row" id="fechav">

                            </div>
                        </div>
                        <div class="col-xs-8">
                            <div class="row title">
                                <h4>texto</h4> 
                            </div>
                            <div class="row">
                                <ul id="textov">
                                    
                                </ul>
                            </div>
                        </div>
                    </div>
                     
              <div class="modal-footer">
                <div id="replypanel">
                            <form:form action="responder.htm" method="POST">
                                <div id="replydata">
                                </div>  
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
                                    <input class="btn btn-primary btn-lg" type="submit" name="Submit" value="reply"onclick="rellenarText()">
                                </div>
                            </form:form>
                        </div>
                </div>
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
              </div>
            </div>

          </div>
        </div>
        <div id="borrar_modal" class="modal fade" role="dialog">
          <div class="modal-dialog modal-lg">

            <!-- Modal content-->
            <div class="modal-content">
              <div class="modal-header modal-header-details">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Delete folder</h4>
              </div>
                <div class="modal-body">
                    <div class="container-fluid">
                        <h2>Are you sure? </h2>
                    </div>
                     
                    <div class="modal-footer">
                        <div id="borrarfolder">
                            <button type="button" class="btn btn-danger">Delete folder</button>
                        </div>
                    </div>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>

          </div>
        </div>
        <div id="change_modal" class="modal fade" role="dialog">
          <div class="modal-dialog modal-lg">

            <!-- Modal content-->
            <div class="modal-content">
              <div class="modal-header modal-header-details">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Delete folder</h4>
              </div>
                <div class="modal-body">
                    <div class="container-fluid">
                        <h2>Select folder to move the message</h2>
                        <select class="form-control" id="pepe" style="width: 100% !important;">
                            <c:forEach var="folder" items="${folders}" >
                                <c:choose>
                                    <c:when test="${folder.nombre=='Inbox'}">
                                    </c:when>    
                                    <c:when test="${folder.nombre=='Sent'}">
                                    </c:when>
                                    <c:when test="${folder.nombre=='Trash'}">
                                    </c:when>    
                                    <c:otherwise>
                                         <option id="folder2_${folder.id}" value="${folder.id}" > ${folder.nombre} </option>          
                                    </c:otherwise>
                                </c:choose>  
                            </c:forEach>
                        </select>
                    </div>
                     
                    <div class="modal-footer">
                        <div id="changefolder">
                            <button type="button" class="btn btn-success">Move to folder</button>
                        </div>
                    </div>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>

          </div>
        </div>
    </body>
</html>
