/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import com.google.gson.Gson;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ActivityLog;
import model.Hijo;
import model.Mensaje;
import model.Profesor;
import model.SendMail;
import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Norhan
 */
@Controller
public class EnviarmensajePadre {
    
    @RequestMapping("/enviarmensajepadre/seleccionchild.htm")
    @ResponseBody
    public String seleccionado(@RequestParam("seleccion") String id,HttpServletRequest hsr, HttpServletResponse hsr1) throws SQLException{
        ArrayList<Profesor> profesores = new ArrayList<>(); 
        if(id.equals("staff")){
            String consulta = "select StaffID, FirstName, LastName, Email, Occupation from Staff where Administrator=1 and active = 1";
            ResultSet rs = Homepage.st2.executeQuery(consulta);
            while(rs.next()){
                //String firstName, String lastName, int id, String email,String asig
                profesores.add(new Profesor(rs.getString("FirstName"),rs.getString("LastName"),
                                Integer.parseInt(rs.getString("StaffID")),rs.getString("Email"),rs.getString("Occupation")));
            }
        }else{
            profesores = getProfesors(Integer.parseInt(id));
        }
        hsr1.setContentType("application/json");
        hsr1.setCharacterEncoding("ISO-8859-1"); 
        return new Gson().toJson(profesores);
    }
    
    @RequestMapping("/enviarmensajepadre/start.htm")
    public ModelAndView start(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        User us = (User) hsr.getSession().getAttribute("user");
        ModelAndView mv = Homepage.checklogin(hsr);
         if(mv!=null)
             return mv;
        mv = new ModelAndView("enviarmensajepadre");
        try {
            mv.addObject("hijos", getChildren(us));
        } catch (SQLException ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
        }
        return mv;
    }
    
