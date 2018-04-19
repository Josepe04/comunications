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
import model.ActivityLog;
import model.SendMail;
import model.User;
import org.springframework.context.ApplicationContext;
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
    //static Logger log = Logger.getLogger(ProgressbyStudent.class.getName());
    private ServletContext servlet;
    
    private static Object getBean(String nombrebean, ServletContext servlet)
    {
        ApplicationContext contexto = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet);
        Object beanobject = contexto.getBean(nombrebean);
        return beanobject;
    }
    @RequestMapping("/enviarmensaje/start.htm")
    public ModelAndView start(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        ModelAndView mv = Homepage.checklogin(hsr);
         if(mv!=null)
             return mv;
        User u = (User)hsr.getSession().getAttribute("user");
        boolean p = Boolean.parseBoolean(hsr.getParameter("reply"));
        String parameter2 = hsr.getParameter("parentid");
        int p2=-1;
        if(parameter2!=null)
            p2 = Integer.parseInt(parameter2);
        mv = new ModelAndView("enviarmensaje");
        List <Level> grades = new ArrayList();
        Level l = new Level();
        l.setName("Select class");
        grades.add(l);
        try{
            int termId = 1, yearId = 1;
            ResultSet rs2 = Homepage.st2.executeQuery("select defaultyearid,defaulttermid from ConfigSchool where configschoolid = 1");
            while (rs2.next()) {
                termId = rs2.getInt("defaulttermid");
                yearId = rs2.getInt("defaultyearid");
            }
            ResultSet rs;
            String consulta = "";
            if(u.getType()==2 || u.getType()==0)   
                consulta = "SELECT * FROM Classes where (StaffID="+u.getId()
                        +" or AltStaffID="+u.getId()+" or AidID="+u.getId()
                        + ") and "+"yearid="+yearId;
            else{
                consulta = "SELECT * FROM Classes where "+"yearid="+yearId;       
                mv.addObject("listaAlumnos", this.getStudents());
            }
            rs = Homepage.st2.executeQuery(consulta);
            while(rs.next())
            {
                Level x = new Level();
                String[] ids = new String[1];
                ids[0]=""+rs.getInt("ClassID");
                x.setId(ids);
                x.setName(rs.getString("Name")+ " "+ rs.getString("Section"));
                grades.add(x);
            }
        }catch(SQLException ex){
               StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                //log.error(ex+errors.toString());
        }
        hsr1.setContentType("application/json");
        hsr1.setCharacterEncoding("ISO-8859-1"); 
        mv.addObject("gradelevels", grades);
        return mv;
    }
    
    /**
     * Esta funcion cambio el select, este puede ser select de grados
     * o select de clases.
     * @param id
     * @param hsr
     * @param hsr1
     * @return
     * @throws Exception 
     */
    @RequestMapping("/enviarmensaje/filter.htm")
    @ResponseBody
    public String filterSelect (@RequestParam("seleccion") String id,HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        List <Level> grades = new ArrayList();
        User u = (User)hsr.getSession().getAttribute("user");
        //SELECT DE GRADOS
        if(id.equals("0"))
           try{
                ResultSet rs = Homepage.st2.executeQuery("SELECT GradeLevel,GradeLevelID FROM GradeLevels");
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
        //SELECT DE CLASES
        else
           try{
            int termId = 1, yearId = 1;
            ResultSet rs = Homepage.st2.executeQuery("select defaultyearid,defaulttermid from ConfigSchool where configschoolid = 1");
            while (rs.next()) {
                termId = rs.getInt("defaulttermid");
                yearId = rs.getInt("defaultyearid");
            }
            String consulta = "";
            if(u.getType()==2 || u.getType()==0)   
                consulta = "SELECT * FROM Classes where (StaffID="+u.getId()
                        +" or AltStaffID="+u.getId()+" or AidID="+u.getId()
                        + ") and "+"yearid="+yearId;
            else
                consulta = "SELECT * FROM Classes where "+"yearid="+yearId;       
            rs = Homepage.st2.executeQuery(consulta);
            Level l = new Level();
            l.setName("Select class");
            grades.add(l);
            while(rs.next())
            {
                Level x = new Level();
                 String[] ids = new String[1];
                 ids[0]=""+rs.getInt("ClassID");
                x.setId(ids);
                x.setName(rs.getString("Name") + " "+ rs.getString("Section"));
            grades.add(x);
            }
           }catch(SQLException ex){
               StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                //log.error(ex+errors.toString());
           } 
        hsr1.setContentType("application/json");
        hsr1.setCharacterEncoding("ISO-8859-1"); 
        return new Gson().toJson(grades);
    }

    @RequestMapping("/enviarmensaje/studentclassLevel.htm")
    @ResponseBody
    public String studentclassLevel(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        List <Students> studentsgrades = new ArrayList();
        String[] levelid = hsr.getParameterValues("nivel");
        studentsgrades =this.getStudentsclass(levelid[0]);
        char c = studentsgrades.get(5).getNombre_students().charAt(8);
        String pr = Integer.toHexString((int) c);
        String data=new Gson().toJson(studentsgrades);
        hsr1.setContentType("application/json");
        hsr1.setCharacterEncoding("ISO-8859-1"); 
        return data;
    }
    
    /**
     * Devuelve las clases de un determinado grado.
     * @param gradeid
     * @return
     * @throws SQLException 
     */
    public ArrayList<Students> getStudentsclass(String gradeid) throws SQLException
    {         
        ArrayList<Students> listaAlumnos = new ArrayList<>();
        try {
            int termId = 1, yearId = 1;
            ResultSet rs = Homepage.st2.executeQuery("select defaultyearid,defaulttermid from ConfigSchool where configschoolid = 1");
            while (rs.next()) {
                termId = rs.getInt("defaulttermid");
                yearId = rs.getInt("defaultyearid");
            }
            rs= Homepage.st2.executeQuery("select * from Roster inner join Students on "
                     + "Roster.StudentID = Students.StudentID where Roster.ClassID="+gradeid+" and Roster.enrolled"+termId+"='1'");
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
    
    /**
     * Lista de grados dado un estudiante.
     * @param hsr
     * @param hsr1
     * @return
     * @throws Exception 
     */
    @RequestMapping("/enviarmensaje/studentlistLevel.htm")
    @ResponseBody
    public String studentlistLevel(HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {

        long time_start, time_end;
        time_start = System.currentTimeMillis();
              
//        DriverManagerDataSource dataSource;
//        dataSource = (DriverManagerDataSource)this.getBean("dataSourceAH",hsr.getServletContext());
//        this.cn = dataSource.getConnection();
        List <Students> studentsgrades = new ArrayList();
        String[] levelid = hsr.getParameterValues("nivel");
        String test = hsr.getParameter("levelStudent");
        studentsgrades =this.getStudentslevel(levelid[0]);
        String data=new Gson().toJson(studentsgrades);
        
        time_end = System.currentTimeMillis();
        System.out.println("the task has taken "+ ( time_end - time_start ) +" milliseconds");
        hsr1.setContentType("application/json");
        hsr1.setCharacterEncoding("ISO-8859-1"); 
        return data;
    }
    
    /**
     * 
     * @param gradeid
     * @return
     * @throws SQLException 
     */
    public ArrayList<Students> getStudentslevel(String gradeid) throws SQLException
    {         
        ArrayList<Students> listaAlumnos = new ArrayList<>();
        String gradelevel = null;
        try {
            ResultSet rs1= Homepage.st2.executeQuery("select GradeLevel from GradeLevels where GradeLevelID ="+gradeid);
             while(rs1.next())
             {
             gradelevel = rs1.getString("GradeLevel");
             }
           
            String consulta = "SELECT * FROM Students where Status = 'Enrolled' and GradeLevel = '"+gradelevel+"'";
            ResultSet rs = Homepage.st2.executeQuery(consulta);
          
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
    
    /**
     * Crea una carpeta.
     * @param st
     * @param id
     * @param nombre
     * @return
     * @throws SQLException 
     */
    public static String createFolder(Statement st,String id,String nombre) throws SQLException{
        st.executeUpdate("insert into folder(idpersona,nombre) values ("+id+",'"+nombre+"')",
                          Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = st.getGeneratedKeys();
        if(rs.next())
            return rs.getString(1);
        else 
            return null;
    }
    
    
    /**
     * Quita los asteriscos del nombre, 
     * ya que estos dan problemas en la base de datos
     * @param name
     * @return 
     */
    public static String limpiarFromName(String name){
        if(name.contains("*") && name.indexOf("*") < name.length()-1){
            name = name.substring(0, name.indexOf("*"))+limpiarFromName(name.substring(name.indexOf("*")+1));
        }else if(name.indexOf("*") >= name.length()-1){
            name = name.substring(0,name.length()-1);
        }
        return name;
    }
    
    
    /**
     * Envia un mensaje a los custody que tenga un alumno concreto
     * @param hsr
     * @param hsr1
     * @return
     * @throws Exception 
     */
    @RequestMapping("/enviarmensaje/enviar.htm")
    public ModelAndView enviar( HttpServletRequest hsr, HttpServletResponse hsr1) throws Exception {
        ModelAndView mv = Homepage.checklogin(hsr);
         if(mv!=null)
             return mv;  
        
        String from = ""+((User)hsr.getSession().getAttribute("user")).getId();
        String[] destinationListAux = hsr.getParameterValues("destino[]");
        String asunto = hsr.getParameter("asunto");
        String text = hsr.getParameter("NotificationMessage");
        String parentid = hsr.getParameter("parentid");
        String msgid = "";
        String consulta;
        String fromName = "error not found";
        ResultSet name = Homepage.st2.executeQuery("select * from Person where PersonID="+from);
        if(name.next())
            fromName = name.getString("firstname")+" "+name.getString("LastName");
        fromName = limpiarFromName(fromName);
        if(asunto.equals("") || asunto.length()>30 || text.equals("") || destinationListAux==null){
            mv = new ModelAndView("enviarmensaje");
            mv.addObject("error", "error");
            return mv;
        }
            
        Mensaje m;
        User u = (User)hsr.getSession().getAttribute("user");
        
        ArrayList<String> destinationList = new ArrayList<>();
        ArrayList<String> destinationEmails = new ArrayList<>();
        ArrayList<String> folderList = new ArrayList<>();
        Calendar t = Calendar.getInstance();
        
        String time = t.get(Calendar.YEAR)+ "-" +(t.get(Calendar.MONTH)+1)+
                    "-"+t.get(Calendar.DAY_OF_MONTH)+" "+t.get(Calendar.HOUR_OF_DAY)+":"+
                    t.get(Calendar.MINUTE)+":"+t.get(Calendar.SECOND);
        for(String dest:destinationListAux){
            consulta = "select ps.parentid, ps.relationship, p.firstname as name, p.lastname as lname, ISNULL(p.Email, 0) as mail"
                    + " from parent_student ps"
                    + " inner join person p"
                    + " on p.personid = ps.parentid" 
                    + " where ps.studentid ="+dest
                    + " and ps.Custody = 1";
            ResultSet rs = Homepage.st2.executeQuery(consulta);
            while(rs.next()){
                String mail = rs.getString("mail");
                String destId = rs.getString(1);
                if(mail != null){
                    destinationList.add(destId);
                    destinationEmails.add(mail);
                } else
                    ActivityLog.nuevaEntrada(from,fromName,destId, "no email", "El custody no tiene corrreo");
            }
        }
        for(String dest:destinationList){
          ResultSet rs = Homepage.st.executeQuery("select * from folder where idpersona="+dest+" and nombre='Inbox'");
          if(rs.next())
              folderList.add(rs.getString("idfolder"));
          else
              folderList.add(createFolder(Homepage.st,dest,"Inbox"));
        }
        ResultSet rs3 = Homepage.st.executeQuery("select * from folder where idpersona="+u.getId()+" and nombre='Sent'");
        if(rs3.next())
            folderList.add(rs3.getString("idfolder"));
        else
            folderList.add(createFolder(Homepage.st,""+u.getId(),"Sent"));
        if(parentid==null)
            consulta = "insert into mensaje (parentid,fecha,prio,asunto,texto) values ("
                    +((User)hsr.getSession().getAttribute("user")).getId()+
                    ", '"+time+"' ,1,'"+asunto+"','"+text+"')";
        else
            consulta = "insert into mensaje (parentid,fecha,prio,asunto,texto) values ("
                    +((User)hsr.getSession().getAttribute("user")).getId()+
                    ", '"+time+"' ,"+parentid+",'"+asunto+"','"+text+"')";
        Homepage.st.executeUpdate(consulta,Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = Homepage.st.getGeneratedKeys();
        if(rs.next())
            msgid = ""+rs.getInt(1);
        for(String f:folderList){
            Homepage.st.executeUpdate("insert into msg_folder values("+msgid+","+f+")");
        }
        
        for(int i = 0;i<destinationList.size();i++){
            consulta = "insert into msg_from_to(msgid,msfrom,msto,fromname) values("+msgid+","+from+","
                    +destinationList.get(i)+",'"+fromName+"')"; 
            Homepage.st.executeUpdate(consulta);
        }
        if(parentid!=null)
            m = new Mensaje(fromName,"Mensaje de "+fromName+": "+asunto,text,Integer.parseInt(parentid),1);
        else
            m = new Mensaje(fromName,"Mensaje de "+fromName+": "+asunto,text,0,1);
        m.setDestinatarios(destinationEmails);
        try{
            SendMail.SendMail(m,from);
        }catch(Exception e){
            mv = new ModelAndView("enviarmensaje");
            mv.addObject("error", "error");
            return mv;
        }
        return new ModelAndView("redirect:/menu/start.htm?folder=null");
    }
    
    
    /**
     * Coge la lista de estudiantes de renweb.
     * @return
     * @throws SQLException 
     */
    public ArrayList<Students> getStudents() throws SQLException
    {
//        this.conectarOracle();
        ArrayList<Students> listaAlumnos = new ArrayList<>();
        try {
            String consulta = "SELECT * FROM Students where Status = 'Enrolled' order by lastname";
            ResultSet rs = Homepage.st2.executeQuery(consulta);
            
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
          
            
        } catch (SQLException ex) {
            System.out.println("Error leyendo Alumnos: " + ex);
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
        }
        return listaAlumnos;
    }
    
    public static String convertFromUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
    
    
}
