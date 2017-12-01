/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import com.google.gson.Gson;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Hijo;
import model.Mensaje;
import model.Profesor;
import model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Norhan
 */
@Controller
public class EnviarmensajePadre {
    
     Connection cn;
    //static Logger log = Logger.getLogger(ProgressbyStudent.class.getName());
    private ServletContext servlet;
    
    private Object getBean(String nombrebean, ServletContext servlet)
    {
        ApplicationContext contexto = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet);
        Object beanobject = contexto.getBean(nombrebean);
        return beanobject;
    }
    
    
    
    @RequestMapping("/enviarmensajepadre/seleccionchild.htm")
    @ResponseBody
    public String seleccionado(@RequestParam("seleccion") String id,HttpServletRequest hsr, HttpServletResponse hsr1) throws SQLException{
        ArrayList<Profesor> profesores = new ArrayList<>(); 
        if(id.equals("staff")){
            Statement st = this.cn.createStatement();
            String consulta = "select StaffID, FirstName, LastName, Email, Occupation from Staff where Faculty=0";
            ResultSet rs = st.executeQuery(consulta);
            while(rs.next()){
                //String firstName, String lastName, int id, String email,String asig
                profesores.add(new Profesor(rs.getString("FirstName"),rs.getString("LastName"),
                                Integer.parseInt(rs.getString("StaffID")),rs.getString("Email"),rs.getString("Occupation")));
            }
        }else{
            profesores = getProfesors(Integer.parseInt(id));
        }
        return new Gson().toJson(profesores);
    }
    
    @RequestMapping("/enviarmensajepadre/start.htm")
    public ModelAndView start(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        User us = (User) hsr.getSession().getAttribute("user");
        ModelAndView mv;
        mv = new ModelAndView("enviarmensajepadre");
        try {
            DriverManagerDataSource dataSource;
            dataSource = (DriverManagerDataSource) this.getBean("dataSourceAH", hsr.getServletContext());
            this.cn = dataSource.getConnection();
            Statement st = this.cn.createStatement();
            mv.addObject("hijos", getChildren(us));
        } catch (SQLException ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
        }
        return mv;
    }
    
    
    @RequestMapping("/enviarmensajepadre/enviar.htm")
    public ModelAndView enviar( HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        ModelAndView mv = new ModelAndView("redirect:/menu/start.htm");
        String destinatarios = hsr.getParameter("destinatarios");
        String asunto = hsr.getParameter("asunto");
        String text = hsr.getParameter("NotificationMessage");
        String data = hsr.getParameter("student");
        String profesorid = hsr.getParameter("parentid");
        String msgid = "";
        String consulta;
        Mensaje m;
        User u = (User)hsr.getSession().getAttribute("user");
        ArrayList<String> destinationList = new ArrayList<>();
        ArrayList<String> folderList = new ArrayList<>();
        Calendar t = Calendar.getInstance();
        String time = t.get(Calendar.YEAR)+ "-" +t.get(Calendar.MONTH)+
                    "-"+t.get(Calendar.DAY_OF_MONTH)+" "+t.get(Calendar.HOUR)+":"+
                    t.get(Calendar.MINUTE)+":"+t.get(Calendar.SECOND);
        
        destinatarios = destinatarios+"]";
        destinationList = (new Gson()).fromJson(destinatarios, destinationList.getClass());
        DriverManagerDataSource dataSource;
        dataSource = (DriverManagerDataSource) this.getBean("comunicacion", hsr.getServletContext());
        this.cn = dataSource.getConnection();
        Statement st = this.cn.createStatement();
        for(String dest:destinationList){
          ResultSet rs = st.executeQuery("select * from folder where idpersona="+dest+" and nombre='Inbox'");
          if(rs.next())
              folderList.add(rs.getString("idfolder"));
          else
              folderList.add(EnviarMensaje.createFolder(st,dest,"Inbox"));
        }
        ResultSet rs3 = st.executeQuery("select * from folder where idpersona="+u.getId()+" and nombre='sent'");
        if(rs3.next())
            folderList.add(rs3.getString("idfolder"));
        else
            folderList.add(EnviarMensaje.createFolder(st,""+u.getId(),"sent"));
        
        consulta = "insert into mensaje (parentid,fecha,prio,asunto,texto) values ("
                +((User)hsr.getSession().getAttribute("user")).getId()+
                ", '"+time+"' ,1,'"+asunto+"','"+text+"')";
        st.executeUpdate(consulta,Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = st.getGeneratedKeys();
        if(rs.next())
            msgid = ""+rs.getInt(1);
        for(String f:folderList){
            st.executeUpdate("insert into msg_folder values("+msgid+","+f+")");
        }
        String from = ""+((User)hsr.getSession().getAttribute("user")).getId();
        for(String dest:destinationList){
            st.executeUpdate("insert into msg_from_to values("+msgid+","+from+","+dest+")");
        }
        
        m = new Mensaje(asunto,text,Integer.parseInt(profesorid),1,"chemamola");
        m.setDestinatarios(destinationList);
        //SendMail.SendMail(m);
        return mv;
    }
    
    public ArrayList<Profesor> getProfesors(int id) throws SQLException
    {
        ArrayList<Profesor> listaProfesores = new ArrayList<>();
        try {
            ArrayList<Integer> staffids = new ArrayList<>(); 
            ArrayList<String> classids = new ArrayList<>(); 
            Statement st = this.cn.createStatement();
            String consulta = "select StaffID, Classes.ClassID , Courses.Title from Roster inner join Classes\n" +
                                "on Roster.ClassID = Classes.ClassID\n" +
                                "inner join Courses on  Classes.CourseID = Courses.CourseID"+          
                                "  where Roster.StudentID = "+id;
            ResultSet rs = st.executeQuery(consulta);
            if(rs.next()){
                staffids.add(rs.getInt("StaffID"));
                classids.add(rs.getString("Title"));
            }
           
            for(Integer i : staffids){
                consulta = "select FirstName,LastName,Email from Person where PersonID = "+i;
                ResultSet rs2 = st.executeQuery(consulta);
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

            Statement st = this.cn.createStatement();
            String consulta = "SELECT Person.FirstName, Person.LastName, Person_Student.SchoolCode, Person_Student.StudentID, Parent_Student.Custody, Parent_Student.Correspondence,"
                    + " Parent_Student.PWBlock, Parent_Student.ParentID"
                    + " FROM  Parent_Student INNER JOIN"
                    + " Person_Student ON Parent_Student.StudentID = Person_Student.StudentID INNER JOIN"
                    + " Person ON Person_Student.StudentID = Person.PersonID"
                    + " WHERE ((Parent_Student.Correspondence = 1) or (Parent_Student.PWBlock <> 1) ) AND"
                    + " (Parent_Student.ParentID = "+u.getId()+")";
            ResultSet rs = st.executeQuery(consulta);
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