    @RequestMapping("/enviarmensajepadre/enviar.htm")
    public ModelAndView enviar( HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        ModelAndView mv = Homepage.checklogin(hsr);
         if(mv!=null)
             return mv;
        mv = new ModelAndView("redirect:/menu/start.htm?folder=null");
        String asunto = hsr.getParameter("asunto");
        String text = hsr.getParameter("NotificationMessage");
        String parentmsgid = hsr.getParameter("parentid");
        String[] destinationListAux = hsr.getParameterValues("destino[]");
        ArrayList<String> destinationList = new ArrayList();
        String msgid = "";
        String consulta;
        
        /**
         * Saco el nombre del sender de renweb
         */
        String from = ""+((User)hsr.getSession().getAttribute("user")).getId();
        String fromName = "error not found";
        ResultSet name = Homepage.st2.executeQuery("select * from Person where PersonID="+from);
        if(name.next())
            fromName = name.getString("firstname")+" "+name.getString("LastName");
        fromName = EnviarMensaje.limpiarFromName(fromName);
         
        Mensaje m;
        User u = (User)hsr.getSession().getAttribute("user");
        ArrayList<String> folderList = new ArrayList<>();
        ArrayList<String> emails = new ArrayList<>();
        
        //  
         if(asunto.equals("") || asunto.length()>30 || text.equals("") || destinationList==null){
            mv = new ModelAndView("enviarmensaje");
            mv.addObject("error", "error");
            return mv;
        }
        Calendar t = Calendar.getInstance();
        String time = t.get(Calendar.YEAR)+ "-" +(t.get(Calendar.MONTH)+1)+
                    "-"+t.get(Calendar.DAY_OF_MONTH)+" "+t.get(Calendar.HOUR_OF_DAY)+":"+
                    t.get(Calendar.MINUTE)+":"+t.get(Calendar.SECOND);
        
        for(String dest:destinationListAux){
          ResultSet rs = Homepage.st.executeQuery("select * from folder where idpersona="+dest+" and nombre='Inbox'");
          if(rs.next())
              folderList.add(rs.getString("idfolder"));
          else
              folderList.add(EnviarMensaje.createFolder(Homepage.st,dest,"Inbox"));
          ResultSet rs2 = Homepage.st2.executeQuery("select email from person where personid="+dest);
          if(rs2.next()){
              destinationList.add(dest);
              emails.add(rs2.getString(1));
          }else{
              ActivityLog.nuevaEntrada(from,fromName,dest, "no email", "El custody no tiene corrreo");
          }
        }
        ResultSet rs3 = Homepage.st.executeQuery("select * from folder where idpersona="+u.getId()+" and nombre='Sent'");
        if(rs3.next())
            folderList.add(rs3.getString("idfolder"));
        else
            folderList.add(EnviarMensaje.createFolder(Homepage.st,""+u.getId(),"Sent"));
        
        consulta = "insert into mensaje (parentid,fecha,prio,asunto,texto) values ("
                +((User)hsr.getSession().getAttribute("user")).getId()+
                ", '"+time+"' ,1,'"+asunto+"','"+text+"')";
        Homepage.st.executeUpdate(consulta,Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = Homepage.st.getGeneratedKeys();
        if(rs.next())
            msgid = ""+rs.getInt(1);
        for(String f:folderList){
            Homepage.st.executeUpdate("insert into msg_folder values("+msgid+","+f+")");
        }
        for(int i=0;i<destinationList.size();i++){
            consulta = "insert into msg_from_to(msgid,msfrom,msto,fromname) values("+msgid+","+from+","
                    +destinationList.get(i)+",'"+fromName+"')"; 
            Homepage.st.execute(consulta);
        }
        if(parentmsgid!=null)
            m = new Mensaje(fromName,"Mensaje de "+fromName+": "+asunto,text,Integer.parseInt(parentmsgid),1);
        else
            m = new Mensaje(fromName,"Mensaje de "+fromName+": "+asunto,text,0,1);
        m.setDestinatarios(emails);
        SendMail.SendMail(m,from);
        return mv;
    }
    
    public ArrayList<Profesor> getProfesors(int id) throws SQLException
    {
        ArrayList<Profesor> listaProfesores = new ArrayList<>();
        try {
            ArrayList<Integer> staffids = new ArrayList<>(); 
            ArrayList<String> classids = new ArrayList<>(); 
            String consulta = "select StaffID, Classes.ClassID , Courses.Title from Roster inner join Classes" +
                                " on Roster.ClassID = Classes.ClassID" +
                                " inner join Courses on  Classes.CourseID = Courses.CourseID"+          
                                "  where Roster.StudentID = "+id;
            ResultSet rs = Homepage.st2.executeQuery(consulta);
            while(rs.next()){
                staffids.add(rs.getInt("StaffID"));
                classids.add(rs.getString("Title"));
            }
           
            for(Integer i : staffids){
                consulta = "select FirstName,LastName,Email from Person where PersonID = "+i;
                ResultSet rs2 = Homepage.st2.executeQuery(consulta);
                if(rs2.next())
                    listaProfesores.add(new Profesor(rs2.getString("FirstName"),rs2.getString("LastName"),i,rs2.getString("Email")));
            }
            for(int i = 0;i < classids.size();i++){
                listaProfesores.get(i).setAsignatura(classids.get(i));
            }
            
            int p = 6;
            //this.finalize();
            
        } catch (SQLException ex) {
            System.out.println("Error leyendo Alumnos: " + ex);
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            //log.error(ex+errors.toString());
        }
       
        return listaProfesores;
    }
    
    public ArrayList<Hijo> getChildren(User u) throws SQLException {
        int id = u.getId();
        ArrayList<Hijo> listaAlumnos = new ArrayList<>();
        try {
            String consulta = "SELECT Person.FirstName, Person.LastName, Person_Student.SchoolCode, Person_Student.StudentID, Parent_Student.Custody, Parent_Student.Correspondence,"
                    + " Parent_Student.PWBlock, Parent_Student.ParentID"
                    + " FROM  Parent_Student INNER JOIN"
                    + " Person_Student ON Parent_Student.StudentID = Person_Student.StudentID INNER JOIN"
                    + " Person ON Person_Student.StudentID = Person.PersonID"
                    + " WHERE ((Parent_Student.Correspondence = 1) or (Parent_Student.PWBlock <> 1) ) AND"
                    + " (Parent_Student.ParentID = "+u.getId()+")";
            ResultSet rs = Homepage.st2.executeQuery(consulta);
            while (rs.next()) {
                listaAlumnos.add(new Hijo(rs.getInt("StudentID"),rs.getString("FirstName")
                        ,rs.getString("LastName")));
            }
            //this.finalize();

        } catch (SQLException ex) {
            System.out.println("Error leyendo Alumnos: " + ex);
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            //log.error(ex+errors.toString());
        }

        return listaAlumnos;
    }
}
