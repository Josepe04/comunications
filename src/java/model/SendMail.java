/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author nmohamed
 */
public class SendMail {
    public static void SendMail(Mensaje m, String from) throws MessagingException {
        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//        props.put("mail.user", "nmohamed@eduwebgroup.com");
//        props.put("mail.password", "kokowawa1");
        String host = "smtp.office365.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", "commapp@colegioalemannk.org");
        props.put("mail.smtp.password", "Tut62672");
        props.put("mail.smtp.port", "587");//587
        props.put("mail.smtp.auth", "true");
//     
        Session session = Session.getDefaultInstance(props);
        for(String dest : m.getDestinatarios()){
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("commapp@colegioalemannk.org"));
                // put here if reciepient is not empty, incase the parent doe snot have an email on renweb

                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dest));//dest
                message.setSubject(m.getAsunto());
                message.setContent(m.getTexto()+"<p> Este mensaje ha sido enviado por un sistema automático <br>"
                        + "Por favor no respondas directamente a este email.</p>", "text/html; charset=utf-8");
            //    message.setText(m.getBody());


    //            Transport.send(message);

                Transport transport = session.getTransport("smtp");
//                transport.connect(host, "info.ca.pan2018", "Kokowawa1");
                transport.connect(host, "commapp@colegioalemannk.org", "Tut62672");
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
                System.out.println("Sent message successfully....");
              //  Class.forName("org.postgresql.Driver");
              //  Connection cn = DriverManager.getConnection("jdbc:postgresql://192.168.1.3:5432/Maintenance_jobs?user=eduweb&password=Madrid2016");
            //ActivityLog.log(m.getJob_id(),m.getRw_event_id(),m.getRecipient(),m.getBody(), cn);
//            Statement st = cn.createStatement();
            //st.executeUpdate("update jobs set lastrun = now() where id ="+m.getJob_id());
                ActivityLog.nuevaEntrada(from ,m.getSender(),"correo: "+dest ,"mensaje enviado", "mensaje enviado correctamente");
            }catch (MessagingException e) {
                ActivityLog.nuevaEntrada(from ,m.getSender(),"correo: "+dest ,"fallo", "mensaje no enviado");
                throw e;
            }
        }
    }
}
