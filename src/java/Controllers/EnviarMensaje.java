/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import model.Level;
import model.Students;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mensaje;
import com.google.gson.Gson;
import java.util.Calendar;
import model.SendMail;
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
public class EnviarMensaje {
    Connection cn;
    //static Logger log = Logger.getLogger(ProgressbyStudent.class.getName());
    private ServletContext servlet;
    
    private Object getBean(String nombrebean, ServletContext servlet)
    {
        ApplicationContext contexto = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet);
        Object beanobject = contexto.getBean(nombrebean);
        return beanobject;
    }
    @RequestMapping("/enviarmensaje/start.htm")
    public ModelAndView start(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        User us = (User)hsr.getSession().getAttribute("user");
   
        ModelAndView mv;
        mv = new ModelAndView("enviarmensaje");
        List <Level> grades = new ArrayList();
        try{
            DriverManagerDataSource dataSource;
            dataSource = (DriverManagerDataSource)this.getBean("dataSourceAH",hsr.getServletContext());
            this.cn = dataSource.getConnection();
            mv.addObject("listaAlumnos", this.getStudents());
            Statement st = this.cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT GradeLevel,GradeLevelID FROM GradeLevels");
            Level l = new Level();
            l.setName("Select level");
            grades.add(l);
            while(rs.next())
            {
                Level x = new Level();
                 String[] ids = new String[1];
                 ids[0]=""+rs.getInt("GradeLevelID");
                x.setId(ids);
                x.setName(rs.getString("GradeLevel"));
            grades.add(x);
            }
        }catch(SQLException ex){
               StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                //log.error(ex+errors.toString());
        }
        mv.addObject("gradelevels", grades);
        return mv;
    }
    
    @RequestMapping("/enviarmensaje/filter.htm")
    @ResponseBody
    public String filterSelect (@RequestParam("seleccion") String id,HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        List <Level> grades = new ArrayList();
        User u = (User)hsr.getSession().getAttribute("user");
        DriverManagerDataSource dataSource;
        dataSource = (DriverManagerDataSource)this.getBean("dataSourceAH",hsr.getServletContext());
        this.cn = dataSource.getConnection();
        Statement st = this.cn.createStatement();
        if(id.equals("0"))
           try{
            ResultSet rs = st.executeQuery("SELECT GradeLevel,GradeLevelID FROM GradeLevels");
            Level l = new Level();
            l.setName("Select level");
            grades.add(l);
            while(rs.next())
            {
                Level x = new Level();
                 String[] ids = new String[1];
                 ids[0]=""+rs.getInt("GradeLevelID");
                x.setId(ids);
                x.setName(rs.getString("GradeLevel"));
            grades.add(x);
            }
           }catch(SQLException ex){
               StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                //log.error(ex+errors.toString());
           }
        else
           try{
            ResultSet rs;
            if(u.getId()==2)   
                rs = st.executeQuery("SELECT * FROM Classes where (StaffID="+u.getId()
                        +" or AltStaffID="+u.getId()+" or AidID="+u.getId()
                        + ")");
            else
                rs = st.executeQuery("SELECT * FROM Classes");
            Level l = new Level();
            l.setName("Select class");
            grades.add(l);
            while(rs.next())
            {
                Level x = new Level();
                 String[] ids = new String[1];
                 ids[0]=""+rs.getInt("ClassID");
                x.setId(ids);
                x.setName(rs.getString("Name"));
            grades.add(x);
            }
           }catch(SQLException ex){
               StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                //log.error(ex+errors.toString());
           } 
        return new Gson().toJson(grades);
    }

    @RequestMapping("/enviarmensaje/studentclassLevel.htm")
    @ResponseBody
    public String studentclassLevel(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {

        DriverManagerDataSource dataSource;
        dataSource = (DriverManagerDataSource)this.getBean("dataSourceAH",hsr.getServletContext());
        this.cn = dataSource.getConnection();
        List <Students> studentsgrades = new ArrayList();
        String[] levelid = hsr.getParameterValues("nivel");
        String test = hsr.getParameter("levelStudent");
        studentsgrades =this.getStudentsclass(levelid[0]);
        String data=new Gson().toJson(studentsgrades);
        
        return data;
    }
    
    public ArrayList<Students> getStudentsclass(String gradeid) throws SQLException
    {         
        ArrayList<Students> listaAlumnos = new ArrayList<>();
        try {
            
             Statement st = this.cn.createStatement();
             ResultSet rs= st.executeQuery("select * from Roster inner join Students on "
                     + "Roster.StudentID = Students.StudentID where Roster.ClassID ="+gradeid);
            while (rs.next())
            {
                Students alumnos = new Students();
                alumnos.setId_students(rs.getInt("StudentID"));
                alumnos.setNombre_students(rs.getString("LastName")+", "+ rs.getString("FirstName")+" "+ rs.getString("MiddleName"));
                listaAlumnos.add(alumnos);
            }
            //this.finalize();
            
        } catch (SQLException ex) {
            System.out.println("Error leyendo Alumnos: " + ex);
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
           // log.error(ex+errors.toString());
        }
       
        return listaAlumnos;
         
         
    }
    
    @RequestMapping("/enviarmensaje/studentlistLevel.htm")
    @ResponseBody
    public String studentlistLevel(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {

        DriverManagerDataSource dataSource;
        dataSource = (DriverManagerDataSource)this.getBean("dataSourceAH",hsr.getServletContext());
        this.cn = dataSource.getConnection();
        List <Students> studentsgrades = new ArrayList();
        String[] levelid = hsr.getParameterValues("nivel");
        String test = hsr.getParameter("levelStudent");
        studentsgrades =this.getStudentslevel(levelid[0]);
        String data=new Gson().toJson(studentsgrades);
        
        return data;
    }
    
    public ArrayList<Students> getStudentslevel(String gradeid) throws SQLException
    {         
        ArrayList<Students> listaAlumnos = new ArrayList<>();
        String gradelevel = null;
        try {
            
             Statement st = this.cn.createStatement();
            ResultSet rs1= st.executeQuery("select GradeLevel from GradeLevels where GradeLevelID ="+gradeid);
             while(rs1.next())
             {
             gradelevel = rs1.getString("GradeLevel");
             }
           
            String consulta = "SELECT * FROM Students where Status = 'Enrolled' and GradeLevel = '"+gradelevel+"'";
            ResultSet rs = st.executeQuery(consulta);
          
            while (rs.next())
            {
                Students alumnos = new Students();
                alumnos.setId_students(rs.getInt("StudentID"));
                alumnos.setNombre_students(rs.getString("LastName")+", "+ rs.getString("FirstName")+" "+ rs.getString("MiddleName"));
                alumnos.setFecha_nacimiento(rs.getString("Birthdate"));
                alumnos.setFoto(rs.getString("PathToPicture"));
                alumnos.setLevel_id(rs.getString("GradeLevel"));
                alumnos.setNextlevel(rs.getString("Placement"));
                alumnos.setSubstatus(rs.getString("Substatus"));
                listaAlumnos.add(alumnos);
            }
            //this.finalize();
            
        } catch (SQLException ex) {
            System.out.println("Error leyendo Alumnos: " + ex);
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
           // log.error(ex+errors.toString());
        }
       
        return listaAlumnos;
         
         
    }
    
    public static String createFolder(Statement st,String id,String nombre) throws SQLException{
        st.executeUpdate("insert into folder(idpersona,nombre) values ("+id+",'"+nombre+"')",
                          Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = st.getGeneratedKeys();
        if(rs.next())
            return rs.getString(1);
        else 
            return null;
    }
    
    @RequestMapping("/enviarmensaje/enviar.htm")
    public ModelAndView enviar( HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        
        ModelAndView mv = new ModelAndView("redirect:/menu/start.htm");
        String destinatarios = hsr.getParameter("destinatarios");
        String asunto = hsr.getParameter("asunto");
        String text = hsr.getParameter("NotificationMessage");
        String parentid = hsr.getParameter("parentid");
        String msgid = "";
        String consulta;
        if(asunto.equals("") || asunto.length()>30 || text.equals("") || destinatarios.equals("")){
            mv = new ModelAndView("redirect:/enviarmensaje/start.htm");
            mv.addObject("error", "error");
            return mv;
        }
            
        Mensaje m;
        User u = (User)hsr.getSession().getAttribute("user");
        ArrayList<String> destinationListAux = new ArrayList<>();
        ArrayList<String> destinationList = new ArrayList<>();
        ArrayList<String> destinationEmails = new ArrayList<>();
        ArrayList<String> folderList = new ArrayList<>();
        Calendar t = Calendar.getInstance();
        String time = t.get(Calendar.YEAR)+ "-" +t.get(Calendar.MONTH)+
                    "-"+t.get(Calendar.DAY_OF_MONTH)+" "+t.get(Calendar.HOUR)+":"+
                    t.get(Calendar.MINUTE)+":"+t.get(Calendar.SECOND);
        
        destinatarios = destinatarios+"]";
        destinationListAux = (new Gson()).fromJson(destinatarios, destinationListAux.getClass());
        DriverManagerDataSource dataSource;
        dataSource = (DriverManagerDataSource)this.getBean("dataSourceAH",hsr.getServletContext());
        this.cn = dataSource.getConnection();
        Statement st = this.cn.createStatement();
        for(String dest:destinationListAux){
            consulta = "select ps.parentid, ps.relationship, p.firstname, p.lastname, ISNULL(p.Email, 0) as mail"
                    + " from parent_student ps"
                    + " inner join person p"
                    + " on p.personid = ps.parentid" 
                    + " where ps.studentid ="+dest
                    + " and ps.Custody = 1";
            ResultSet rs = st.executeQuery(consulta);
            while(rs.next()){
                destinationList.add(rs.getString(1));
                destinationEmails.add(rs.getString("mail"));
            }
        }
        dataSource = (DriverManagerDataSource) this.getBean("comunicacion", hsr.getServletContext());
        this.cn = dataSource.getConnection();
        st = this.cn.createStatement(); 
        for(String dest:destinationList){
          ResultSet rs = st.executeQuery("select * from folder where idpersona="+dest+" and nombre='inbox'");
          if(rs.next())
              folderList.add(rs.getString("idfolder"));
          else
              folderList.add(createFolder(st,dest,"inbox"));
        }
        ResultSet rs3 = st.executeQuery("select * from folder where idpersona="+u.getId()+" and nombre='sent'");
        if(rs3.next())
            folderList.add(rs3.getString("idfolder"));
        else
            folderList.add(createFolder(st,""+u.getId(),"sent"));
        
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
        if(parentid!=null)
            m = new Mensaje(asunto,text,Integer.parseInt(parentid),1,"chemamola");
        else
            m = new Mensaje(asunto,text,0,1,"chemamola");
        m.setDestinatarios(destinationEmails);
       SendMail.SendMail(m);
        return mv;
    }
    
    public ArrayList<Students> getStudents() throws SQLException
    {
//        this.conectarOracle();
        ArrayList<Students> listaAlumnos = new ArrayList<>();
        try {
            
             Statement st = this.cn.createStatement();
             
            String consulta = "SELECT * FROM Students where Status = 'Enrolled' order by lastname";
            ResultSet rs = st.executeQuery(consulta);
          
            while (rs.next())
            {
                Students alumnos = new Students();
                alumnos.setId_students(rs.getInt("StudentID"));
                alumnos.setNombre_students(rs.getString("LastName")+", "+ rs.getString("FirstName")+" "+ rs.getString("MiddleName"));
                alumnos.setFecha_nacimiento(rs.getString("Birthdate"));
                alumnos.setFoto(rs.getString("PathToPicture"));
                alumnos.setLevel_id(rs.getString("GradeLevel"));
                alumnos.setNextlevel("Placement");
                alumnos.setSubstatus("Substatus");
                listaAlumnos.add(alumnos);
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
