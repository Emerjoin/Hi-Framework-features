package com.web.yayee.notification;

import com.google.gson.Gson;
import com.web.yayee.users.Sessions;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

/**
 * Created by Mario Junior.
 */


@ServerEndpoint("/push-web-socket-end-point")
public class WebSocket {


    private static Map<String,Session> sessionsUsers = new HashMap<String, Session>();
    private static Map<String,Set<Session>> sessionsGroups = new HashMap<String, Set<Session>>();


    protected static void tellGroups(String what, String[] groups){

        if(what==null||groups==null)
            return;

        if(what.trim().equals(""))
            return;


        for(String group: groups){

            if(group.trim().equals(""))
                continue;

            tellGroup(what,group);

        }


    }

    protected static void tellGroup(String what, String group){

        if(what==null||group==null)
            return;


        if(what.trim().equals("")||group.trim().equals(""))
            return;

        synchronized (sessionsGroups){

            Set<Session> sessions = sessionsGroups.get(group);

            if(sessions!=null){

                for(Session session : sessions){

                    if(session.isOpen())
                        session.getAsyncRemote().sendText(what);

                }

            }

        }

    }

    protected static void tellUser(String what, String user){

        synchronized (sessionsUsers){

            Session session = sessionsUsers.get(user);

            if(session!=null) {

                if(session.isOpen())
                    session.getAsyncRemote().sendText(what);

            }

        }

    }

    protected static void tellAllUsers(String what){

        if(what==null)
            return;

        if(what.trim().equals(""))
            return;

        synchronized (sessionsUsers){

            Collection<Session> sessions = sessionsUsers.values();

            for(Session session: sessions){

                if(session.isOpen())
                    session.getAsyncRemote().sendText(what);

            }


        }

    }

    @OnOpen
    public void onOpen(Session session){

        try {


            Principal userPrincipal = session.getUserPrincipal();
            if(userPrincipal==null){

                Map error = new HashMap<>();
                error.put("error","true");

                session.getBasicRemote().sendText(new Gson().toJson(error));

                try {

                    Thread.sleep(4000);

                }catch (Exception ex){



                }

                try {

                    session.close();

                }catch (Exception ex){



                }

                return;

            }else{

                Map error = new HashMap<>();
                error.put("success","true");
                session.getBasicRemote().sendText(new Gson().toJson(error));

            }

            synchronized (sessionsUsers) {

                sessionsUsers.put(userPrincipal.getName(), session);

            }

            String groups[] = UserGroups.getGroups(userPrincipal.getName());

            if(groups==null)
                return;

            synchronized (sessionsGroups){

                for(String group : groups){

                    if(!session.isOpen()) {

                        break;

                    }

                    Set<Session> groupSessions = sessionsGroups.get(group);
                    if(groupSessions==null){

                        groupSessions = new HashSet<Session>();

                    }

                    groupSessions.add(session);
                    sessionsGroups.put(group,groupSessions);

                }


            }

            session.getBasicRemote().sendText("Connection Established. Welcome.");


        } catch (IOException ex) {


            ex.printStackTrace();

        }
    }

    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String message, Session session){

        try {



            session.getBasicRemote().sendText("You sent me this : "+ message);


        } catch (IOException ex) {

            ex.printStackTrace();

        }
    }

    /**
     * The user closes the connection.
     *
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){


        Principal userPrincipal = session.getUserPrincipal();
        if(userPrincipal==null)
            return;

        synchronized (sessionsUsers){

            sessionsUsers.remove(session);

        }

        String[] groups = UserGroups.getGroups(userPrincipal.getName());

        if(groups==null)
            return;


        synchronized (sessionsGroups){


            for(String group : groups){


                Set<Session> groupSessions = sessionsGroups.get(group);
                if(groupSessions!=null)
                    groupSessions.remove(session);

            }


        }

    }

}
