<%-- 
    Document   : enviarmensaje
    Created on : 07-nov-2017, 10:41:05
    Author     : Norhan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html>
    <%@ include file="infouser.jsp" %>
    <%--<%@ include file="menu.jsp" %>--%>
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Students</title>
        <script>
            
            function rellenarText() {
                var message = CKEDITOR.instances.NotificationMessage.getData();
                $('#NotificationMessage').val(message);
            }

            $(document).ready(function () {
                $("#tg").treegrid();
                table = $('#table_students').DataTable(
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

                $('#table_students tbody').on('click', 'tr', function () {

                    data = table.row(this).data();
                    //if(!containsDest($('#destinatarios').valueOf(),data1))
                    var campo = $('#destinatarios').val();
                    var name = $('#names').val();
                    if (!name.includes(data.name))
                        if (name === "")
                            $('#names').val(data.name + ". ");
                        else
                            $('#names').val(name + data.name + ". ");
                    if (!campo.includes(data.id))
                        if (campo === "")
                            $('#destinatarios').val("['" + data.id + "'");
                        else
                            $('#destinatarios').val(campo + "," + "'" + data.id + "'");
                });

                $('#fecha').datetimepicker({
                    format: 'YYYY-MM-DD',
//            locale: userLang.valueOf(),
                    daysOfWeekDisabled: [0, 6],
                    useCurrent: false//Important! See issue #1075
                            //defaultDate: '08:32:33',


                });


            });


            var ajax;
            var d = new Date();

            var month = d.getMonth() + 1;
            var day = d.getDate();
            var hour = d.getHours();
            var minute = d.getMinutes();
            var second = d.getSeconds();

            var currentTime = d.getFullYear() + '-' +
                    (('' + month).length < 2 ? '0' : '') + month + '-' +
                    (('' + day).length < 2 ? '0' : '') + day + ' ' +
                    (('' + hour).length < 2 ? '0' : '') + hour + ':'
                    + (('' + minute).length < 2 ? '0' : '') + minute;

            function funcionCallBackSelectStudent()
            {
                if (ajax.readyState === 4) {

                    if (ajax.status === 200) {
                        //data
                        var json = JSON.parse(ajax.responseText);
                        var info = JSON.parse(json.info);
                                            var subjects = JSON.parse(json.sub);
                        var prog = JSON.parse(json.prog);
                        //first load the demographics
                        $('#gradelevel').text(info.level_id);
                        $('#nextlevel').text(info.nextlevel);
                        $('#student').text(info.nombre_students);
                        $('#studentid').val(info.id_students);
                        if (typeof info.foto === 'undefined') {
                            $('#foto').attr('src', '../recursos/img/NotPhoto.png');
                        } else {
                            $('#foto').attr('src', "ftp://AH-ZAF:e3f14+7mANDp@ftp2.renweb.com/Pictures/" + info.foto);
                        }
                        //load the objectives tracking tree
                        $('#tg').treegrid({
//                    view: myview,        
                            data: prog.children,
                            idField: 'id',
                            treeField: 'name',
                            columns: [[
                                    {title: 'Name', field: 'name'},
                                    {title: 'No.of presentations planned', field: 'noofplannedlessons'},
                                    {title: 'No.of presentations done', field: 'noofarchivedlessons'},
                                    {title: 'Progress', field: 'progress', formatter: formatProgress},
                                    {title: 'Final rating', field: 'rating'}
                                ]]

                        });
                        //hide the objectives in case a previous student was selected
                        $('#divTableObjective').addClass('hidden');//to avoid having the general comments of the previous selected student
                        $('#divNotObjective').addClass('hidden');
                        $('#subjects').empty();
                        $.each(subjects, function (i, item) {
                             $('#subjects').append('<option value= "' + subjects[i].id + '">' + subjects[i].name + '</option>');
                        });

                        $('#loadingmessage').hide();  // hide the loading message.
                    }
                }
            }
            ;

            
            function comboSelectFilter(){
                var filter = $('#filter').val();
                 $.ajax({
                    type: "POST",
                    url: "filter.htm?seleccion=" + filter,
                    data: filter,
                    dataType: 'text',
                    success: function (data) {
                        var json = JSON.parse(data);
                        $('#pepe').remove();
                        var anadir="";
                        if(filter===1){
                            anadir = "<select class='form-control' name='levelStudent' id='pepe' style='width: 100% !important;' onchange='comboSelectionLevelStudent()'>";
                        }else{
                            anadir = "<select class='form-control' name='levelStudent' id='pepe' style='width: 100% !important;' onchange='comboSelectionClassStudent()'>";
                        }
                        $.each(json, function (i) {
                            anadir+="<option value='"+json[i].id+"'>"+json[i].name+"</option>";
                        });
                        anadir+="</select>";
                        $('#filtro').append(anadir);
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        console.log(thrownError);
                    }

                });
            }

            function comboSelectionLevelStudent()
            {

                var nivel = $('#pepe').val();
                $.ajax({
                    type: "POST",
                    url: "studentlistLevel.htm?nivel=" + nivel,
                    data: nivel,
         dataType: 'text',
                    success: function (data) {
                        var json = JSON.parse(data);
                        //var table = $('#table_students').DataTable();
                        table.clear();

                        $.each(json, function (i) {
                            table.row.add({'id': json[i].id_students, 'name': json[i].nombre_students}).draw();

                        });

                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        console.log(thrownError);
                    }

                });

            }
            
            function comboSelectionClassStudent()
            {
                var nivel = $('#pepe').val();
                $.ajax({
                    type: "POST",
                    url: "studentclassLevel.htm?nivel=" + nivel,
                    data: nivel,
                    dataType: 'text',
                    success: function (data) {
                        var json = JSON.parse(data);
                        //var table = $('#table_students').DataTable();
                        table.clear();

                        $.each(json, function (i) {
                            table.row.add({'id': json[i].id_students, 'name': json[i].nombre_students}).draw();

                        });

                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        console.log(thrownError);
                    }

                });

            }

            function comboSelectionLevel()
            {
                if (window.XMLHttpRequest) //mozilla
                {
                    ajax = new XMLHttpRequest(); //No Internet explorer
                } else
                {
                    ajax = new ActiveXObject("Microsoft.XMLHTTP");
                }

                $('#createOnClick').attr('disabled', true);
                ajax.onreadystatechange = funcionCallBackSubject;
                var seleccion1 = document.getElementById("level").value;
                ajax.open("POST", "progressbystudent.htm?option=subjectlistLevel&seleccion1=" + seleccion1, true);

                ajax.send("");

            }

            function selectionStudent()
            {
                var selectStudent = data1;
                if (window.XMLHttpRequest) //mozilla
                {
                    ajax = new XMLHttpRequest(); //No Internet explorer
                } else
                {
                    ajax = new ActiveXObject("Microsoft.XMLHTTP");
                }
                $('#loadingmessage').show();  // show the loading message.
                //$('#createOnClick').attr('disabled', true);
                ajax.onreadystatechange = funcionCallBackSelectStudent;

                //  var selectStudent = document.getElementsByClassName("nameStudent").value;
                ajax.open("POST", "studentPage.htm?selectStudent=" + selectStudent, true);

                ajax.send("");

            }

            $(function () {
                $('#subject').change(function () {
//        $('#LoadTemplates').parent().attr("disabled",false);
//        $('#LoadTemplates').attr("disabled",false);
                    $('#LoadTemplates').children().removeClass("disabled");
                });

                $('#LoadTemplates').change(function () {
                    if ($("input:radio[name='options']:checked").val() === 'option1') {
                        $("#lessons").attr("disabled", true);
                        $('#divCrearLessons').removeClass('hidden');
                        $('#divLoadLessons').addClass('hidden');
//    $("#NameLessons").attr("disabled", true);
                    } else {
                        $("#lessons").attr("disabled", false);
                        $('#divLoadLessons').removeClass('hidden');
                        $('#divCrearLessons').addClass('hidden');
//    $("#NameLessons").attr("disabled", false);
                    }
                });
            });
        </script>

        <style>
            /*            textarea 
                        {
                        resize: none;
                        }*/
            .studentarea
            {            
                height: 500px;
                width: 100%;
                overflow-y: scroll;
            }
            .nameStudent
            {
                background-color: #D0D2D3;
                border-radius: 10px;
                margin-top: 20px;
                margin-bottom: 20px;
                padding-top: 10px;
                padding-bottom: 10px;
                min-height: 40px;
            }
            .tab-pane
            {
                padding-top: 20px;
            }
            .sinpadding
            {
                padding: 0px;
            }
            .containerPhoto
            {
                display: table;
                /*                background-color: lightgray;*/
                border-right: 1px #D0D2D3 double;
                min-height: 300px;
            }
            .cell{
                display: table-cell;
                vertical-align: middle;
            }
            #divTableObjective{
                margin-top: 20px;
            }
            .label-demographic{
                background-color: lightgray;
                text-align: center;
                padding: 5px;
                border-top-left-radius: 10px;
                border-top-right-radius: 10px;
                margin-bottom: 0px;
            }
            .demographic{
                border: 1px solid lightgray;
                padding: 5px;
                margin-bottom: 10px;
                min-height: 32px;
            }
            .btn-unbutton{
                background-color: Transparent;
                background-repeat:no-repeat;
                border: none;
                cursor:pointer;
                overflow: hidden;
                outline:none;
            }
            .dataTables_length select {

            }
            .dataTables_filter {
                display: block !important;
                float: left !important;
                text-align: left !important;
                padding-left: 16px;
            }
            .dataTables_filter input {
                display: block;
                float: left;
                width: 100%;
                height: 34px;
                padding: 6px 12px;
                margin-left: 0px !important;
                font-size: 14px;
                line-height: 1.42857143;
                color: #555;
                background-color: #fff;
                background-image: none;
                border: 1px solid #ccc;
                border-radius: 4px;
                -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
                box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
                -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
                -o-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
                transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
            }

        </style>
    </head>

    <body>
        <div class="container">
            <h1 class="text-center">Send Message</h1>

            <form:form action="enviar.htm" method="POST">

                <fieldset>
                    <!--                    <legend>Select student</legend>-->
                    <div class="col-xs-9 center-block form-group" style="padding-right: 0px;">
                        <div>
                            <input name="destinatarios" id="destinatarios" type="hidden" ></input>
                        </div>
                        <div>
                            <label>Recipients: </label>
                        </div>
                        <div>
                            <textarea readonly="readonly" id="names" style="width:60%"></textarea>
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
                            <input class="btn btn-primary btn-lg" type="submit" name="Submit" value="send"onclick="rellenarText()">
                        </div>
                    </div>

                    <div class="col-xs-3">
                        <c:set value="${user.type}" var="num"/>
                        <c:choose>
                            <c:when test="${num=='2'}">
                            </c:when>    
                            <c:when test="${num=='0'}">
                            </c:when>
                            <c:otherwise>
                                <label>Filter</label>
                                <select class="form-control" id="filter" style="margin-bottom: 5px;" onchange="comboSelectFilter()">
                                    <option value="1">Classes</option>
                                    <option value="0">Levels</option>
                                </select>
                            </c:otherwise>
                        </c:choose>
                        <div class="col-xs-12" id="filtro">
                            <select class="form-control" name="levelStudent" id="pepe" style="width: 100% !important;" onchange="comboSelectionLevelStudent()">
                                <c:forEach var="levels" items="${gradelevels}">
                                    <option value="${levels.id[0]}">${levels.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <!--                        <div class="col-xs-12">
                                                    <label>By name</label>
                                                    <input class="form-control" name="nameStudent" id="nameStudent" style="width: 100% !important;" onchange="comboSelectionnameStudent()">
                                                </div>-->
                        <div class="col-xs-12 studentarea">
                            <table id="table_students" class="display" >
                                <thead>
                                    <tr>
                                        <td>ID</td>
                                        <td>Name students</td>
                                    </tr>
                                </thead>
                                <c:forEach var="alumnos" items="${listaAlumnos}" >
                                    <tr>
                                        <td >${alumnos.id_students}</td>
                                        <td >${alumnos.nombre_students}</td>
                                    </tr>
                                </c:forEach>
                            </table>      
                        </div>
                    </div> 
                </fieldset>
            </form:form>
            <div>

            </div>
        </div>

        <div class="divLoadStudent" id="loadingmessage">
            <div class="text-center"> 
                <img src='../recursos/img/large_loading.gif'/>
            </div>
        </div>

        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <!--        <h4 class="modal-title" id="myModalLabel">Modal title</h4>-->
                    </div>
                    <div class="modal-body text-center">
                        <H1><%= request.getParameter("message")%></H1>
                    </div>
                    <!--      <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary">Save changes</button>
                          </div>-->
                </div>
            </div>
        </div>


        <div id="modalCommentGeneral">
            <!-- Button trigger modal -->
            <button type="button" class="btn btn-primary btn-lg hidden" data-toggle="modal" data-target="#modalComment" id="showModalComment">
                Launch demo modal
            </button>   
            <!-- Modal -->
            <div class="modal fade" id="modalComment" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="titleComment"></h4>
                        </div>
                    </div>
                </div>
            </div>
        </div>




    </body>
</html>

